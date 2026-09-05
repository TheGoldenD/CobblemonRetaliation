package com.auy.cobblemonretaliation;

import com.auy.cobblemonretaliation.compat.rct.RctBattleBridge;
import com.auy.cobblemonretaliation.compat.retaliation.PokemonRetaliationHandler;
import com.auy.cobblemonretaliation.event.PokemonBattleSafetyHandler;
import com.auy.cobblemonretaliation.registry.ModAttachments;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(CobblemonRetaliation.MOD_ID)
public final class CobblemonRetaliation {

    public static final String MOD_ID =
            "cobblemonretaliation";

    public static final Logger LOGGER =
            LogUtils.getLogger();

    public CobblemonRetaliation(
            IEventBus modEventBus,
            ModContainer modContainer
    ) {

        LOGGER.info(
                "Loading CobblemonRetaliation"
        );

        ModAttachments.register(
                modEventBus
        );

        logDependency(
                "cobblemon",
                "Cobblemon"
        );

        logDependency(
                "rctapi",
                "Radical Cobblemon Trainers API"
        );

        logDependency(
                "villagerretaliation",
                "Villager Retaliation"
        );

        logDependency(
                "rctmod",
                "Radical Cobblemon Trainers"
        );

        RctBattleBridge.bootstrap();

        // ------------------------------------------------
        // Commands
        // ------------------------------------------------

        NeoForge.EVENT_BUS.addListener(
                CobblemonRetaliationCommands
                        ::onRegisterCommands
        );

        // ------------------------------------------------
        // RCT lifecycle
        // ------------------------------------------------

        NeoForge.EVENT_BUS.addListener(
                RctBattleBridge
                        ::onServerStarting
        );

        NeoForge.EVENT_BUS.addListener(
                RctBattleBridge
                        ::onServerStopped
        );

        // ------------------------------------------------
        // Automatic Pokemon retaliation
        // ------------------------------------------------

        /*
         * Villager Retaliation processes the attack first.
         *
         * LOWEST means we inspect VR's decision after it
         * has established retaliation/reputation state.
         */
        NeoForge.EVENT_BUS.addListener(
                EventPriority.LOWEST,
                PokemonRetaliationHandler
                        ::onLivingDamagePost
        );

        // ------------------------------------------------
        // Pokemon battle safety
        // ------------------------------------------------

        /*
         * Freeze the actual villager trainer while its
         * Pokemon battle is active.
         *
         * HIGHEST means this happens before Villager
         * Retaliation's own normal tick listeners.
         */
        NeoForge.EVENT_BUS.addListener(
                EventPriority.HIGHEST,
                PokemonBattleSafetyHandler
                        ::onEntityTickPre
        );

        /*
         * Prevent ordinary Minecraft combat targeting
         * during a villager Pokemon battle.
         */
        NeoForge.EVENT_BUS.addListener(
                EventPriority.HIGHEST,
                PokemonBattleSafetyHandler
                        ::onLivingChangeTarget
        );

        /*
         * Catch physical attacks/projectiles that were
         * already in progress when the battle began.
         */
        NeoForge.EVENT_BUS.addListener(
                EventPriority.HIGHEST,
                PokemonBattleSafetyHandler
                        ::onLivingIncomingDamage
        );
    }

    private static void logDependency(
            String modId,
            String displayName
    ) {

        if (ModList
                .get()
                .isLoaded(
                        modId
                )) {

            LOGGER.info(
                    "[CobblemonRetaliation] {} detected.",
                    displayName
            );

        } else {

            LOGGER.info(
                    "[CobblemonRetaliation] {} not detected.",
                    displayName
            );
        }
    }
}