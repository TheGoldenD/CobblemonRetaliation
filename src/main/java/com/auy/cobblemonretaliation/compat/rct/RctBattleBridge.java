package com.auy.cobblemonretaliation.compat.rct;

import com.auy.cobblemonretaliation.CobblemonRetaliation;
import com.auy.cobblemonretaliation.villager.VillagerPokemonManager;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.gitlab.srcmc.rctapi.api.RCTApi;
import com.gitlab.srcmc.rctapi.api.battle.BattleFormat;
import com.gitlab.srcmc.rctapi.api.battle.BattleFormatProvider;
import com.gitlab.srcmc.rctapi.api.battle.BattleRules;
import com.gitlab.srcmc.rctapi.api.battle.BattleState;
import com.gitlab.srcmc.rctapi.api.events.Event;
import com.gitlab.srcmc.rctapi.api.events.Events;
import com.gitlab.srcmc.rctapi.api.trainer.TrainerNPC;
import com.gitlab.srcmc.rctapi.api.trainer.TrainerPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class RctBattleBridge {

    private static final RCTApi RCT =
            RCTApi.initInstance(
                    CobblemonRetaliation.MOD_ID
            );

    /*
     * Battle UUID -> active battle information.
     */
    private static final Map<UUID, ActiveVillagerBattle>
            ACTIVE_BATTLES =
            new HashMap<>();

    /*
     * Villager UUID -> Battle UUID.
     */
    private static final Map<UUID, UUID>
            ACTIVE_VILLAGER_BATTLES =
            new HashMap<>();

    /*
     * Player UUID -> Battle UUID.
     *
     * Used by the safety handler so nearby villagers
     * cannot physically attack a player while that
     * player is in one of our villager Pokemon battles.
     */
    private static final Map<UUID, UUID>
            ACTIVE_PLAYER_BATTLES =
            new HashMap<>();

    private static boolean bootstrapped =
            false;

    private RctBattleBridge() {
    }

    public static void bootstrap() {

        if (bootstrapped) {
            return;
        }

        RCT.getEventContext()
                .register(
                        Events.BATTLE_ENDED,
                        RctBattleBridge::onBattleEnded
                );

        bootstrapped = true;

        CobblemonRetaliation.LOGGER.info(
                "[CobblemonRetaliation] RCT bridge initialized."
        );
    }

    public static void onServerStarting(
            ServerStartingEvent event
    ) {

        RCT.getTrainerRegistry()
                .init(
                        event.getServer()
                );

        ACTIVE_BATTLES.clear();
        ACTIVE_VILLAGER_BATTLES.clear();
        ACTIVE_PLAYER_BATTLES.clear();

        CobblemonRetaliation.LOGGER.info(
                "[CobblemonRetaliation] RCT trainer registry initialized."
        );
    }

    public static void onServerStopped(
            ServerStoppedEvent event
    ) {

        ACTIVE_BATTLES.clear();
        ACTIVE_VILLAGER_BATTLES.clear();
        ACTIVE_PLAYER_BATTLES.clear();

        RCT.getTrainerRegistry()
                .clear();
    }

    public static boolean isInPokemonBattle(
            Villager villager
    ) {

        if (villager == null) {
            return false;
        }

        return ACTIVE_VILLAGER_BATTLES
                .containsKey(
                        villager.getUUID()
                );
    }

    public static boolean isPlayerInPokemonBattle(
            ServerPlayer player
    ) {

        if (player == null) {
            return false;
        }

        return ACTIVE_PLAYER_BATTLES
                .containsKey(
                        player.getUUID()
                );
    }

    public static boolean isVillagerBusy(
            Villager villager
    ) {

        if (villager == null) {
            return false;
        }

        return isInPokemonBattle(villager)
                ||
                RCT.getTrainerRegistry()
                        .getId(
                                villager
                        )
                        != null;
    }

    public static UUID startBattle(
            ServerPlayer player,
            Villager villager
    ) {

        if (player == null
                || villager == null
                || villager.isBaby()
                || !villager.isAlive()) {

            return null;
        }

        /*
         * A player may only participate in one
         * CobblemonRetaliation villager battle at once.
         */
        if (isPlayerInPokemonBattle(player)) {
            return null;
        }

        if (isVillagerBusy(villager)) {
            return null;
        }

        List<Pokemon> persistentTeam =
                VillagerPokemonManager
                        .getOrCreateTeam(
                                villager
                        );

        if (persistentTeam.isEmpty()) {
            return null;
        }

        String trainerId =
                createTrainerId(
                        villager
                );

        /*
         * Defensive duplicate check.
         */
        if (RCT.getTrainerRegistry()
                .getById(
                        trainerId
                )
                != null) {

            CobblemonRetaliation.LOGGER.warn(
                    "[CobblemonRetaliation] Temporary RCT trainer already exists: {}",
                    trainerId
            );

            return null;
        }

        TrainerNPC villagerTrainer =
                RctTrainerFactory
                        .createTrainer(
                                villager,
                                persistentTeam
                        );

        RCT.getTrainerRegistry()
                .registerNPC(
                        trainerId,
                        villagerTrainer
                );

        /*
         * Attach the actual Villager entity to the
         * temporary RCT trainer immediately before battle.
         */
        villagerTrainer.setEntity(
                villager
        );

        TrainerPlayer playerTrainer =
                new TrainerPlayer(
                        player
                );

        BattleRules rules =
                new BattleRules.Builder()
                        .withHealPlayers(false)
                        .withAdjustPlayerLevels(false)
                        .withAdjustNPCLevels(false)
                        .build();

        UUID battleId =
                RCT.getBattleManager()
                        .startBattle(
                                List.of(
                                        playerTrainer
                                ),
                                List.of(
                                        villagerTrainer
                                ),
                                (BattleFormatProvider)
                                        BattleFormat.GEN_9_SINGLES,
                                rules
                        );

        /*
         * RCT rejected battle startup.
         */
        if (battleId == null) {

            RCT.getTrainerRegistry()
                    .unregisterById(
                            trainerId
                    );

            CobblemonRetaliation.LOGGER.warn(
                    "[CobblemonRetaliation] Failed to start Pokemon battle with villager {}.",
                    villager.getUUID()
            );

            return null;
        }

        ActiveVillagerBattle activeBattle =
                new ActiveVillagerBattle(
                        trainerId,
                        villager.getUUID(),
                        player.getUUID()
                );

        ACTIVE_BATTLES.put(
                battleId,
                activeBattle
        );

        ACTIVE_VILLAGER_BATTLES.put(
                villager.getUUID(),
                battleId
        );

        ACTIVE_PLAYER_BATTLES.put(
                player.getUUID(),
                battleId
        );

        CobblemonRetaliation.LOGGER.info(
                "[CobblemonRetaliation] Started battle {} between villager {} and player {}.",
                battleId,
                villager.getUUID(),
                player.getUUID()
        );

        return battleId;
    }

    private static void onBattleEnded(
            Event<BattleState> event
    ) {

        UUID battleId =
                event
                        .getValue()
                        .getBattle()
                        .getBattleId();

        ActiveVillagerBattle activeBattle =
                ACTIVE_BATTLES.remove(
                        battleId
                );

        /*
         * Ignore battles that do not belong to us.
         */
        if (activeBattle == null) {
            return;
        }

        ACTIVE_VILLAGER_BATTLES.remove(
                activeBattle.villagerId()
        );

        ACTIVE_PLAYER_BATTLES.remove(
                activeBattle.playerId()
        );

        RCT.getTrainerRegistry()
                .unregisterById(
                        activeBattle.trainerId()
                );

        CobblemonRetaliation.LOGGER.info(
                "[CobblemonRetaliation] Removed temporary RCT trainer {} after battle {}.",
                activeBattle.trainerId(),
                battleId
        );
    }

    private static String createTrainerId(
            Villager villager
    ) {

        return "villager_"
                + villager.getUUID();
    }

    private record ActiveVillagerBattle(
            String trainerId,
            UUID villagerId,
            UUID playerId
    ) {
    }
}