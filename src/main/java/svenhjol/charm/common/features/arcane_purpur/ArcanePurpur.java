package svenhjol.charm.common.features.arcane_purpur;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import svenhjol.charm.common.features.arcane_purpur.common.Handlers;
import svenhjol.charm.common.features.arcane_purpur.common.Registers;
import svenhjol.charm.common.features.arcane_purpur.common.Advancements;
import svenhjol.charm.common.features.arcane_purpur.common.Tags;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, description = "Adds Arcane Purpur blocks and Chorus Fruit teleportation.")
public final class ArcanePurpur extends SidedFeature {
    @Configurable(name = "Teleport range", description = "Range in blocks for Chiseled Arcane Purpur Chorus Fruit teleportation.", requireRestart = false)
    private static int teleportRange = 12;
    public final Registers registers;
    public final Handlers handlers;
    public final Advancements advancements;

    public ArcanePurpur(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
        advancements = new Advancements(this);
    }

    public int teleportRange() { return Mth.clamp(teleportRange, 0, 64); }
    public static ArcanePurpur feature() { return Mod.getSidedFeature(ArcanePurpur.class); }
}
