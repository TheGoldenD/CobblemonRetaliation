package com.auy.cobblemonretaliation;

import com.auy.cobblemonretaliation.compat.rct.RctBattleBridge;
import com.auy.cobblemonretaliation.villager.VillagerPokemonManager;
import com.auy.cobblemonretaliation.villager.VillagerPokemonProfile;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;
import java.util.UUID;

public final class CobblemonRetaliationCommands {

    private static final double INSPECT_RANGE =
            8.0D;

    private CobblemonRetaliationCommands() {
    }

    public static void onRegisterCommands(
            RegisterCommandsEvent event
    ) {
        register(
                event.getDispatcher()
        );
    }

    private static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
                Commands.literal(
                                "cobblemonretaliation"
                        )

                        .then(
                                Commands.literal(
                                                "inspect"
                                        )
                                        .executes(
                                                context ->
                                                        inspectVillager(
                                                                context.getSource()
                                                        )
                                        )
                        )

                        .then(
                                Commands.literal(
                                                "team"
                                        )
                                        .executes(
                                                context ->
                                                        showTeam(
                                                                context.getSource()
                                                        )
                                        )
                        )

                        .then(
                                Commands.literal(
                                                "battle"
                                        )
                                        .executes(
                                                context ->
                                                        startBattle(
                                                                context.getSource()
                                                        )
                                        )
                        )
        );
    }

    private static int inspectVillager(
            CommandSourceStack source
    ) {
        ServerPlayer player =
                getPlayer(
                        source
                );

        if (player == null) {
            return 0;
        }

        Villager villager =
                getTargetVillager(
                        player
                );

        if (villager == null) {

            sendTargetFailure(
                    source
            );

            return 0;
        }

        VillagerPokemonProfile profile =
                VillagerPokemonManager
                        .getOrCreate(
                                villager
                        );

        String profession =
                VillagerPokemonManager
                        .getProfessionId(
                                villager
                        );

        String employment =
                getEmploymentState(
                        profession
                );

        sendHeader(
                source,
                "CobblemonRetaliation Villager",
                ChatFormatting.GOLD
        );

        sendField(
                source,
                "Name",
                villager
                        .getName()
                        .getString(),
                ChatFormatting.WHITE
        );

        sendField(
                source,
                "UUID",
                villager
                        .getUUID()
                        .toString(),
                ChatFormatting.WHITE
        );

        sendField(
                source,
                "Profession",
                profession,
                ChatFormatting.AQUA
        );

        sendField(
                source,
                "Employment",
                employment,
                ChatFormatting.YELLOW
        );

        sendField(
                source,
                "Villager level",
                Integer.toString(
                        villager
                                .getVillagerData()
                                .getLevel()
                ),
                ChatFormatting.WHITE
        );

        sendField(
                source,
                "Villager type",
                VillagerPokemonManager
                        .getVillagerTypeId(
                                villager
                        ),
                ChatFormatting.WHITE
        );

        sendField(
                source,
                "Biome",
                VillagerPokemonManager
                        .getBiomeId(
                                villager
                        ),
                ChatFormatting.GREEN
        );

        sendField(
                source,
                "Age",
                villager.isBaby()
                        ? "Baby"
                        : "Adult",
                ChatFormatting.WHITE
        );

        sendField(
                source,
                "Position",
                villager
                        .blockPosition()
                        .toShortString(),
                ChatFormatting.WHITE
        );

        sendHeader(
                source,
                "Pokemon Profile",
                ChatFormatting.LIGHT_PURPLE
        );

        sendField(
                source,
                "Generation seed",
                Long.toString(
                        profile
                                .getGenerationSeed()
                ),
                ChatFormatting.WHITE
        );

        sendField(
                source,
                "Origin biome",
                profile
                        .getOriginBiomeId(),
                ChatFormatting.GREEN
        );

        sendField(
                source,
                "Origin villager type",
                profile
                        .getOriginVillagerTypeId(),
                ChatFormatting.WHITE
        );

        sendField(
                source,
                "Origin profession",
                profile
                        .getOriginProfessionId(),
                ChatFormatting.AQUA
        );

        sendField(
                source,
                "Current profession",
                profile
                        .getLastKnownProfessionId(),
                ChatFormatting.AQUA
        );

        sendField(
                source,
                "Team generated",
                Boolean.toString(
                        profile
                                .isTeamGenerated()
                ),
                ChatFormatting.YELLOW
        );

        sendField(
                source,
                "Team size",
                Integer.toString(
                        profile
                                .getTeam()
                                .size()
                ),
                ChatFormatting.WHITE
        );

        return 1;
    }

    private static int showTeam(
            CommandSourceStack source
    ) {
        ServerPlayer player =
                getPlayer(
                        source
                );

        if (player == null) {
            return 0;
        }

        Villager villager =
                getTargetVillager(
                        player
                );

        if (villager == null) {

            sendTargetFailure(
                    source
            );

            return 0;
        }

        if (villager.isBaby()) {

            source.sendFailure(
                    Component.literal(
                            "Baby villagers cannot have battle teams."
                    )
            );

            return 0;
        }

        List<Pokemon> team =
                VillagerPokemonManager
                        .getOrCreateTeam(
                                villager
                        );

        sendHeader(
                source,
                "Villager Pokemon Team",
                ChatFormatting.GREEN
        );

        sendField(
                source,
                "Owner",
                villager
                        .getName()
                        .getString(),
                ChatFormatting.WHITE
        );

        sendField(
                source,
                "Profession",
                VillagerPokemonManager
                        .getProfessionId(
                                villager
                        ),
                ChatFormatting.AQUA
        );

        sendField(
                source,
                "Team size",
                Integer.toString(
                        team.size()
                ),
                ChatFormatting.YELLOW
        );

        for (int i = 0;
             i < team.size();
             i++) {

            Pokemon pokemon =
                    team.get(i);

            String species =
                    pokemon
                            .getSpecies()
                            .getResourceIdentifier()
                            .toString();

            String value =
                    species
                            + "  Lv."
                            + pokemon.getLevel();

            int slot =
                    i + 1;

            sendField(
                    source,
                    Integer.toString(slot),
                    value,
                    ChatFormatting.GREEN
            );

            sendField(
                    source,
                    "   Pokemon UUID",
                    pokemon
                            .getUuid()
                            .toString(),
                    ChatFormatting.DARK_GRAY
            );
        }

        return 1;
    }

    private static int startBattle(
            CommandSourceStack source
    ) {
        ServerPlayer player =
                getPlayer(
                        source
                );

        if (player == null) {
            return 0;
        }

        Villager villager =
                getTargetVillager(
                        player
                );

        if (villager == null) {

            sendTargetFailure(
                    source
            );

            return 0;
        }

        if (villager.isBaby()) {

            source.sendFailure(
                    Component.literal(
                            "Baby villagers cannot battle."
                    )
            );

            return 0;
        }

        if (RctBattleBridge
                .isVillagerBusy(
                        villager
                )) {

            source.sendFailure(
                    Component.literal(
                            "This villager is already in a Pokemon battle."
                    )
            );

            return 0;
        }

        /*
         * This also lazily generates the villager's
         * persistent team if they don't have one yet.
         */
        UUID battleId =
                RctBattleBridge
                        .startBattle(
                                player,
                                villager
                        );

        if (battleId == null) {

            source.sendFailure(
                    Component.literal(
                            "The Pokemon battle could not be started. "
                                    + "Check the battle error shown by Cobblemon/RCT."
                    )
            );

            return 0;
        }

        source.sendSuccess(
                () ->
                        Component.literal(
                                        "Started Pokemon battle with "
                                                + villager
                                                .getName()
                                                .getString()
                                                + "."
                                )
                                .withStyle(
                                        ChatFormatting.GREEN
                                ),
                false
        );

        CobblemonRetaliation.LOGGER.info(
                "[CobblemonRetaliation] Command started RCT battle {}.",
                battleId
        );

        return 1;
    }

    private static ServerPlayer getPlayer(
            CommandSourceStack source
    ) {
        try {

            return source
                    .getPlayerOrException();

        } catch (Exception exception) {

            source.sendFailure(
                    Component.literal(
                            "This command must be used by a player."
                    )
            );

            return null;
        }
    }

    private static Villager getTargetVillager(
            ServerPlayer player
    ) {
        HitResult hitResult =
                ProjectileUtil
                        .getHitResultOnViewVector(
                                player,
                                entity ->
                                        entity instanceof Villager,
                                INSPECT_RANGE
                        );

        if (hitResult
                instanceof EntityHitResult entityHitResult
                &&
                entityHitResult
                        .getEntity()
                        instanceof Villager villager) {

            return villager;
        }

        return null;
    }

    private static void sendTargetFailure(
            CommandSourceStack source
    ) {
        source.sendFailure(
                Component.literal(
                        "Look directly at a villager within "
                                + (int) INSPECT_RANGE
                                + " blocks."
                )
        );
    }

    private static String getEmploymentState(
            String profession
    ) {
        return switch (profession) {

            case "minecraft:none" ->
                    "Unemployed";

            case "minecraft:nitwit" ->
                    "Nitwit";

            default ->
                    "Employed";
        };
    }

    private static void sendHeader(
            CommandSourceStack source,
            String title,
            ChatFormatting color
    ) {
        source.sendSuccess(
                () ->
                        Component.literal(
                                        "----- "
                                                + title
                                                + " -----"
                                )
                                .withStyle(
                                        color
                                ),
                false
        );
    }

    private static void sendField(
            CommandSourceStack source,
            String label,
            String value,
            ChatFormatting valueColor
    ) {
        source.sendSuccess(
                () ->
                        Component.literal(
                                        label
                                                + ": "
                                )
                                .withStyle(
                                        ChatFormatting.GRAY
                                )
                                .append(
                                        Component.literal(
                                                        value
                                                )
                                                .withStyle(
                                                        valueColor
                                                )
                                ),
                false
        );
    }
}