package com.auy.cobblemonretaliation.compat.rct;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.gitlab.srcmc.rctapi.api.ai.RCTBattleAI;
import com.gitlab.srcmc.rctapi.api.trainer.TrainerBag;
import com.gitlab.srcmc.rctapi.api.trainer.TrainerNPC;
import net.minecraft.world.entity.npc.Villager;

import java.util.List;

public final class RctTrainerFactory {

    private RctTrainerFactory() {
    }

    public static TrainerNPC createTrainer(
            Villager villager,
            List<Pokemon> persistentTeam
    ) {

        Pokemon[] battleTeam =
                copyTeam(
                        persistentTeam
                );

        return new TrainerNPC(
                villager
                        .getName()
                        .getString(),

                battleTeam,

                new TrainerBag(),

                new RCTBattleAI(),

                villager
        );
    }

    /*
     * RCT must NEVER receive our persistent Pokemon
     * instances directly.
     *
     * RCT modifies trainer Pokemon metadata when
     * registering them.
     *
     * We therefore make a deep copy for the battle.
     */
    private static Pokemon[] copyTeam(
            List<Pokemon> sourceTeam
    ) {

        Pokemon[] copiedTeam =
                new Pokemon[
                        sourceTeam.size()
                        ];

        for (int i = 0;
             i < sourceTeam.size();
             i++) {

            Pokemon copy =
                    new Pokemon();

            copy.copyFrom(
                    sourceTeam.get(i)
            );

            copiedTeam[i] =
                    copy;
        }

        return copiedTeam;
    }
}