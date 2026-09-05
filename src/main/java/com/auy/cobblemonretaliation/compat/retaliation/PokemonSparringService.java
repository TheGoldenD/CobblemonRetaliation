package com.auy.cobblemonretaliation.compat.retaliation;

import com.auy.cobblemonretaliation.compat.rct.RctBattleBridge;
import com.jvn.villagerretaliation.dialogue.DialogueContext;
import com.jvn.villagerretaliation.dialogue.resources.VillagerDialogueResources;
import com.jvn.villagerretaliation.interaction.VillagerConversationService;
import com.jvn.villagerretaliation.interaction.VillagerInteractionService;
import com.jvn.villagerretaliation.reputation.VillagerReputationLevel;
import com.jvn.villagerretaliation.reputation.VillagerReputationManager;
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

    private static final String ACCEPT_MESSAGE =
            "cobblemonretaliation.pokemon_spar.accept";

    private static final String DECLINE_MESSAGE =
            "cobblemonretaliation.pokemon_spar.decline";

    private static final String LOW_REPUTATION_DECLINE_MESSAGE =
            "cobblemonretaliation.pokemon_spar.decline_low_reputation";

    private static final String UNAVAILABLE_MESSAGE =
            "cobblemonretaliation.pokemon_spar.unavailable";

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

    private static final List<String> DECLINE_FALLBACKS =
            List.of(
                    "Not this time.",
                    "I don't really feel like battling right now.",
                    "Maybe another time.",
                    "I'll pass for now.",
                    "A battle? No, not today.",
                    "I think I'll sit this one out."
            );

    private static final List<String> LOW_REPUTATION_DECLINE_FALLBACKS =
            List.of(
                    "You want to battle me? I don't trust you enough for that.",
                    "I'd rather not put my Pokemon in a battle with you.",
                    "No. I don't know if I can trust you.",
                    "I think you should earn my trust first.",
                    "A friendly battle requires a little more trust than that.",
                    "I'm not comfortable battling you."
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

    public static void startFromDialogue(
            ServerPlayer player,
            Villager villager
    ) {

        if (player == null
                || villager == null) {

            return;
        }

        int villagerEntityId =
                villager.getId();

        /*
         * Close Villager Retaliation's interaction
         * screen before we continue.
         */
        VillagerConversationService
                .endForPlayer(
                        player,
                        true
                );

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
         * Do battle-state checks before rolling
         * reputation acceptance.
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
         * Read the REAL Villager Retaliation
         * reputation level between this player
         * and this specific villager.
         */
        VillagerReputationLevel reputationLevel =
                VillagerReputationManager
                        .getReputationLevel(
                                player.serverLevel(),
                                villager,
                                player.getUUID()
                        );

        /*
         * Very poor reputation:
         *
         * Friendly sparring is refused outright.
         *
         * The outer mixin should normally already
         * catch villagers who are actively hostile,
         * but keeping this check here makes the
         * sparring service safe on its own.
         */
        if (reputationLevel == VillagerReputationLevel.HOSTILE
                ||
                reputationLevel == VillagerReputationLevel.DESPISED
                ||
                reputationLevel == VillagerReputationLevel.FEARED) {

            sendDialogueMessage(
                    player,
                    villager,
                    LOW_REPUTATION_DECLINE_MESSAGE,
                    randomLowReputationFallback(player)
            );

            return;
        }

        /*
         * Reputation-dependent chance of refusing.
         */
        double declineChance =
                getDeclineChance(
                        reputationLevel
                );

        if (player.getRandom().nextDouble()
                < declineChance) {

            /*
             * Suspicious villagers use the more
             * distrustful refusal dialogue.
             *
             * Higher-reputation villagers can still
             * occasionally decline, but for normal
             * personal reasons instead.
             */
            if (reputationLevel
                    == VillagerReputationLevel.SUSPICIOUS) {

                sendDialogueMessage(
                        player,
                        villager,
                        LOW_REPUTATION_DECLINE_MESSAGE,
                        randomLowReputationFallback(player)
                );

            } else {

                sendDialogueMessage(
                        player,
                        villager,
                        DECLINE_MESSAGE,
                        randomDeclineFallback(player)
                );
            }

            return;
        }

        /*
         * Accepted.
         *
         * Send the pre-battle line before starting
         * the actual RCT battle.
         */
        sendDialogueMessage(
                player,
                villager,
                ACCEPT_MESSAGE,
                randomAcceptFallback(player)
        );

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
                    randomUnavailableFallback(player)
            );
        }
    }

    private static double getDeclineChance(
            VillagerReputationLevel reputationLevel
    ) {

        return switch (reputationLevel) {

            /*
             * Player is known, but not trusted.
             */
            case SUSPICIOUS ->
                    0.75D;

            /*
             * Stranger / ordinary relationship.
             */
            case NEUTRAL ->
                    0.30D;

            /*
             * Villager trusts the player, but can
             * still occasionally decline.
             */
            case TRUSTED ->
                    0.15D;

            /*
             * Very unlikely to turn down someone
             * they respect.
             */
            case RESPECTED ->
                    0.05D;

            /*
             * Strong relationship:
             * always willing to friendly spar when
             * otherwise available.
             */
            case REVERED,
                 ROYALTY ->
                    0.0D;

            /*
             * These are handled as outright refusals
             * before this method is normally reached.
             */
            case HOSTILE,
                 DESPISED,
                 FEARED ->
                    1.0D;
        };
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

    private static String randomDeclineFallback(
            ServerPlayer player
    ) {

        return DECLINE_FALLBACKS.get(
                player
                        .getRandom()
                        .nextInt(
                                DECLINE_FALLBACKS.size()
                        )
        );
    }

    private static String randomLowReputationFallback(
            ServerPlayer player
    ) {

        return LOW_REPUTATION_DECLINE_FALLBACKS.get(
                player
                        .getRandom()
                        .nextInt(
                                LOW_REPUTATION_DECLINE_FALLBACKS.size()
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