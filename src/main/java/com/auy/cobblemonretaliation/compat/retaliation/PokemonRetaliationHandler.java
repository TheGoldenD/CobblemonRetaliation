package com.auy.cobblemonretaliation.compat.retaliation;

import com.auy.cobblemonretaliation.CobblemonRetaliation;
import com.auy.cobblemonretaliation.compat.rct.RctBattleBridge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.UUID;

public final class PokemonRetaliationHandler {

    /*
     * Only searched once when the Pokemon battle starts.
     *
     * This is NOT a repeating/ticking scan.
     */
    private static final double
            WITNESS_SUPPRESSION_RADIUS =
            32.0D;

    private PokemonRetaliationHandler() {
    }

    public static void onLivingDamagePost(
            LivingDamageEvent.Post event
    ) {

        /*
         * Ignore attacks which ultimately caused
         * no damage.
         */
        if (event.getNewDamage() <= 0.0F) {
            return;
        }

        /*
         * Cobblemon Nurses are still normal Villager
         * entities, so this also catches them.
         */
        if (!(event.getEntity()
                instanceof Villager villager)) {

            return;
        }

        /*
         * No Pokemon retaliation battles for babies
         * or dead villagers.
         */
        if (villager.isBaby()
                || !villager.isAlive()) {

            return;
        }

        Entity attacker =
                event.getSource()
                        .getEntity();

        /*
         * For now only PLAYER aggression becomes
         * a Pokemon battle.
         *
         * Zombies / hostile mobs etc. remain handled
         * normally by Villager Retaliation.
         */
        if (!(attacker
                instanceof ServerPlayer player)) {

            return;
        }

        /*
         * If the villager is already fighting this
         * Pokemon battle and somehow gets hit again,
         * make sure VR does not restore its physical
         * retaliation target.
         */
        if (RctBattleBridge
                .isInPokemonBattle(
                        villager
                )) {

            VillagerRetaliationCompat
                    .suppressPhysicalRetaliation(
                            villager
                    );

            return;
        }

        /*
         * Let Villager Retaliation make the decision.
         *
         * If VR decides this villager should flee,
         * ignore the player, etc., we do NOT force
         * a Pokemon battle.
         */
        if (!VillagerRetaliationCompat
                .isRetaliatingAgainst(
                        villager,
                        player
                )) {

            return;
        }

        /*
         * This lazily generates the persistent
         * Pokemon team if needed.
         */
        UUID battleId =
                RctBattleBridge
                        .startBattle(
                                player,
                                villager
                        );

        /*
         * RCT could not start the battle.
         *
         * Leave normal Villager Retaliation active
         * as our fallback.
         */
        if (battleId == null) {

            CobblemonRetaliation.LOGGER.debug(
                    "[CobblemonRetaliation] Pokemon retaliation "
                            + "battle could not start for villager {}. "
                            + "Normal physical retaliation remains.",
                    villager.getUUID()
            );

            return;
        }

        /*
         * The Pokemon battle successfully started.
         *
         * Clear physical retaliation from:
         *
         * - the attacked villager
         * - any nearby witness villagers targeting
         *   this player
         *
         * Reputation/social consequences remain.
         */
        int suppressed =
                VillagerRetaliationCompat
                        .suppressNearbyRetaliation(
                                player,
                                villager,
                                WITNESS_SUPPRESSION_RADIUS
                        );

        CobblemonRetaliation.LOGGER.info(
                "[CobblemonRetaliation] Villager {} replaced "
                        + "physical retaliation against player {} "
                        + "with Pokemon battle {}. "
                        + "Suppressed {} physical villager "
                        + "retaliation target(s).",
                villager.getUUID(),
                player.getUUID(),
                battleId,
                suppressed
        );
    }
}