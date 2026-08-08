package svenhjol.charm.common.features.copper_pistons.common;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import svenhjol.charm.common.features.copper_pistons.CopperPistons;
import svenhjol.charm.common.features.copper_pistons.block.CopperPistonBaseBlock;
import svenhjol.charm.common.features.copper_pistons.block.CopperPistonHeadBlock;
import svenhjol.charm.common.features.copper_pistons.block.MovingCopperPistonBlock;
import svenhjol.charm.common.features.copper_pistons.block.StickyCopperPistonBaseBlock;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.common.CommonRegistry;

import java.util.List;
import java.util.function.Supplier;

public final class Registers extends Setup<CopperPistons> {
    public final Supplier<Block> copperPistonBlock;
    public final Supplier<Block> copperPistonHeadBlock;
    public final Supplier<Block> movingCopperPistonBlock;
    public final Supplier<Block> stickyCopperPistonBlock;
    public final Supplier<Item> copperPistonItem;
    public final Supplier<Item> stickyCopperPistonItem;

    public Registers(CopperPistons feature) {
        super(feature);
        var registry = CommonRegistry.forFeature(feature);
        copperPistonBlock = registerBlock("copper_piston", key -> new CopperPistonBaseBlock(CopperPistonBaseBlock.createProperties().setId(key)));
        copperPistonHeadBlock = registerBlock("copper_piston_head", key -> new CopperPistonHeadBlock(CopperPistonHeadBlock.createProperties().setId(key)));
        movingCopperPistonBlock = registerBlock("moving_copper_piston", key -> new MovingCopperPistonBlock(MovingCopperPistonBlock.createProperties().setId(key)));
        stickyCopperPistonBlock = registerBlock("sticky_copper_piston", key -> new StickyCopperPistonBaseBlock(StickyCopperPistonBaseBlock.createProperties().setId(key)));
        copperPistonItem = registerItem("copper_piston", key -> new BlockItem(copperPistonBlock.get(), new Item.Properties().setId(key)));
        stickyCopperPistonItem = registerItem("sticky_copper_piston", key -> new BlockItem(stickyCopperPistonBlock.get(), new Item.Properties().setId(key)));
    }

    private <B extends Block> Registerable<B> registerBlock(String id, java.util.function.Function<ResourceKey<Block>, B> factory) {
        return new Registerable<>(feature(), () -> {
            var key = ResourceKey.create(Registries.BLOCK, feature().id(id));
            return Registry.register(BuiltInRegistries.BLOCK, feature().id(id), factory.apply(key));
        });
    }

    private <I extends Item> Registerable<I> registerItem(String id, java.util.function.Function<ResourceKey<Item>, I> factory) {
        return new Registerable<>(feature(), () -> {
            var key = ResourceKey.create(Registries.ITEM, feature().id(id));
            return Registry.register(BuiltInRegistries.ITEM, feature().id(id), factory.apply(key));
        });
    }

    @Override
    public Runnable boot() {
        return () -> CommonRegistry.forFeature(feature()).blocksForBlockEntity(
            () -> BlockEntityType.PISTON, List.of(movingCopperPistonBlock));
    }
}
