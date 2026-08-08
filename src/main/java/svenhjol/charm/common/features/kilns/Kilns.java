package svenhjol.charm.common.features.kilns;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.crafting.RecipePropertySet;
import svenhjol.charm.common.features.firing.Firing;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, description = "A functional block that speeds up cooking of clay, glass, bricks and terracotta.")
public final class Kilns extends SidedFeature {
    public final Registerable<Block> block;
    public final Registerable<Item> item;
    public final Registerable<BlockEntityType<KilnBlockEntity>> blockEntity;
    public final Registerable<MenuType<KilnMenu>> menu;

    public Kilns(Mod mod) {
        super(mod);
        block = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.BLOCK, id("kiln"),
            new KilnBlock(Block.Properties.ofFullCopy(Blocks.FURNACE)
                .lightLevel(s -> s.getValue(BlockStateProperties.LIT) ? 13 : 0)
                .setId(ResourceKey.create(Registries.BLOCK, id("kiln"))))));
        item = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.ITEM, id("kiln"),
            new BlockItem(block.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("kiln"))))));
        blockEntity = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("kiln"),
            FabricBlockEntityTypeBuilder.create(KilnBlockEntity::new, block.get()).build()));
        menu = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.MENU, id("kiln"),
            new MenuType<>(KilnMenu::new, net.minecraft.world.flag.FeatureFlags.VANILLA_SET)));
    }

    public static final class KilnBlock extends AbstractFurnaceBlock {
        private static final MapCodec<KilnBlock> CODEC = simpleCodec(KilnBlock::new);
        public KilnBlock(Properties properties) { super(properties); }
        @Override protected MapCodec<? extends AbstractFurnaceBlock> codec() { return CODEC; }
        @Override public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(net.minecraft.core.BlockPos pos, BlockState state) { return new KilnBlockEntity(pos, state); }
        @Override public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(net.minecraft.world.level.Level level, BlockState state, BlockEntityType<T> type) { return createFurnaceTicker(level, type, Mod.getSidedFeature(Kilns.class).blockEntity.get()); }
        @Override protected void openContainer(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player) { if (level.getBlockEntity(pos) instanceof KilnBlockEntity kiln) player.openMenu(kiln); }
    }

    public static final class KilnBlockEntity extends AbstractFurnaceBlockEntity {
        public KilnBlockEntity(net.minecraft.core.BlockPos pos, BlockState state) { super(Mod.getSidedFeature(Kilns.class).blockEntity.get(), pos, state, Mod.getSidedFeature(Firing.class).recipeType.get()); }
        @Override protected Component getDefaultName() { return Component.translatable("container.charm.kiln"); }
        @Override protected AbstractContainerMenu createMenu(int syncId, Inventory inventory) { return new KilnMenu(syncId, inventory, this, dataAccess); }
    }

    public static final class KilnMenu extends AbstractFurnaceMenu {
        private static final ResourceKey<RecipePropertySet> FIRING_KEY = ResourceKey.create(RecipePropertySet.TYPE_KEY, Mod.getSidedFeature(Firing.class).id("firing"));
        public KilnMenu(int id, Inventory inventory) { super(Mod.getSidedFeature(Kilns.class).menu.get(), Mod.getSidedFeature(Firing.class).recipeType.get(), FIRING_KEY, RecipeBookType.SMOKER, id, inventory); }
        public KilnMenu(int id, Inventory inventory, net.minecraft.world.Container container, net.minecraft.world.inventory.ContainerData data) { super(Mod.getSidedFeature(Kilns.class).menu.get(), Mod.getSidedFeature(Firing.class).recipeType.get(), FIRING_KEY, RecipeBookType.SMOKER, id, inventory, container, data); }
    }
}
