package com.auy.cobblemonretaliation.villager;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class VillagerPokemonProfile
        implements INBTSerializable<CompoundTag> {

    private static final int CURRENT_DATA_VERSION = 3;

    private int dataVersion =
            CURRENT_DATA_VERSION;

    private boolean initialized =
            false;

    private long generationSeed =
            0L;

    private String originBiomeId =
            "";

    private String originVillagerTypeId =
            "";

    private String originProfessionId =
            "";

    private String lastKnownProfessionId =
            "";

    private boolean teamGenerated =
            false;

    /*
     * The highest villager career level that has already
     * been applied to this Pokemon team.
     *
     * -1 means:
     *
     * "This profile came from an older version of
     * CobblemonRetaliation and has not been migrated yet."
     */
    private int teamProgressionLevel =
            -1;

    private final List<Pokemon> team =
            new ArrayList<>();

    public VillagerPokemonProfile() {
    }

    public void initialize(
            long generationSeed,
            String originBiomeId,
            String originVillagerTypeId,
            String originProfessionId
    ) {

        if (initialized) {
            return;
        }

        initialized = true;

        this.generationSeed =
                generationSeed;

        this.originBiomeId =
                originBiomeId;

        this.originVillagerTypeId =
                originVillagerTypeId;

        this.originProfessionId =
                originProfessionId;

        this.lastKnownProfessionId =
                originProfessionId;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public long getGenerationSeed() {
        return generationSeed;
    }

    public String getOriginBiomeId() {
        return originBiomeId;
    }

    public String getOriginVillagerTypeId() {
        return originVillagerTypeId;
    }

    public String getOriginProfessionId() {
        return originProfessionId;
    }

    public String getLastKnownProfessionId() {
        return lastKnownProfessionId;
    }

    public void setLastKnownProfessionId(
            String lastKnownProfessionId
    ) {

        this.lastKnownProfessionId =
                lastKnownProfessionId;
    }

    public boolean isTeamGenerated() {
        return teamGenerated;
    }

    public int getTeamProgressionLevel() {
        return teamProgressionLevel;
    }

    public void setTeamProgressionLevel(
            int teamProgressionLevel
    ) {

        this.teamProgressionLevel =
                teamProgressionLevel;
    }

    public List<Pokemon> getTeam() {

        return Collections.unmodifiableList(
                team
        );
    }

    /**
     * Used when a team is generated for the first time.
     */
    public void setTeam(
            List<Pokemon> pokemon,
            int progressionLevel
    ) {

        team.clear();
        team.addAll(pokemon);

        teamGenerated = true;

        teamProgressionLevel =
                progressionLevel;
    }

    /**
     * Used when an existing team progresses.
     */
    public void updateTeam(
            List<Pokemon> pokemon,
            int progressionLevel
    ) {

        team.clear();
        team.addAll(pokemon);

        teamGenerated = true;

        teamProgressionLevel =
                progressionLevel;
    }

    public void clearTeam() {

        team.clear();

        teamGenerated = false;

        teamProgressionLevel =
                -1;
    }

    @Override
    public CompoundTag serializeNBT(
            HolderLookup.Provider provider
    ) {

        CompoundTag tag =
                new CompoundTag();

        tag.putInt(
                "DataVersion",
                CURRENT_DATA_VERSION
        );

        tag.putBoolean(
                "Initialized",
                initialized
        );

        tag.putLong(
                "GenerationSeed",
                generationSeed
        );

        tag.putString(
                "OriginBiome",
                originBiomeId
        );

        tag.putString(
                "OriginVillagerType",
                originVillagerTypeId
        );

        tag.putString(
                "OriginProfession",
                originProfessionId
        );

        tag.putString(
                "LastKnownProfession",
                lastKnownProfessionId
        );

        tag.putBoolean(
                "TeamGenerated",
                teamGenerated
        );

        tag.putInt(
                "TeamProgressionLevel",
                teamProgressionLevel
        );

        /*
         * Save the complete Cobblemon Pokemon objects.
         */
        ListTag teamTag =
                new ListTag();

        var ops =
                provider.createSerializationContext(
                        NbtOps.INSTANCE
                );

        for (Pokemon pokemon : team) {

            Pokemon.getCODEC()
                    .encodeStart(
                            ops,
                            pokemon
                    )
                    .result()
                    .ifPresent(
                            teamTag::add
                    );
        }

        tag.put(
                "Team",
                teamTag
        );

        return tag;
    }

    @Override
    public void deserializeNBT(
            HolderLookup.Provider provider,
            CompoundTag tag
    ) {

        dataVersion =
                tag.getInt(
                        "DataVersion"
                );

        initialized =
                tag.getBoolean(
                        "Initialized"
                );

        generationSeed =
                tag.getLong(
                        "GenerationSeed"
                );

        originBiomeId =
                tag.getString(
                        "OriginBiome"
                );

        originVillagerTypeId =
                tag.getString(
                        "OriginVillagerType"
                );

        originProfessionId =
                tag.getString(
                        "OriginProfession"
                );

        lastKnownProfessionId =
                tag.getString(
                        "LastKnownProfession"
                );

        teamGenerated =
                tag.getBoolean(
                        "TeamGenerated"
                );

        /*
         * Existing worlds from data version 1/2 don't
         * have this value.
         *
         * Leave it at -1 so VillagerPokemonManager can
         * safely migrate it WITHOUT changing the team.
         */
        if (tag.contains(
                "TeamProgressionLevel",
                Tag.TAG_INT
        )) {

            teamProgressionLevel =
                    tag.getInt(
                            "TeamProgressionLevel"
                    );

        } else {

            teamProgressionLevel =
                    -1;
        }

        team.clear();

        if (!tag.contains(
                "Team",
                Tag.TAG_LIST
        )) {

            return;
        }

        ListTag teamTag =
                tag.getList(
                        "Team",
                        Tag.TAG_COMPOUND
                );

        var ops =
                provider.createSerializationContext(
                        NbtOps.INSTANCE
                );

        for (int i = 0;
             i < teamTag.size();
             i++) {

            CompoundTag pokemonTag =
                    teamTag.getCompound(i);

            Pokemon.getCODEC()
                    .parse(
                            ops,
                            pokemonTag
                    )
                    .result()
                    .ifPresent(
                            team::add
                    );
        }
    }
}