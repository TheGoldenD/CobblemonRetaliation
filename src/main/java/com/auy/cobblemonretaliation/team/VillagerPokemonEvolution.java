package com.auy.cobblemonretaliation.team;

import com.cobblemon.mod.common.api.pokemon.evolution.Evolution;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.evolution.variants.LevelUpEvolution;
import com.cobblemon.mod.common.pokemon.requirements.LevelRequirement;

import java.util.ArrayList;
import java.util.List;

public final class VillagerPokemonEvolution {

    /*
     * Defensive limit.
     *
     * No normal Pokemon should need anywhere near this
     * many sequential evolutions.
     */
    private static final int MAX_EVOLUTION_STEPS = 5;

    private VillagerPokemonEvolution() {
    }

    /**
     * Evolves a villager-owned Pokemon through every
     * currently-valid pure level evolution.
     *
     * Example:
     *
     * Lillipup Lv.39
     *      ↓
     * Herdier Lv.39
     *      ↓
     * Stoutland Lv.39
     *
     * The same Pokemon object is mutated, so its UUID and
     * persistent identity remain intact.
     *
     * @return true if at least one evolution occurred.
     */
    public static boolean evolveLevelOnlyFully(
            Pokemon pokemon
    ) {

        if (pokemon == null) {
            return false;
        }

        boolean evolvedAtLeastOnce =
                false;

        for (int step = 0;
             step < MAX_EVOLUTION_STEPS;
             step++) {

            boolean evolvedThisStep =
                    evolveOneLevelStep(
                            pokemon
                    );

            if (!evolvedThisStep) {
                break;
            }

            evolvedAtLeastOnce =
                    true;
        }

        return evolvedAtLeastOnce;
    }

    /**
     * Attempts exactly one evolution.
     */
    private static boolean evolveOneLevelStep(
            Pokemon pokemon
    ) {

        /*
         * Copy the current evolution list first.
         *
         * Applying an evolution changes the Pokemon's
         * species, which also changes its available
         * evolutions.
         */
        List<Evolution> evolutions =
                new ArrayList<>();

        for (Evolution evolution
                : pokemon.getEvolutions()) {

            evolutions.add(
                    evolution
            );
        }

        for (Evolution evolution : evolutions) {

            /*
             * Only passive/level-up evolutions are
             * considered in this checkpoint.
             */
            if (!(evolution
                    instanceof LevelUpEvolution levelEvolution)) {

                continue;
            }

            /*
             * IMPORTANT:
             *
             * A LevelUpEvolution can still contain
             * additional requirements such as:
             *
             * - friendship
             * - time
             * - biome
             * - held item
             * - moves
             * - stats
             *
             * We don't want to silently bypass those.
             *
             * For now we only accept evolutions whose
             * requirements are entirely LevelRequirement.
             */
            if (!hasOnlyLevelRequirements(
                    levelEvolution
            )) {

                continue;
            }

            /*
             * Let Cobblemon itself verify the configured
             * level requirement.
             *
             * This also respects Cobblemon's evolution
             * tested event.
             */
            if (!levelEvolution.test(
                    pokemon
            )) {

                continue;
            }

            String speciesBefore =
                    getSpeciesId(
                            pokemon
                    );

            /*
             * DO NOT call forceEvolve() here.
             *
             * These Pokemon live in our villager profile
             * rather than a normal player Pokemon store.
             *
             * applyTo() applies Cobblemon's evolution
             * result directly to THIS Pokemon object.
             *
             * Therefore:
             *
             * - UUID stays the same
             * - IVs stay the same
             * - EVs stay the same
             * - nature stays the same
             * - shiny stays the same
             * - persistent identity stays the same
             */
            levelEvolution.applyTo(
                    pokemon
            );

            String speciesAfter =
                    getSpeciesId(
                            pokemon
                    );

            /*
             * Safety against malformed datapack
             * evolutions which technically pass but
             * don't change species.
             */
            if (speciesBefore.equals(
                    speciesAfter
            )) {

                return false;
            }

            return true;
        }

        return false;
    }

    private static boolean hasOnlyLevelRequirements(
            LevelUpEvolution evolution
    ) {

        /*
         * An empty requirement list should not be treated
         * as an automatic level evolution.
         */
        if (evolution
                .getRequirements()
                .isEmpty()) {

            return false;
        }

        return evolution
                .getRequirements()
                .stream()
                .allMatch(
                        requirement ->
                                requirement
                                        instanceof LevelRequirement
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
}