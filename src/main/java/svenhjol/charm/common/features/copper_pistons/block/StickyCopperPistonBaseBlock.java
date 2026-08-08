package svenhjol.charm.common.features.copper_pistons.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;

public final class StickyCopperPistonBaseBlock extends PistonBaseBlock {
    public StickyCopperPistonBaseBlock(Properties properties) {
        super(true, properties);
    }
    public static Properties createProperties() { return Properties.ofFullCopy(Blocks.STICKY_PISTON).isRedstoneConductor((state, getter, pos) -> false); }
}
