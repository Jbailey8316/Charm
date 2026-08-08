package svenhjol.charm.common.features.coral_squids.common;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CoralSquidHeadBlock extends HorizontalDirectionalBlock {
    private static final MapCodec<CoralSquidHeadBlock> CODEC = simpleCodec(CoralSquidHeadBlock::new);
    private static final VoxelShape NORTH = Block.box(4, 4, 7, 12, 12, 13);
    private static final VoxelShape SOUTH = Block.box(4, 4, 3, 12, 12, 9);
    private static final VoxelShape EAST = Block.box(3, 4, 4, 9, 12, 12);
    private static final VoxelShape WEST = Block.box(7, 4, 4, 13, 12, 12);
    public CoralSquidHeadBlock(ResourceKey<Block> key) { this(Properties.ofFullCopy(Blocks.PLAYER_HEAD).noOcclusion().setId(key)); }
    private CoralSquidHeadBlock(Properties properties) { super(properties); registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)); }
    @Override protected MapCodec<? extends HorizontalDirectionalBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return switch (state.getValue(FACING)) { case NORTH -> NORTH; case SOUTH -> SOUTH; case EAST -> EAST; case WEST -> WEST; default -> NORTH; }; }
}
