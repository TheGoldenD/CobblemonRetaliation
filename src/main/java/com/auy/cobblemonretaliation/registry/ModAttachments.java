package com.auy.cobblemonretaliation.registry;

import com.auy.cobblemonretaliation.CobblemonRetaliation;
import com.auy.cobblemonretaliation.villager.VillagerPokemonProfile;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class ModAttachments {

    private static final DeferredRegister<AttachmentType<?>>
            ATTACHMENT_TYPES =
            DeferredRegister.create(
                    NeoForgeRegistries.ATTACHMENT_TYPES,
                    CobblemonRetaliation.MOD_ID
            );

    public static final Supplier<
            AttachmentType<VillagerPokemonProfile>
            > VILLAGER_POKEMON_PROFILE =
            ATTACHMENT_TYPES.register(
                    "villager_pokemon_profile",
                    () -> AttachmentType
                            .serializable(
                                    VillagerPokemonProfile::new
                            )
                            .copyOnDeath()
                            .build()
            );

    private ModAttachments() {
    }

    public static void register(
            IEventBus modEventBus
    ) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}