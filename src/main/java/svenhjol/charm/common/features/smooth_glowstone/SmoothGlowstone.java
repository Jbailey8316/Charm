package svenhjol.charm.common.features.smooth_glowstone;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;

/** Historical Charm block made by firing Glowstone. */
@FeatureDefinition(side = Side.Common, description = "Adds a smooth, solid Glowstone block.")
public final class SmoothGlowstone extends SidedFeature {
    public final Registerable<Block> block;
    public final Registerable<Item> item;

    public SmoothGlowstone(Mod mod) {
        super(mod);
        block = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.BLOCK, id("smooth_glowstone"),
            new Block(Block.Properties.ofFullCopy(Blocks.GLOWSTONE)
                .setId(ResourceKey.create(Registries.BLOCK, id("smooth_glowstone"))))));
        item = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.ITEM, id("smooth_glowstone"),
            new BlockItem(block.get(), new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id("smooth_glowstone"))))));
    }

    public static SmoothGlowstone feature() {
        return Mod.getSidedFeature(SmoothGlowstone.class);
    }
}
