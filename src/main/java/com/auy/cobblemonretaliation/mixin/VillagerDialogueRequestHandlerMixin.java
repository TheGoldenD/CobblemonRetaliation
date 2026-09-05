package com.auy.cobblemonretaliation.mixin;

import com.auy.cobblemonretaliation.compat.retaliation.PokemonSparringService;
import com.jvn.villagerretaliation.interaction.InteractionRequestValidator;
import com.jvn.villagerretaliation.interaction.InteractionTargetContext;
import com.jvn.villagerretaliation.interaction.VillagerConversationService;
import com.jvn.villagerretaliation.interaction.VillagerInteractionService;
import com.jvn.villagerretaliation.reputation.VillagerAggressionPolicy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        value = VillagerInteractionService.class,
        remap = false
)
public abstract class VillagerDialogueRequestHandlerMixin {

    private VillagerDialogueRequestHandlerMixin() {
    }

    @Inject(
            method = "handleDialogueRequest",
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
         * Ignore every Villager Retaliation dialogue
         * option except our Pokémon spar option.
         */
        if (!PokemonSparringService.isSparringOption(optionId)) {
            return;
        }

        /*
         * Validate that the player really has an
         * active dialogue with this villager.
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

        Villager villager =
                target.villager();

        /*
         * Don't hijack quest/forced conversations.
         */
        if (VillagerConversationService.isForced(
                player,
                villager
        )) {
            ci.cancel();
            return;
        }

        /*
         * Preserve Villager Retaliation's normal
         * hostility rules.
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
         * Our option has now been completely claimed.
         *
         * PokemonSparringService closes the dialogue
         * and starts the RCT/Cobblemon battle.
         */
        PokemonSparringService.startFromDialogue(
                player,
                villager
        );

        /*
         * CRITICAL:
         *
         * Do not allow Villager Retaliation to process
         * this option afterward.
         *
         * This is what prevents:
         *
         * - question dialogue
         * - question cooldown
         * - repeated-question penalty
         * - reputation gain/loss
         */
        ci.cancel();
    }
}