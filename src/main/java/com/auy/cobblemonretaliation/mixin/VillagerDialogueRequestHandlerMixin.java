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

        // Not our sparring option.
        // Let Villager Retaliation process it normally.
        if (!PokemonSparringService.SPARRING_OPTION_ID.equals(optionId)) {
            return;
        }

        /*
         * Validate the active Villager Retaliation conversation.
         */
        InteractionTargetContext target =
                InteractionRequestValidator
                        .requireDialogueConversation(
                                player,
                                entityId
                        )
                        .orElse(null);

        if (target == null) {
            ci.cancel();
            return;
        }

        Villager villager = target.villager();

        /*
         * Don't interfere with forced quest/story dialogue.
         */
        if (VillagerConversationService.isForced(
                player,
                villager
        )) {
            ci.cancel();
            return;
        }

        /*
         * Preserve Villager Retaliation's aggression rules.
         */
        if (VillagerAggressionPolicy.shouldAttackOnSight(
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
         * Build VR's actual dialogue context.
         */
        DialogueContext context =
                VillagerInteractionService
                        .createDialogueContext(
                                target.level(),
                                player,
                                villager
                        );

        /*
         * Verify that the spar option genuinely exists
         * for this villager/context.
         */
        DialogueOptionDefinition option =
                VillagerDialogueResources
                        .dialogueOption(
                                context,
                                optionId
                        )
                        .orElse(null);

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
         * Start Cobblemon sparring.
         *
         * PokemonSparringService handles closing the
         * VR dialogue and starting the RCT battle.
         */
        PokemonSparringService.startFromDialogue(
                player,
                villager
        );

        /*
         * Prevent VR from processing this afterward
         * as a normal dialogue question.
         */
        ci.cancel();
    }
}