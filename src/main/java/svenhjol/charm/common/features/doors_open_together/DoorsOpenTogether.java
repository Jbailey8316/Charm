package svenhjol.charm.common.features.doors_open_together;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DoorBlock;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, description = "Opens compatible adjacent doors together.")
public final class DoorsOpenTogether extends SidedFeature {
    public final Handlers handlers;

    public DoorsOpenTogether(Mod mod) {
        super(mod);
        handlers = new Handlers();
    }

    public static DoorsOpenTogether feature() {
        return Mod.getSidedFeature(DoorsOpenTogether.class);
    }

    public static final class Handlers {
        private final Set<BlockPos> synchronizedPositions = new HashSet<>();

        public boolean isSynchronizing(BlockPos pos) {
            return synchronizedPositions.contains(pos);
        }

        public void synchronizeNeighbour(net.minecraft.world.level.Level level,
                                         net.minecraft.world.level.block.state.BlockState state,
                                         BlockPos pos, boolean open) {
            if (synchronizedPositions.contains(pos) || !(state.getBlock() instanceof DoorBlock)) {
                return;
            }

            var facing = state.getValue(net.minecraft.world.level.block.DoorBlock.FACING);
            var hinge = state.getValue(net.minecraft.world.level.block.DoorBlock.HINGE);
            var offset = hinge == net.minecraft.world.level.block.state.properties.DoorHingeSide.RIGHT
                ? facing.getCounterClockWise() : facing.getClockWise();
            var neighbourPos = pos.relative(offset);
            var neighbourState = level.getBlockState(neighbourPos);
            boolean isDoor = neighbourState.getBlock() instanceof DoorBlock;
            if (!isDoor) return;
            var neighbour = (DoorBlock) neighbourState.getBlock();
            boolean sameFacing = neighbourState.getValue(DoorBlock.FACING) == facing;
            boolean oppositeHinge = neighbourState.getValue(DoorBlock.HINGE) != hinge;
            boolean differentOpen = neighbourState.getValue(DoorBlock.OPEN) != open;
            if (!sameFacing || !oppositeHinge || !differentOpen) return;

            synchronizedPositions.add(neighbourPos);
            try {
                // setOpen's only state mutation is this update; applying it directly keeps
                // the synchronized door from producing a second open/close sound.
                level.setBlock(neighbourPos,
                    neighbourState.setValue(DoorBlock.OPEN, open), 10);
            } finally {
                synchronizedPositions.remove(neighbourPos);
            }
        }
    }
}
