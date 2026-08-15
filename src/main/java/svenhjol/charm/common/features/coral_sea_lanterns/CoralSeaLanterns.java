package svenhjol.charm.common.features.coral_sea_lanterns;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;

import java.util.Optional;
import java.util.List;

/** Colored Sea Lantern variants made from the five vanilla living corals. */
@FeatureDefinition(side = Side.Common, description = "Adds colored Sea Lanterns made from living coral.")
public final class CoralSeaLanterns extends SidedFeature {
    public final Registerable<Block> tubeBlock;
    public final Registerable<Block> brainBlock;
    public final Registerable<Block> bubbleBlock;
    public final Registerable<Block> fireBlock;
    public final Registerable<Block> hornBlock;
    public final Registerable<Item> tubeItem;
    public final Registerable<Item> brainItem;
    public final Registerable<Item> bubbleItem;
    public final Registerable<Item> fireItem;
    public final Registerable<Item> hornItem;

    public CoralSeaLanterns(Mod mod) {
        super(mod);
        tubeBlock = block("tube_sea_lantern");
        brainBlock = block("brain_sea_lantern");
        bubbleBlock = block("bubble_sea_lantern");
        fireBlock = block("fire_sea_lantern");
        hornBlock = block("horn_sea_lantern");
        tubeItem = item("tube_sea_lantern", tubeBlock);
        brainItem = item("brain_sea_lantern", brainBlock);
        bubbleItem = item("bubble_sea_lantern", bubbleBlock);
        fireItem = item("fire_sea_lantern", fireBlock);
        hornItem = item("horn_sea_lantern", hornBlock);
    }

    private Registerable<Block> block(String id) {
        var lootTable = ResourceKey.create(Registries.LOOT_TABLE, this.id("blocks/" + id));
        return new Registerable<>(this, () -> Registry.register(BuiltInRegistries.BLOCK, this.id(id),
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SEA_LANTERN)
                .overrideLootTable(Optional.of(lootTable))
                .setId(ResourceKey.create(Registries.BLOCK, this.id(id))))));
    }

    private Registerable<Item> item(String id, Registerable<Block> block) {
        return new Registerable<>(this, () -> Registry.register(BuiltInRegistries.ITEM, this.id(id),
            new BlockItem(block.get(), new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, this.id(id))))));
    }

    public List<Registerable<Block>> blocks() {
        return List.of(tubeBlock, brainBlock, bubbleBlock, fireBlock, hornBlock);
    }

    public List<Registerable<Item>> items() {
        return List.of(tubeItem, brainItem, bubbleItem, fireItem, hornItem);
    }

    public static CoralSeaLanterns feature() {
        return Mod.getSidedFeature(CoralSeaLanterns.class);
    }
}
