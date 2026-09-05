package com.auy.cobblemonretaliation.team;

import com.auy.cobblemonretaliation.villager.VillagerPokemonManager;
import com.auy.cobblemonretaliation.villager.VillagerPokemonProfile;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.entity.npc.Villager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class VillagerTeamGenerator {

    /*
     * ============================================================
     * NURSE
     * ============================================================
     */

    private static final List<WeightedPokemon> NURSE_POOL =
            List.of(
                    p("cobblemon:happiny", 18, 1, 2),
                    p("cobblemon:chansey", 18, 1, 4),
                    p("cobblemon:blissey", 14, 4, 5),

                    p("cobblemon:audino", 16, 1, 5),
                    p("cobblemon:comfey", 14, 1, 5),
                    p("cobblemon:indeedee", 12, 2, 5),

                    p("cobblemon:cutiefly", 14, 1, 2),
                    p("cobblemon:ribombee", 12, 3, 5)
            );

    /*
     * ============================================================
     * FARMER
     * ============================================================
     */

    private static final List<WeightedPokemon> FARMER_POOL =
            List.of(
                    p("cobblemon:wooloo", 18, 1, 2),
                    p("cobblemon:dubwool", 18, 3, 5),

                    p("cobblemon:mudbray", 18, 1, 2),
                    p("cobblemon:mudsdale", 18, 3, 5),

                    p("cobblemon:skiddo", 16, 1, 3),
                    p("cobblemon:gogoat", 16, 4, 5),

                    p("cobblemon:smoliv", 16, 1, 2),
                    p("cobblemon:dolliv", 16, 3, 4),
                    p("cobblemon:arboliva", 16, 5, 5),

                    p("cobblemon:hoppip", 12, 1, 2),
                    p("cobblemon:skiploom", 12, 3, 4),
                    p("cobblemon:jumpluff", 12, 5, 5),

                    p("cobblemon:tauros", 8, 3, 5),
                    p("cobblemon:miltank", 8, 3, 5)
            );

    /*
     * ============================================================
     * FISHERMAN
     * ============================================================
     */

    private static final List<WeightedPokemon> FISHERMAN_POOL =
            List.of(
                    p("cobblemon:magikarp", 20, 1, 2),
                    p("cobblemon:gyarados", 14, 3, 5),

                    p("cobblemon:goldeen", 16, 1, 2),
                    p("cobblemon:seaking", 14, 3, 5),

                    p("cobblemon:finneon", 15, 1, 2),
                    p("cobblemon:lumineon", 13, 3, 5),

                    p("cobblemon:corphish", 15, 1, 2),
                    p("cobblemon:crawdaunt", 13, 3, 5),

                    p("cobblemon:chewtle", 14, 1, 2),
                    p("cobblemon:drednaw", 14, 3, 5),

                    p("cobblemon:barboach", 13, 1, 2),
                    p("cobblemon:whiscash", 13, 3, 5),

                    p("cobblemon:horsea", 12, 1, 2),
                    p("cobblemon:seadra", 12, 3, 5)
            );

    /*
     * ============================================================
     * ARMORER
     * ============================================================
     */

    private static final List<WeightedPokemon> ARMORER_POOL =
            List.of(
                    p("cobblemon:aron", 20, 1, 2),
                    p("cobblemon:lairon", 18, 3, 4),
                    p("cobblemon:aggron", 16, 5, 5),

                    p("cobblemon:shieldon", 16, 1, 3),
                    p("cobblemon:bastiodon", 16, 4, 5),

                    p("cobblemon:bronzor", 16, 1, 2),
                    p("cobblemon:bronzong", 16, 3, 5),

                    p("cobblemon:rookidee", 12, 1, 1),
                    p("cobblemon:corvisquire", 12, 2, 3),
                    p("cobblemon:corviknight", 14, 4, 5)
            );

    /*
     * ============================================================
     * BUTCHER
     * ============================================================
     */

    private static final List<WeightedPokemon> BUTCHER_POOL =
            List.of(
                    p("cobblemon:lechonk", 18, 1, 2),
                    p("cobblemon:oinkologne", 18, 3, 5),

                    p("cobblemon:tepig", 16, 1, 2),
                    p("cobblemon:pignite", 16, 3, 4),
                    p("cobblemon:emboar", 14, 5, 5),

                    p("cobblemon:tauros", 12, 2, 5),
                    p("cobblemon:miltank", 12, 2, 5),

                    p("cobblemon:combee", 10, 1, 2),
                    p("cobblemon:vespiquen", 10, 3, 5)
            );

    /*
     * ============================================================
     * CARTOGRAPHER
     * ============================================================
     */

    private static final List<WeightedPokemon> CARTOGRAPHER_POOL =
            List.of(
                    p("cobblemon:natu", 18, 1, 2),
                    p("cobblemon:xatu", 18, 3, 5),

                    p("cobblemon:wingull", 16, 1, 2),
                    p("cobblemon:pelipper", 16, 3, 5),

                    p("cobblemon:taillow", 14, 1, 2),
                    p("cobblemon:swellow", 14, 3, 5),

                    p("cobblemon:noibat", 12, 1, 3),
                    p("cobblemon:noivern", 12, 4, 5),

                    p("cobblemon:oranguru", 8, 4, 5)
            );

    /*
     * ============================================================
     * CLERIC
     * ============================================================
     */

    private static final List<WeightedPokemon> CLERIC_POOL =
            List.of(
                    p("cobblemon:abra", 18, 1, 2),
                    p("cobblemon:kadabra", 16, 3, 4),
                    p("cobblemon:alakazam", 14, 5, 5),

                    p("cobblemon:ralts", 16, 1, 2),
                    p("cobblemon:kirlia", 16, 3, 4),
                    p("cobblemon:gardevoir", 14, 5, 5),

                    p("cobblemon:hatenna", 16, 1, 2),
                    p("cobblemon:hattrem", 16, 3, 4),
                    p("cobblemon:hatterene", 14, 5, 5),

                    p("cobblemon:chingling", 10, 1, 2),
                    p("cobblemon:chimecho", 12, 3, 5)
            );

    /*
     * ============================================================
     * FLETCHER
     * ============================================================
     */

    private static final List<WeightedPokemon> FLETCHER_POOL =
            List.of(
                    p("cobblemon:fletchling", 20, 1, 2),
                    p("cobblemon:fletchinder", 18, 3, 4),
                    p("cobblemon:talonflame", 16, 5, 5),

                    p("cobblemon:rowlet", 16, 1, 2),
                    p("cobblemon:dartrix", 16, 3, 4),
                    p("cobblemon:decidueye", 14, 5, 5),

                    p("cobblemon:starly", 16, 1, 2),
                    p("cobblemon:staravia", 16, 3, 4),
                    p("cobblemon:staraptor", 14, 5, 5)
            );

    /*
     * ============================================================
     * LEATHERWORKER
     * ============================================================
     */

    private static final List<WeightedPokemon> LEATHERWORKER_POOL =
            List.of(
                    p("cobblemon:sandile", 18, 1, 2),
                    p("cobblemon:krokorok", 16, 3, 4),
                    p("cobblemon:krookodile", 14, 5, 5),

                    p("cobblemon:scraggy", 18, 1, 2),
                    p("cobblemon:scrafty", 16, 3, 5),

                    p("cobblemon:houndour", 14, 1, 2),
                    p("cobblemon:houndoom", 14, 3, 5),

                    p("cobblemon:mudbray", 12, 1, 2),
                    p("cobblemon:mudsdale", 12, 3, 5)
            );

    /*
     * ============================================================
     * LIBRARIAN
     * ============================================================
     */

    private static final List<WeightedPokemon> LIBRARIAN_POOL =
            List.of(
                    p("cobblemon:hoothoot", 18, 1, 2),
                    p("cobblemon:noctowl", 18, 3, 5),

                    p("cobblemon:abra", 16, 1, 2),
                    p("cobblemon:kadabra", 14, 3, 4),
                    p("cobblemon:alakazam", 12, 5, 5),

                    p("cobblemon:solosis", 16, 1, 2),
                    p("cobblemon:duosion", 14, 3, 4),
                    p("cobblemon:reuniclus", 12, 5, 5),

                    p("cobblemon:oranguru", 10, 3, 5)
            );

    /*
     * ============================================================
     * MASON
     * ============================================================
     */

    private static final List<WeightedPokemon> MASON_POOL =
            List.of(
                    p("cobblemon:roggenrola", 20, 1, 2),
                    p("cobblemon:boldore", 18, 3, 4),
                    p("cobblemon:gigalith", 16, 5, 5),

                    p("cobblemon:nacli", 18, 1, 2),
                    p("cobblemon:naclstack", 16, 3, 4),
                    p("cobblemon:garganacl", 14, 5, 5),

                    p("cobblemon:geodude", 16, 1, 2),
                    p("cobblemon:graveler", 14, 3, 4),
                    p("cobblemon:golem", 12, 5, 5),

                    p("cobblemon:timburr", 12, 1, 2),
                    p("cobblemon:gurdurr", 12, 3, 4),
                    p("cobblemon:conkeldurr", 10, 5, 5)
            );

    /*
     * ============================================================
     * SHEPHERD
     * ============================================================
     */

    private static final List<WeightedPokemon> SHEPHERD_POOL =
            List.of(
                    p("cobblemon:mareep", 20, 1, 2),
                    p("cobblemon:flaaffy", 18, 3, 4),
                    p("cobblemon:ampharos", 16, 5, 5),

                    p("cobblemon:wooloo", 20, 1, 2),
                    p("cobblemon:dubwool", 18, 3, 5),

                    p("cobblemon:swablu", 14, 1, 3),
                    p("cobblemon:altaria", 14, 4, 5),

                    p("cobblemon:skiddo", 12, 1, 2),
                    p("cobblemon:gogoat", 12, 3, 5)
            );

    /*
     * ============================================================
     * TOOLSMITH
     * ============================================================
     */

    private static final List<WeightedPokemon> TOOLSMITH_POOL =
            List.of(
                    p("cobblemon:drilbur", 20, 1, 2),
                    p("cobblemon:excadrill", 18, 3, 5),

                    p("cobblemon:tinkatink", 16, 1, 2),
                    p("cobblemon:tinkatuff", 16, 3, 4),
                    p("cobblemon:tinkaton", 14, 5, 5),

                    p("cobblemon:magnemite", 16, 1, 2),
                    p("cobblemon:magneton", 14, 3, 4),
                    p("cobblemon:magnezone", 12, 5, 5),

                    p("cobblemon:diglett", 12, 1, 2),
                    p("cobblemon:dugtrio", 12, 3, 5)
            );

    /*
     * ============================================================
     * WEAPONSMITH
     * ============================================================
     */

    private static final List<WeightedPokemon> WEAPONSMITH_POOL =
            List.of(
                    p("cobblemon:pawniard", 20, 1, 3),
                    p("cobblemon:bisharp", 18, 4, 5),

                    p("cobblemon:riolu", 16, 1, 2),
                    p("cobblemon:lucario", 16, 3, 5),

                    p("cobblemon:honedge", 16, 1, 2),
                    p("cobblemon:doublade", 14, 3, 4),
                    p("cobblemon:aegislash", 12, 5, 5),

                    p("cobblemon:scyther", 10, 2, 4),
                    p("cobblemon:scizor", 10, 5, 5)
            );

    /*
     * ============================================================
     * UNEMPLOYED
     * ============================================================
     */

    private static final List<WeightedPokemon> UNEMPLOYED_POOL =
            List.of(
                    p("cobblemon:eevee", 8, 1, 5),
                    p("cobblemon:pidgey", 14, 1, 5),
                    p("cobblemon:sentret", 14, 1, 5),
                    p("cobblemon:zigzagoon", 14, 1, 5),
                    p("cobblemon:bidoof", 14, 1, 5),
                    p("cobblemon:lillipup", 14, 1, 5),
                    p("cobblemon:skwovet", 14, 1, 5),
                    p("cobblemon:wooloo", 12, 1, 5),
                    p("cobblemon:yamper", 10, 1, 5)
            );

    /*
     * ============================================================
     * NITWIT
     * ============================================================
     *
     * Strange rather than simply weak.
     */

    private static final List<WeightedPokemon> NITWIT_POOL =
            List.of(
                    p("cobblemon:slowpoke", 16, 1, 5),
                    p("cobblemon:psyduck", 16, 1, 5),
                    p("cobblemon:spinda", 14, 1, 5),
                    p("cobblemon:slakoth", 14, 1, 5),
                    p("cobblemon:wobbuffet", 12, 1, 5),
                    p("cobblemon:dunsparce", 12, 1, 5),
                    p("cobblemon:magikarp", 18, 1, 5),
                    p("cobblemon:smoliv", 10, 1, 5)
            );

    /*
     * ============================================================
     * BIOME / REGIONAL BONUS POOLS
     * ============================================================
     *
     * We use the villager's ORIGINAL villager type, not
     * their current physical biome.
     *
     * A taiga villager moved to a desert remains culturally
     * a taiga villager.
     */

    private static final List<WeightedPokemon> PLAINS_BONUS =
            List.of(
                    p("cobblemon:eevee", 5, 1, 5),
                    p("cobblemon:lillipup", 7, 1, 5),
                    p("cobblemon:wooloo", 7, 1, 5),
                    p("cobblemon:pidgey", 7, 1, 5)
            );

    private static final List<WeightedPokemon> TAIGA_BONUS =
            List.of(
                    p("cobblemon:teddiursa", 7, 1, 5),
                    p("cobblemon:stantler", 6, 1, 5),
                    p("cobblemon:rockruff", 7, 1, 5),
                    p("cobblemon:skwovet", 7, 1, 5)
            );

    private static final List<WeightedPokemon> DESERT_BONUS =
            List.of(
                    p("cobblemon:sandshrew", 7, 1, 5),
                    p("cobblemon:trapinch", 7, 1, 5),
                    p("cobblemon:numel", 7, 1, 5),
                    p("cobblemon:hippopotas", 6, 1, 5)
            );

    private static final List<WeightedPokemon> SAVANNA_BONUS =
            List.of(
                    p("cobblemon:litleo", 7, 1, 5),
                    p("cobblemon:blitzle", 7, 1, 5),
                    p("cobblemon:girafarig", 6, 1, 5),
                    p("cobblemon:mudbray", 7, 1, 5)
            );

    private static final List<WeightedPokemon> SNOW_BONUS =
            List.of(
                    p("cobblemon:snom", 7, 1, 5),
                    p("cobblemon:swinub", 7, 1, 5),
                    p("cobblemon:cubchoo", 7, 1, 5),
                    p("cobblemon:snorunt", 7, 1, 5)
            );

    private static final List<WeightedPokemon> SWAMP_BONUS =
            List.of(
                    p("cobblemon:wooper", 7, 1, 5),
                    p("cobblemon:lotad", 7, 1, 5),
                    p("cobblemon:croagunk", 7, 1, 5),
                    p("cobblemon:tympole", 7, 1, 5)
            );

    private static final List<WeightedPokemon> JUNGLE_BONUS =
            List.of(
                    p("cobblemon:aipom", 7, 1, 5),
                    p("cobblemon:bellsprout", 7, 1, 5),
                    p("cobblemon:exeggcute", 7, 1, 5),
                    p("cobblemon:pikipek", 7, 1, 5)
            );

    private VillagerTeamGenerator() {
    }

    /*
     * ============================================================
     * GENERATION
     * ============================================================
     */

    public static List<Pokemon> generateTeam(
            Villager villager,
            VillagerPokemonProfile profile
    ) {

        Random random =
                new Random(
                        profile.getGenerationSeed()
                );

        String profession =
                VillagerPokemonManager
                        .getProfessionId(
                                villager
                        );

        int villagerLevel =
                clampVillagerLevel(
                        villager
                                .getVillagerData()
                                .getLevel()
                );

        /*
         * Start with profession-specific Pokemon.
         */
        List<WeightedPokemon> pool =
                new ArrayList<>(
                        getProfessionPool(
                                profession
                        )
                );

        /*
         * Add regional flavor.
         *
         * These have deliberately lower weights than
         * the profession pool.
         */
        pool.addAll(
                getRegionalBonusPool(
                        profile.getOriginVillagerTypeId()
                )
        );

        int teamSize =
                chooseTeamSize(
                        villagerLevel,
                        profession,
                        random
                );

        List<Pokemon> result =
                new ArrayList<>();

        Set<String> usedSpecies =
                new HashSet<>();

        for (int i = 0;
             i < teamSize;
             i++) {

            WeightedPokemon selected =
                    chooseWeighted(
                            pool,
                            usedSpecies,
                            villagerLevel,
                            random
                    );

            if (selected == null) {
                break;
            }

            usedSpecies.add(
                    selected.speciesId()
            );

            int pokemonLevel =
                    choosePokemonLevel(
                            villagerLevel,
                            random
                    );

            Pokemon pokemon =
                    createPokemon(
                            selected.speciesId(),
                            pokemonLevel
                    );

            result.add(
                    pokemon
            );
        }

        return result;
    }

    /*
     * ============================================================
     * PROFESSION POOLS
     * ============================================================
     */

    private static List<WeightedPokemon> getProfessionPool(
            String profession
    ) {

        return switch (profession) {

            case "cobblemon:nurse" ->
                    NURSE_POOL;

            case "minecraft:armorer" ->
                    ARMORER_POOL;

            case "minecraft:butcher" ->
                    BUTCHER_POOL;

            case "minecraft:cartographer" ->
                    CARTOGRAPHER_POOL;

            case "minecraft:cleric" ->
                    CLERIC_POOL;

            case "minecraft:farmer" ->
                    FARMER_POOL;

            case "minecraft:fisherman" ->
                    FISHERMAN_POOL;

            case "minecraft:fletcher" ->
                    FLETCHER_POOL;

            case "minecraft:leatherworker" ->
                    LEATHERWORKER_POOL;

            case "minecraft:librarian" ->
                    LIBRARIAN_POOL;

            case "minecraft:mason" ->
                    MASON_POOL;

            case "minecraft:shepherd" ->
                    SHEPHERD_POOL;

            case "minecraft:toolsmith" ->
                    TOOLSMITH_POOL;

            case "minecraft:weaponsmith" ->
                    WEAPONSMITH_POOL;

            case "minecraft:nitwit" ->
                    NITWIT_POOL;

            case "minecraft:none" ->
                    UNEMPLOYED_POOL;

            /*
             * Unknown modded professions get the safe
             * generic pool until explicit support exists.
             */
            default ->
                    UNEMPLOYED_POOL;
        };
    }

    /*
     * ============================================================
     * REGIONAL POOLS
     * ============================================================
     */

    private static List<WeightedPokemon> getRegionalBonusPool(
            String villagerType
    ) {

        return switch (villagerType) {

            case "minecraft:taiga" ->
                    TAIGA_BONUS;

            case "minecraft:desert" ->
                    DESERT_BONUS;

            case "minecraft:savanna" ->
                    SAVANNA_BONUS;

            case "minecraft:snow" ->
                    SNOW_BONUS;

            case "minecraft:swamp" ->
                    SWAMP_BONUS;

            case "minecraft:jungle" ->
                    JUNGLE_BONUS;

            case "minecraft:plains" ->
                    PLAINS_BONUS;

            default ->
                    List.of();
        };
    }

    /*
     * ============================================================
     * TEAM SIZE
     * ============================================================
     */

    private static int chooseTeamSize(
            int villagerLevel,
            String profession,
            Random random
    ) {

        int minimum;
        int maximum;

        switch (villagerLevel) {

            /*
             * Novice
             */
            case 1 -> {
                minimum = 1;
                maximum = 2;
            }

            /*
             * Apprentice
             */
            case 2 -> {
                minimum = 1;
                maximum = 2;
            }

            /*
             * Journeyman
             */
            case 3 -> {
                minimum = 2;
                maximum = 3;
            }

            /*
             * Expert
             */
            case 4 -> {
                minimum = 2;
                maximum = 4;
            }

            /*
             * Master
             */
            default -> {
                minimum = 3;
                maximum = 5;
            }
        }

        /*
         * Unemployed villagers shouldn't generally have
         * huge parties simply because their internal
         * villager level happens to be unusual.
         */
        if ("minecraft:none".equals(profession)) {

            minimum = 1;
            maximum = 2;
        }

        return minimum
                + random.nextInt(
                maximum - minimum + 1
        );
    }

    /*
     * ============================================================
     * POKEMON LEVELS
     * ============================================================
     */

    private static int choosePokemonLevel(
            int villagerLevel,
            Random random
    ) {

        int minimum;
        int maximum;

        switch (villagerLevel) {

            case 1 -> {
                minimum = 8;
                maximum = 14;
            }

            case 2 -> {
                minimum = 14;
                maximum = 20;
            }

            case 3 -> {
                minimum = 20;
                maximum = 28;
            }

            case 4 -> {
                minimum = 28;
                maximum = 36;
            }

            default -> {
                minimum = 36;
                maximum = 46;
            }
        }

        return minimum
                + random.nextInt(
                maximum - minimum + 1
        );
    }

    /*
     * ============================================================
     * WEIGHTED SELECTION
     * ============================================================
     */

    private static WeightedPokemon chooseWeighted(
            List<WeightedPokemon> pool,
            Set<String> excludedSpecies,
            int villagerLevel,
            Random random
    ) {

        int totalWeight = 0;

        /*
         * First determine the total weight of all
         * currently valid entries.
         */
        for (WeightedPokemon entry : pool) {

            if (!entry.isAvailableAt(
                    villagerLevel
            )) {
                continue;
            }

            if (excludedSpecies.contains(
                    entry.speciesId()
            )) {
                continue;
            }

            totalWeight +=
                    entry.weight();
        }

        if (totalWeight <= 0) {
            return null;
        }

        int roll =
                random.nextInt(
                        totalWeight
                );

        /*
         * Then walk the weighted pool until the
         * random roll falls inside one entry.
         */
        for (WeightedPokemon entry : pool) {

            if (!entry.isAvailableAt(
                    villagerLevel
            )) {
                continue;
            }

            if (excludedSpecies.contains(
                    entry.speciesId()
            )) {
                continue;
            }

            roll -=
                    entry.weight();

            if (roll < 0) {
                return entry;
            }
        }

        return null;
    }

    /*
     * ============================================================
     * COBBLEMON CREATION
     * ============================================================
     */

    private static Pokemon createPokemon(
            String speciesId,
            int level
    ) {

        PokemonProperties properties =
                new PokemonProperties();

        properties.setSpecies(
                speciesId
        );

        properties.setLevel(
                level
        );

        return properties.create();
    }

    /*
     * ============================================================
     * HELPERS
     * ============================================================
     */

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

    private static WeightedPokemon p(
            String speciesId,
            int weight,
            int minimumVillagerLevel,
            int maximumVillagerLevel
    ) {

        return new WeightedPokemon(
                speciesId,
                weight,
                minimumVillagerLevel,
                maximumVillagerLevel
        );
    }

    /*
     * A pool entry is available only for the specified
     * villager career-level range.
     *
     * Example:
     *
     * Mudbray
     *   ranks 1-2
     *
     * Mudsdale
     *   ranks 3-5
     */
    private record WeightedPokemon(
            String speciesId,
            int weight,
            int minimumVillagerLevel,
            int maximumVillagerLevel
    ) {

        private boolean isAvailableAt(
                int villagerLevel
        ) {

            return villagerLevel
                    >= minimumVillagerLevel
                    &&
                    villagerLevel
                            <= maximumVillagerLevel;
        }
    }
}