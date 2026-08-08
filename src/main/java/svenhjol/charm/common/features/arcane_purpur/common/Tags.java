package svenhjol.charm.common.features.arcane_purpur.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import svenhjol.charmony.core.Charmony;

public final class Tags {
    public static final TagKey<Block> CHORUS_TELEPORTS = TagKey.create(Registries.BLOCK, Charmony.id("chorus_teleports"));
    private Tags() {}
}
