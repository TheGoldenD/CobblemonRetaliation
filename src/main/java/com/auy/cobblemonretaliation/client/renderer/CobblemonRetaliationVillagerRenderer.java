package com.auy.cobblemonretaliation.client.renderer;

import com.jvn.villagerretaliation.client.renderer.VillagerRetaliationVillagerRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;

public final class CobblemonRetaliationVillagerRenderer
        extends VillagerRetaliationVillagerRenderer {

    private static final ResourceLocation COBBLEMON_NURSE =
            ResourceLocation.fromNamespaceAndPath(
                    "cobblemon",
                    "nurse"
            );

    /*
     * Vanilla renderer is used only for Cobblemon Nurses.
     *
     * This gives Cobblemon's nurse profession texture the
     * model/UV layout it was designed for.
     */
    private final VillagerRenderer vanillaRenderer;

    public CobblemonRetaliationVillagerRenderer(
            EntityRendererProvider.Context context
    ) {
        super(context);

        this.vanillaRenderer =
                new VillagerRenderer(
                        context
                );
    }

    @Override
    public void render(
            Villager villager,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {

        /*
         * Cobblemon's Nurse profession texture uses the
         * normal Minecraft villager model layout.
         *
         * Villager Retaliation normally renders villagers
         * using its combat-capable model, whose UV layout
         * is different.
         *
         * Therefore Nurse villagers are delegated to the
         * vanilla renderer.
         */
        if (isCobblemonNurse(villager)) {

            vanillaRenderer.render(
                    villager,
                    entityYaw,
                    partialTick,
                    poseStack,
                    buffer,
                    packedLight
            );

            return;
        }

        /*
         * Every other villager continues using Villager
         * Retaliation's renderer normally.
         */
        super.render(
                villager,
                entityYaw,
                partialTick,
                poseStack,
                buffer,
                packedLight
        );
    }

    private static boolean isCobblemonNurse(
            Villager villager
    ) {

        ResourceLocation professionId =
                BuiltInRegistries
                        .VILLAGER_PROFESSION
                        .getKey(
                                villager
                                        .getVillagerData()
                                        .getProfession()
                        );

        return COBBLEMON_NURSE.equals(
                professionId
        );
    }
}