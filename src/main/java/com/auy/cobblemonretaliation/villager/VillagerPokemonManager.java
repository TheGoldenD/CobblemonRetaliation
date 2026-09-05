package com.auy.cobblemonretaliation.villager;

import com.auy.cobblemonretaliation.registry.ModAttachments;
import com.auy.cobblemonretaliation.team.VillagerTeamGenerator;
import com.auy.cobblemonretaliation.team.VillagerTeamProgression;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.UUID;

public final class VillagerPokemonManager {

    private VillagerPokemonManager() {
    }

    public static VillagerPokemonProfile getOrCreate(
            Villager villager
    ) {

        VillagerPokemonProfile profile =
                villager.getData(
                        ModAttachments
                                .VILLAGER_POKEMON_PROFILE
                                .get()
                );

        String currentProfession =
                getProfessionId(
                        villager
                );

        /*
         * ------------------------------------------------
         * Permanent villager Pokemon identity
         * ------------------------------------------------
         */

        if (!profile.isInitialized()) {

            profile.initialize(
                    createGenerationSeed(
                            villager.getUUID()
                    ),
                    getBiomeId(
                            villager
                    ),
                    getVillagerTypeId(
                            villager
                    ),
                    currentProfession
            );

            saveProfile(
                    villager,
                    profile
            );
        }

        /*
         * ------------------------------------------------
         * Profession changes
         * ------------------------------------------------
         *
         * Profession changes DO NOT regenerate the team.
         */

        if (!currentProfession.equals(
                profile.getLastKnownProfessionId()
        )) {

            profile.setLastKnownProfessionId(
                    currentProfession
            );

            saveProfile(
                    villager,
                    profile
            );
        }

        /*
         * ------------------------------------------------
         * Existing-team career progression
         * ------------------------------------------------
         */

        if (profile.isTeamGenerated()) {

            boolean changed =
                    VillagerTeamProgression
                            .progressIfNeeded(
                                    villager,
                                    profile
                            );

            if (changed) {

                saveProfile(
                        villager,
                        profile
                );
            }
        }

        return profile;
    }

    public static List<Pokemon> getOrCreateTeam(
            Villager villager
    ) {

        VillagerPokemonProfile profile =
                getOrCreate(
                        villager
                );

        /*
         * ------------------------------------------------
         * Lazy first-time generation
         * ------------------------------------------------
         */

        if (!profile.isTeamGenerated()) {

            int currentVillagerLevel =
                    clampVillagerLevel(
                            villager
                                    .getVillagerData()
                                    .getLevel()
                    );

            List<Pokemon> generatedTeam =
                    VillagerTeamGenerator
                            .generateTeam(
                                    villager,
                                    profile
                            );

            profile.setTeam(
                    generatedTeam,
                    currentVillagerLevel
            );

            saveProfile(
                    villager,
                    profile
            );
        }

        return profile.getTeam();
    }

    private static void saveProfile(
            Villager villager,
            VillagerPokemonProfile profile
    ) {

        villager.setData(
                ModAttachments
                        .VILLAGER_POKEMON_PROFILE
                        .get(),
                profile
        );
    }

    public static String getProfessionId(
            Villager villager
    ) {

        ResourceLocation location =
                BuiltInRegistries
                        .VILLAGER_PROFESSION
                        .getKey(
                                villager
                                        .getVillagerData()
                                        .getProfession()
                        );

        return location != null
                ? location.toString()
                : "unknown";
    }

    public static String getVillagerTypeId(
            Villager villager
    ) {

        ResourceLocation location =
                BuiltInRegistries
                        .VILLAGER_TYPE
                        .getKey(
                                villager
                                        .getVillagerData()
                                        .getType()
                        );

        return location != null
                ? location.toString()
                : "unknown";
    }

    public static String getBiomeId(
            Villager villager
    ) {

        Holder<Biome> biome =
                villager.level()
                        .getBiome(
                                villager.blockPosition()
                        );

        return biome.unwrapKey()
                .map(
                        key ->
                                key.location()
                                        .toString()
                )
                .orElse(
                        "unknown"
                );
    }

    private static long createGenerationSeed(
            UUID uuid
    ) {

        long seed =
                uuid.getMostSignificantBits()
                        ^
                        Long.rotateLeft(
                                uuid.getLeastSignificantBits(),
                                32
                        );

        seed ^= seed >>> 30;

        seed *=
                0xBF58476D1CE4E5B9L;

        seed ^= seed >>> 27;

        seed *=
                0x94D049BB133111EBL;

        seed ^= seed >>> 31;

        return seed;
    }

    private static int clampVillagerLevel(
            int level
    ) {

        return Math.max(
                1,
                Math.min(
                        5,
                        level
                )
        );
    }
}