package com.auy.cobblemonretaliation.client;

import com.auy.cobblemonretaliation.CobblemonRetaliation;
import com.auy.cobblemonretaliation.client.renderer.CobblemonRetaliationVillagerRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(
        modid = CobblemonRetaliation.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class ClientRendererCompat {

    private ClientRendererCompat() {
    }

    @SubscribeEvent(
            priority = EventPriority.LOWEST
    )
    public static void onRegisterRenderers(
            EntityRenderersEvent.RegisterRenderers event
    ) {

        /*
         * Explicitly bind the provider to Villager.
         *
         * Without this, Java/IntelliJ can fail to infer T
         * through VillagerRetaliationVillagerRenderer's
         * generic superclass hierarchy.
         */
        EntityRendererProvider<Villager> provider =
                context ->
                        new CobblemonRetaliationVillagerRenderer(
                                context
                        );

        event.<Villager>registerEntityRenderer(
                EntityType.VILLAGER,
                provider
        );
    }
}