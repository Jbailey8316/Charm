package svenhjol.charm.common.features.copper_pistons.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonHeadBlock;

public final class CopperPistonHeadBlock extends PistonHeadBlock {
    public CopperPistonHeadBlock(Properties properties) {
        super(properties);
    }
    public static Properties createProperties() { return Properties.ofFullCopy(Blocks.PISTON_HEAD); }
}
