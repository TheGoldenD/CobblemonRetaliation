package com.auy.cobblemonretaliation.compat.retaliation;

import com.auy.cobblemonretaliation.compat.rct.RctBattleBridge;
import com.jvn.villagerretaliation.dialogue.DialogueContext;
import com.jvn.villagerretaliation.dialogue.resources.VillagerDialogueResources;
import com.jvn.villagerretaliation.interaction.VillagerConversationService;
import com.jvn.villagerretaliation.interaction.VillagerInteractionService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;

import java.util.UUID;

public final class PokemonSparringService {

    public static final String SPARRING_OPTION_ID =
            "cobblemonretaliation.pokemon_spar";

    private static final String ACCEPT_MESSAGE =
            "cobblemonretaliation.pokemon_spar.accept";

    private static final String UNAVAILABLE_MESSAGE =
            "cobblemonretaliation.pokemon_spar.unavailable";

    private PokemonSparringService() {
    }

    /**
     * Called after the player selects our Villager
     * Retaliation dialogue option.
     */
    public static void startFromDialogue(
            ServerPlayer player,
            Villager villager
    ) {

        if (player == null
                || villager == null) {

            return;
        }

        /*
         * Save the entity ID before closing the
         * conversation.
         */
        int villagerEntityId =
                villager.getId();

        /*
         * Close VR's interaction screen first.
         *
         * notifyClient = true makes VR tell the
         * client that the conversation has ended.
         */
        VillagerConversationService
                .endForPlayer(
                        player,
                        true
                );

        /*
         * Queue battle startup after the current
         * dialogue-request handling has finished.
         *
         * This prevents the VR conversation screen
         * and Cobblemon battle UI from fighting over
         * the same interaction packet.
         */
        player.getServer().execute(
                () -> startQueued(
                        player,
                        villagerEntityId
                )
        );
    }

    private static void startQueued(
            ServerPlayer player,
            int villagerEntityId
    ) {

        Entity entity =
                player
                        .serverLevel()
                        .getEntity(
                                villagerEntityId
                        );

        if (!(entity instanceof Villager villager)) {

            return;
        }

        if (!villager.isAlive()
                || villager.isBaby()) {

            sendDialogueMessage(
                    player,
                    villager,
                    UNAVAILABLE_MESSAGE,
                    "I can't spar right now."
            );

            return;
        }

        /*
         * startBattle() already handles:
         *
         * - lazy team generation
         * - villager busy checks
         * - player battle checks
         * - temporary RCT trainer creation
         * - persistent Pokemon copying
         */
        UUID battleId =
                RctBattleBridge
                        .startBattle(
                                player,
                                villager
                        );

        if (battleId == null) {

            sendDialogueMessage(
                    player,
                    villager,
                    UNAVAILABLE_MESSAGE,
                    "I can't spar right now."
            );

            return;
        }

        sendDialogueMessage(
                player,
                villager,
                ACCEPT_MESSAGE,
                "All right. Let's have a friendly Pokemon battle."
        );
    }

    private static void sendDialogueMessage(
            ServerPlayer player,
            Villager villager,
            String messageKey,
            String fallback
    ) {

        DialogueContext context =
                VillagerInteractionService
                        .createDialogueContext(
                                player.serverLevel(),
                                player,
                                villager
                        );

        String message =
                VillagerDialogueResources
                        .message(
                                context,
                                messageKey
                        )
                        .orElse(
                                fallback
                        );

        VillagerInteractionService
                .sendPersonalVillagerChat(
                        player,
                        villager,
                        message
                );
    }
}