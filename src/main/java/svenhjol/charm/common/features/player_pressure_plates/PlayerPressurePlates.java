package svenhjol.charm.common.features.player_pressure_plates;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;

@FeatureDefinition(side = Side.Common, description = "Adds a pressure plate activated only by players.")
public final class PlayerPressurePlates extends svenhjol.charmony.core.base.SidedFeature {
    public final Registerable<Block> block;
    public final Registerable<Item> item;

    public PlayerPressurePlates(Mod mod) {
        super(mod);
        block = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.BLOCK, id("player_pressure_plate"),
            new PlayerPressurePlateBlock(Block.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE)
                .strength(2.0f, 1200.0f)
                .requiresCorrectToolForDrops()
                .setId(ResourceKey.create(Registries.BLOCK, id("player_pressure_plate"))))));
        item = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.ITEM, id("player_pressure_plate"),
            new BlockItem(block.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("player_pressure_plate"))))));
    }

    public static PlayerPressurePlates feature() { return Mod.getSidedFeature(PlayerPressurePlates.class); }

    private static final class PlayerPressurePlateBlock extends WeightedPressurePlateBlock {
        private PlayerPressurePlateBlock(Properties properties) { super(15, BlockSetType.STONE, properties); }

        @Override
        protected int getSignalStrength(Level level, BlockPos pos) {
            var box = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 0.25, pos.getZ() + 1.0);
            return level.getEntitiesOfClass(Player.class, box, Player::isAlive).isEmpty() ? 0 : 15;
        }
    }
}
