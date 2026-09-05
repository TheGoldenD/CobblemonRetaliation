package com.auy.cobblemonretaliation.compat.retaliation;

import com.jvn.villagerretaliation.combat.VillagerRetaliationHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;

import java.util.List;

public final class VillagerRetaliationCompat {

    private VillagerRetaliationCompat() {
    }

    /**
     * Ask Villager Retaliation whether this villager
     * is currently retaliating against this target.
     *
     * This means Villager Retaliation remains responsible
     * for deciding whether retaliation is appropriate.
     */
    public static boolean isRetaliatingAgainst(
            Villager villager,
            LivingEntity target
    ) {

        return VillagerRetaliationHandler
                .hasRetaliationTarget(
                        villager,
                        target
                );
    }

    /**
     * Clears Villager Retaliation's immediate physical
     * combat target.
     *
     * This does NOT remove:
     *
     * - reputation loss
     * - gossip
     * - relationship changes
     * - crime consequences
     */
    public static void suppressPhysicalRetaliation(
            Villager villager
    ) {

        if (villager == null) {
            return;
        }

        VillagerRetaliationHandler
                .clearCustomTarget(
                        villager
                );
    }

    /**
     * Clears physical retaliation from nearby villagers
     * which are currently targeting this player.
     *
     * This search only occurs when a Pokemon retaliation
     * battle begins.
     *
     * It is NOT performed every tick.
     */
    public static int suppressNearbyRetaliation(
            ServerPlayer player,
            Villager center,
            double radius
    ) {

        if (player == null
                || center == null
                || !(center.level()
                instanceof ServerLevel level)) {

            return 0;
        }

        List<Villager> villagers =
                level.getEntitiesOfClass(
                        Villager.class,

                        center
                                .getBoundingBox()
                                .inflate(
                                        radius
                                ),

                        villager ->
                                villager.isAlive()
                                        &&
                                        VillagerRetaliationHandler
                                                .hasRetaliationTarget(
                                                        villager,
                                                        player
                                                )
                );

        for (Villager villager : villagers) {

            VillagerRetaliationHandler
                    .clearCustomTarget(
                            villager
                    );
        }

        return villagers.size();
    }
}