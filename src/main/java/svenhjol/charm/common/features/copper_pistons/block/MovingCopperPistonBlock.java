package svenhjol.charm.common.features.copper_pistons.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.MovingPistonBlock;

public final class MovingCopperPistonBlock extends MovingPistonBlock {
    public MovingCopperPistonBlock(Properties properties) {
        super(properties);
    }
    public static Properties createProperties() { return Properties.ofFullCopy(Blocks.MOVING_PISTON); }
}
