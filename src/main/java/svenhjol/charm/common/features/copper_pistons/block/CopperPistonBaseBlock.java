package svenhjol.charm.common.features.copper_pistons.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;

public final class CopperPistonBaseBlock extends PistonBaseBlock {
    public CopperPistonBaseBlock(Properties properties) {
        super(false, properties);
    }
    public static Properties createProperties() { return Properties.ofFullCopy(Blocks.PISTON).isRedstoneConductor((state, getter, pos) -> false); }
}
