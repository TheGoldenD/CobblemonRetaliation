package com.auy.cobblemonretaliation.mixin;

import com.auy.cobblemonretaliation.compat.retaliation.PokemonSparringService;
import com.jvn.villagerretaliation.dialogue.DialogueContext;
import com.jvn.villagerretaliation.dialogue.normal.DialogueOptionDefinition;
import com.jvn.villagerretaliation.dialogue.resources.VillagerDialogueResources;
import com.jvn.villagerretaliation.interaction.InteractionRequestValidator;
import com.jvn.villagerretaliation.interaction.InteractionTargetContext;
import com.jvn.villagerretaliation.interaction.VillagerConversationService;
import com.jvn.villagerretaliation.interaction.VillagerDialogueRequestHandler;
import com.jvn.villagerretaliation.interaction.VillagerInteractionService;
import com.jvn.villagerretaliation.reputation.VillagerAggressionPolicy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        value = VillagerDialogueRequestHandler.class,
        remap = false
)
public abstract class VillagerDialogueRequestHandlerMixin {

    private VillagerDialogueRequestHandlerMixin() {
    }

    /**
     * Intercepts ONLY CobblemonRetaliation's sparring
     * dialogue option.
     *
     * Every normal Villager Retaliation option continues
     * into the original method untouched.
     */
    @Inject(
            method = "handle",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void cobblemonretaliation$handlePokemonSpar(
            ServerPlayer player,
            int entityId,
            String optionId,
            CallbackInfo ci
    ) {

        /*
         * Not our dialogue option.
         *
         * Let Villager Retaliation continue normally.
         */
        if (!PokemonSparringService
                .SPARRING_OPTION_ID
                .equals(
                        optionId
                )) {

            return;
        }

        /*
         * Use Villager Retaliation's own normal
         * conversation validation.
         *
         * This checks that:
         *
         * - the villager exists
         * - this player actually has a conversation
         * - the conversation target matches
         * - normal interaction is currently allowed
         */
        InteractionTargetContext target =
                InteractionRequestValidator
                        .requireDialogueConversation(
                                player,
                                entityId
                        )
                        .orElse(
                                null
                        );

        if (target == null) {

            ci.cancel();
            return;
        }

        Villager villager =
                target.villager();

        /*
         * Don't allow our ordinary spar option to
         * hijack a forced quest/story conversation.
         */
        if (VillagerConversationService
                .isForced(
                        player,
                        villager
                )) {

            ci.cancel();
            return;
        }

        /*
         * Preserve VR's normal despised/attack-on-sight
         * behavior.
         *
         * A villager who wants to kill the player should
         * not suddenly agree to a friendly spar.
         */
        if (VillagerAggressionPolicy
                .shouldAttackOnSight(
                        villager,
                        player
                )) {

            InteractionRequestValidator
                    .endConversationWithRefusal(
                            target,
                            "interaction.refuse_despised"
                    );

            ci.cancel();
            return;
        }

        /*
         * Build the real VR dialogue context.
         */
        DialogueContext context =
                VillagerInteractionService
                        .createDialogueContext(
                                target.level(),
                                player,
                                villager
                        );

        /*
         * Server-side validation that the option is
         * actually available for this context.
         *
         * This prevents a modified client from simply
         * sending our option ID when it isn't available.
         */
        DialogueOptionDefinition option =
                VillagerDialogueResources
                        .dialogueOption(
                                context,
                                optionId
                        )
                        .orElse(
                                null
                        );

        if (option == null) {

            VillagerInteractionService
                    .sendVillagerNotice(
                            player,
                            villager,
                            "interaction.unknown_dialogue_option"
                    );

            ci.cancel();
            return;
        }

        /*
         * IMPORTANT:
         *
         * We deliberately do NOT call VR's normal
         * DialogueReputationService here.
         *
         * Sparring is neutral:
         *
         * no reputation gain
         * no reputation loss
         * no repeated-question penalty
         */
        PokemonSparringService
                .startFromDialogue(
                        player,
                        villager
                );

        /*
         * Stop VillagerDialogueRequestHandler.handle()
         * from processing this as a normal QUESTION.
         */
        ci.cancel();
    }
}