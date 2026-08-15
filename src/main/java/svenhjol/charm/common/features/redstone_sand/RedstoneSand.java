package svenhjol.charm.common.features.redstone_sand;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import com.mojang.serialization.MapCodec;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;

/** Historical Charm sand block that emits a full redstone signal. */
@FeatureDefinition(side = Side.Common, description = "Adds sand that emits a full redstone signal.")
public final class RedstoneSand extends SidedFeature {
    public final Registerable<Block> block;
    public final Registerable<Item> item;

    public RedstoneSand(Mod mod) {
        super(mod);
        block = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.BLOCK, id("redstone_sand"),
            new RedstoneSandBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_RED)
                .sound(SoundType.SAND)
                .strength(0.5f)
                .setId(ResourceKey.create(Registries.BLOCK, id("redstone_sand"))))));
        item = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.ITEM, id("redstone_sand"),
            new BlockItem(block.get(), new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id("redstone_sand"))))));
    }

    public static RedstoneSand feature() {
        return Mod.getSidedFeature(RedstoneSand.class);
    }

    private static final class RedstoneSandBlock extends FallingBlock {
        private static final MapCodec<RedstoneSandBlock> CODEC = simpleCodec(RedstoneSandBlock::new);

        private RedstoneSandBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        protected MapCodec<? extends FallingBlock> codec() {
            return CODEC;
        }

        @Override
        protected boolean isSignalSource(BlockState state) {
            return true;
        }

        @Override
        protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 15;
        }

        // FallingBlock's 1.19.2 default was white; the historical block did not override it.
        @Override
        public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
            return 0xFFFFFF;
        }

    }
}
