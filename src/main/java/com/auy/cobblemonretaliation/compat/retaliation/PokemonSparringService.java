package com.auy.cobblemonretaliation.compat.retaliation;

import com.auy.cobblemonretaliation.compat.rct.RctBattleBridge;
import com.jvn.villagerretaliation.dialogue.DialogueContext;
import com.jvn.villagerretaliation.dialogue.resources.VillagerDialogueResources;
import com.jvn.villagerretaliation.interaction.VillagerConversationService;
import com.jvn.villagerretaliation.interaction.VillagerInteractionService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;

import java.util.List;
import java.util.UUID;

public final class PokemonSparringService {

    public static final String SPARRING_OPTION_ID =
            "cobblemonretaliation.pokemon_spar";

    private static final String LEGACY_SPARRING_OPTION_ID =
            "cobblemon_spar";

    /*
     * Villager Retaliation datapack message keys.
     */
    private static final String ACCEPT_MESSAGE =
            "cobblemonretaliation.pokemon_spar.accept";

    private static final String UNAVAILABLE_MESSAGE =
            "cobblemonretaliation.pokemon_spar.unavailable";

    /*
     * These are only used if the datapack message
     * cannot be found for some reason.
     */
    private static final List<String> ACCEPT_FALLBACKS =
            List.of(
                    "All right. Let's see what your Pokemon can do.",
                    "A friendly battle? I'm ready.",
                    "Very well. Show me how you battle.",
                    "Let's see which of us has trained harder.",
                    "Sounds good. Let's have a proper Pokemon battle.",
                    "I've been waiting for a chance to test my team.",
                    "Don't expect me to go easy on you.",
                    "Let's make this a good battle."
            );

    private static final List<String> UNAVAILABLE_FALLBACKS =
            List.of(
                    "I can't spar right now.",
                    "Perhaps another time.",
                    "I'm not ready for a battle right now.",
                    "We'll have to battle later."
            );

    private PokemonSparringService() {
    }

    public static boolean isSparringOption(
            String optionId
    ) {

        return SPARRING_OPTION_ID.equals(optionId)
                ||
                LEGACY_SPARRING_OPTION_ID.equals(optionId);
    }

    /**
     * Called when the player chooses our sparring
     * dialogue option.
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
         * Save the ID because the conversation is
         * about to be closed.
         */
        int villagerEntityId =
                villager.getId();

        /*
         * Close the Villager Retaliation screen.
         */
        VillagerConversationService
                .endForPlayer(
                        player,
                        true
                );

        /*
         * Run battle preparation after VR finishes
         * handling the dialogue packet.
         */
        player.getServer().execute(
                () -> prepareBattle(
                        player,
                        villagerEntityId
                )
        );
    }

    private static void prepareBattle(
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

        /*
         * Basic villager validation.
         */
        if (!villager.isAlive()
                || villager.isBaby()) {

            sendDialogueMessage(
                    player,
                    villager,
                    UNAVAILABLE_MESSAGE,
                    randomUnavailableFallback(player)
            );

            return;
        }

        /*
         * Check obvious battle conflicts BEFORE
         * the villager agrees to spar.
         */
        if (RctBattleBridge.isVillagerBusy(villager)
                ||
                RctBattleBridge.isPlayerInPokemonBattle(player)) {

            sendDialogueMessage(
                    player,
                    villager,
                    UNAVAILABLE_MESSAGE,
                    randomUnavailableFallback(player)
            );

            return;
        }

        /*
         * IMPORTANT:
         *
         * Send the villager's randomly selected
         * pre-battle line BEFORE starting RCT.
         */
        sendDialogueMessage(
                player,
                villager,
                ACCEPT_MESSAGE,
                randomAcceptFallback(player)
        );

        /*
         * Now start the actual Pokemon battle.
         */
        UUID battleId =
                RctBattleBridge
                        .startBattle(
                                player,
                                villager
                        );

        if (battleId == null) {

            /*
             * Something unexpected prevented RCT
             * from starting after the preliminary
             * checks passed.
             */
            sendDialogueMessage(
                    player,
                    villager,
                    UNAVAILABLE_MESSAGE,
                    randomUnavailableFallback(player)
            );
        }
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

    private static String randomAcceptFallback(
            ServerPlayer player
    ) {

        return ACCEPT_FALLBACKS.get(
                player
                        .getRandom()
                        .nextInt(
                                ACCEPT_FALLBACKS.size()
                        )
        );
    }

    private static String randomUnavailableFallback(
            ServerPlayer player
    ) {

        return UNAVAILABLE_FALLBACKS.get(
                player
                        .getRandom()
                        .nextInt(
                                UNAVAILABLE_FALLBACKS.size()
                        )
        );
    }
}