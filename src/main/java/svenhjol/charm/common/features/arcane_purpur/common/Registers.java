package svenhjol.charm.common.features.arcane_purpur.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import svenhjol.charm.common.features.arcane_purpur.ArcanePurpur;
import svenhjol.charmony.core.base.Registerable;

public final class Registers {
    public final Registerable<Block> block, slab, stairs, glyphBlock, chiseledBlock, chiseledGlyphBlock;
    public final Registerable<Item> blockItem, slabItem, stairsItem, glyphBlockItem, chiseledBlockItem, chiseledGlyphBlockItem;

    public Registers(ArcanePurpur feature) {
        block = block(feature, "arcane_purpur_block", () -> new Block(Block.Properties.ofFullCopy(Blocks.PURPUR_BLOCK).setId(ResourceKey.create(Registries.BLOCK, feature.registryId("arcane_purpur_block")))));
        blockItem = item(feature, "arcane_purpur_block", block);
        slab = block(feature, "arcane_purpur_slab", () -> new SlabBlock(Block.Properties.ofFullCopy(Blocks.PURPUR_SLAB).setId(ResourceKey.create(Registries.BLOCK, feature.registryId("arcane_purpur_slab")))));
        slabItem = item(feature, "arcane_purpur_slab", slab);
        stairs = block(feature, "arcane_purpur_stairs", () -> new StairBlock(block.get().defaultBlockState(), Block.Properties.ofFullCopy(Blocks.PURPUR_STAIRS).setId(ResourceKey.create(Registries.BLOCK, feature.registryId("arcane_purpur_stairs")))));
        stairsItem = item(feature, "arcane_purpur_stairs", stairs);
        glyphBlock = block(feature, "arcane_purpur_glyph_block", () -> new Block(Block.Properties.ofFullCopy(Blocks.PURPUR_BLOCK).setId(ResourceKey.create(Registries.BLOCK, feature.registryId("arcane_purpur_glyph_block")))));
        glyphBlockItem = item(feature, "arcane_purpur_glyph_block", glyphBlock);
        chiseledBlock = block(feature, "chiseled_arcane_purpur_block", () -> new Block(Block.Properties.ofFullCopy(Blocks.PURPUR_BLOCK).setId(ResourceKey.create(Registries.BLOCK, feature.registryId("chiseled_arcane_purpur_block")))));
        chiseledBlockItem = item(feature, "chiseled_arcane_purpur_block", chiseledBlock);
        chiseledGlyphBlock = block(feature, "chiseled_arcane_purpur_glyph_block", () -> new Block(Block.Properties.ofFullCopy(Blocks.PURPUR_BLOCK).setId(ResourceKey.create(Registries.BLOCK, feature.registryId("chiseled_arcane_purpur_glyph_block")))));
        chiseledGlyphBlockItem = item(feature, "chiseled_arcane_purpur_glyph_block", chiseledGlyphBlock);
    }

    private static Registerable<Block> block(ArcanePurpur feature, String id, java.util.function.Supplier<Block> supplier) {
        return new Registerable<>(feature, () -> Registry.register(BuiltInRegistries.BLOCK, feature.registryId(id), supplier.get()));
    }
    private static Registerable<Item> item(ArcanePurpur feature, String id, Registerable<Block> block) {
        return new Registerable<>(feature, () -> Registry.register(BuiltInRegistries.ITEM, feature.registryId(id), new BlockItem(block.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, feature.registryId(id))))));
    }
}
