package com.auy.cobblemonretaliation.event;

import com.auy.cobblemonretaliation.compat.rct.RctBattleBridge;
import com.auy.cobblemonretaliation.compat.retaliation.VillagerRetaliationCompat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public final class PokemonBattleSafetyHandler {

    private PokemonBattleSafetyHandler() {
    }

    /**
     * Freeze the villager which is actively acting as an
     * RCT Pokemon trainer.
     *
     * This is important because Villager Retaliation has
     * its own per-tick combat system. Even after its current
     * retaliation target is cleared, it can reacquire a
     * hostile player from its own combat/reputation logic.
     *
     * Cancelling the villager's tick prevents:
     *
     * - melee attacks
     * - thrown food / harassment
     * - bows / crossbows
     * - potion combat
     * - navigation
     * - combat target reacquisition
     *
     * while the Pokemon battle is active.
     */
    public static void onEntityTickPre(
            EntityTickEvent.Pre event
    ) {

        if (!(event.getEntity()
                instanceof Villager villager)) {

            return;
        }

        if (villager.level().isClientSide) {
            return;
        }

        if (!RctBattleBridge
                .isInPokemonBattle(
                        villager
                )) {

            return;
        }

        /*
         * Make absolutely sure VR has no remaining
         * physical retaliation state before freezing.
         */
        VillagerRetaliationCompat
                .suppressPhysicalRetaliation(
                        villager
                );

        /*
         * NeoForge 1.21.1 EntityTickEvent.Pre is
         * cancellable.
         *
         * The villager entity remains in the world,
         * but does not perform its normal server tick.
         *
         * RCT/Cobblemon's battle simulation is separate.
         */
        event.setCanceled(
                true
        );
    }

    /**
     * Prevent villagers from acquiring a player as a
     * normal Minecraft combat target while that player
     * is already fighting one of our villager Pokemon
     * battles.
     */
    public static void onLivingChangeTarget(
            LivingChangeTargetEvent event
    ) {

        if (!(event.getEntity()
                instanceof Villager villager)) {

            return;
        }

        if (!(event
                .getNewAboutToBeSetTarget()
                instanceof ServerPlayer player)) {

            return;
        }

        if (!RctBattleBridge
                .isPlayerInPokemonBattle(
                        player
                )) {

            return;
        }

        /*
         * Prevent the vanilla target assignment.
         */
        event.setCanceled(
                true
        );

        /*
         * Also remove Villager Retaliation's internal
         * physical combat target.
         */
        VillagerRetaliationCompat
                .suppressPhysicalRetaliation(
                        villager
                );
    }

    /**
     * Final damage safety net.
     *
     * An attack/projectile may already have been launched
     * on the same tick that the Pokemon battle began.
     *
     * Only physical Minecraft damage directly attributed
     * to a Villager is blocked here.
     */
    public static void onLivingIncomingDamage(
            LivingIncomingDamageEvent event
    ) {

        if (!(event.getEntity()
                instanceof ServerPlayer player)) {

            return;
        }

        if (!RctBattleBridge
                .isPlayerInPokemonBattle(
                        player
                )) {

            return;
        }

        if (!(event.getSource()
                .getEntity()
                instanceof Villager)) {

            return;
        }

        event.setAmount(
                0.0F
        );
    }
}