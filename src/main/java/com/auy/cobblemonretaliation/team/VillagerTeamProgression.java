package com.auy.cobblemonretaliation.team;

import com.auy.cobblemonretaliation.villager.VillagerPokemonProfile;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.entity.npc.Villager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class VillagerTeamProgression {

    private VillagerTeamProgression() {
    }

    /**
     * Applies all progression required for the villager's
     * current career rank.
     *
     * This includes:
     *
     * - increasing existing Pokemon levels
     * - adding newly unlocked party members
     * - performing valid pure-level evolutions
     *
     * @return true when persistent team/profile data changed.
     */
    public static boolean progressIfNeeded(
            Villager villager,
            VillagerPokemonProfile profile
    ) {

        if (!profile.isTeamGenerated()) {
            return false;
        }

        int currentVillagerLevel =
                clampVillagerLevel(
                        villager
                                .getVillagerData()
                                .getLevel()
                );

        int previousProgressionLevel =
                profile
                        .getTeamProgressionLevel();

        /*
         * ------------------------------------------------
         * Existing profile migration
         * ------------------------------------------------
         *
         * Older profiles did not track progression level.
         *
         * Preserve their current team exactly and simply
         * establish the current rank as the baseline.
         *
         * We ARE allowed to apply valid level evolutions
         * because this does not reroll or replace Pokemon.
         */
        if (previousProgressionLevel < 0) {

            List<Pokemon> migratedTeam =
                    new ArrayList<>(
                            profile.getTeam()
                    );

            evolveTeam(
                    migratedTeam
            );

            profile.updateTeam(
                    migratedTeam,
                    currentVillagerLevel
            );

            return true;
        }

        /*
         * ------------------------------------------------
         * No career rank-up
         * ------------------------------------------------
         *
         * We still perform evolution checks.
         *
         * This matters for existing worlds that already
         * reached Master before this evolution feature
         * was added.
         *
         * Example:
         *
         * Tyler already has:
         *
         * Skiddo Lv.38
         * Lillipup Lv.39
         * Pidgey Lv.45
         *
         * We want those to evolve immediately without
         * needing a fictional rank 6.
         */
        if (currentVillagerLevel
                <= previousProgressionLevel) {

            List<Pokemon> currentTeam =
                    new ArrayList<>(
                            profile.getTeam()
                    );

            boolean evolved =
                    evolveTeam(
                            currentTeam
                    );

            if (evolved) {

                profile.updateTeam(
                        currentTeam,
                        previousProgressionLevel
                );
            }

            return evolved;
        }

        /*
         * ------------------------------------------------
         * Career rank-up
         * ------------------------------------------------
         */

        List<Pokemon> updatedTeam =
                new ArrayList<>(
                        profile.getTeam()
                );

        /*
         * ------------------------------------------------
         * 1. Raise existing Pokemon levels
         * ------------------------------------------------
         */

        for (Pokemon pokemon : updatedTeam) {

            int targetLevel =
                    chooseProgressedPokemonLevel(
                            profile.getGenerationSeed(),
                            pokemon,
                            currentVillagerLevel
                    );

            if (pokemon.getLevel()
                    < targetLevel) {

                pokemon.setLevel(
                        targetLevel
                );
            }
        }

        /*
         * ------------------------------------------------
         * 2. Determine newly unlocked team slots
         * ------------------------------------------------
         *
         * The generated reference team is NOT used to
         * replace existing Pokemon.
         *
         * It only tells us:
         *
         * - target party size
         * - current-profession candidates for new slots
         */

        List<Pokemon> referenceTeam =
                VillagerTeamGenerator
                        .generateTeam(
                                villager,
                                profile
                        );

        int targetTeamSize =
                Math.max(
                        updatedTeam.size(),
                        referenceTeam.size()
                );

        /*
         * Avoid duplicate species where possible.
         */
        Set<String> ownedSpecies =
                new HashSet<>();

        for (Pokemon pokemon : updatedTeam) {

            ownedSpecies.add(
                    getSpeciesId(
                            pokemon
                    )
            );
        }

        for (Pokemon candidate : referenceTeam) {

            if (updatedTeam.size()
                    >= targetTeamSize) {

                break;
            }

            String speciesId =
                    getSpeciesId(
                            candidate
                    );

            if (ownedSpecies.contains(
                    speciesId
            )) {

                continue;
            }

            updatedTeam.add(
                    candidate
            );

            ownedSpecies.add(
                    speciesId
            );
        }

        /*
         * ------------------------------------------------
         * 3. Evolution
         * ------------------------------------------------
         *
         * Do this AFTER:
         *
         * - leveling existing Pokemon
         * - adding new Pokemon
         *
         * Therefore a regional Pidgey newly generated at
         * Lv.45 can immediately progress to Pidgeot too.
         */
        evolveTeam(
                updatedTeam
        );

        /*
         * ------------------------------------------------
         * 4. Persist progression
         * ------------------------------------------------
         */

        profile.updateTeam(
                updatedTeam,
                currentVillagerLevel
        );

        return true;
    }

    /**
     * Evolves all eligible members.
     *
     * @return true if any member evolved.
     */
    private static boolean evolveTeam(
            List<Pokemon> team
    ) {

        boolean changed =
                false;

        for (Pokemon pokemon : team) {

            if (VillagerPokemonEvolution
                    .evolveLevelOnlyFully(
                            pokemon
                    )) {

                changed = true;
            }
        }

        return changed;
    }

    /**
     * Existing Pokemon receive a deterministic target
     * level for each villager career rank.
     *
     * Villager seed + Pokemon UUID + career rank means
     * each Pokemon receives its own stable result.
     */
    private static int chooseProgressedPokemonLevel(
            long villagerSeed,
            Pokemon pokemon,
            int villagerLevel
    ) {

        long pokemonSeed =
                pokemon
                        .getUuid()
                        .getMostSignificantBits()
                        ^
                        Long.rotateLeft(
                                pokemon
                                        .getUuid()
                                        .getLeastSignificantBits(),
                                17
                        );

        long seed =
                villagerSeed
                        ^
                        pokemonSeed
                        ^
                        (
                                0x9E3779B97F4A7C15L
                                        * villagerLevel
                        );

        Random random =
                new Random(
                        seed
                );

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
                maximum
                        - minimum
                        + 1
        );
    }

    private static String getSpeciesId(
            Pokemon pokemon
    ) {

        return pokemon
                .getSpecies()
                .getResourceIdentifier()
                .toString();
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