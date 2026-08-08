package svenhjol.charm.common.features.coral_squids.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import svenhjol.charmony.core.Charmony;

public final class Tags {
    public static final TagKey<Biome> SPAWNS_CORAL_SQUIDS = TagKey.create(Registries.BIOME,
        Charmony.id("spawns_coral_squids"));
    private Tags() {}
}
