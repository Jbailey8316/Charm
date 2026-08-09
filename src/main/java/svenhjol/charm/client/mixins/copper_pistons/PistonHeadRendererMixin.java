package svenhjol.charm.client.mixins.copper_pistons;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import svenhjol.charm.common.features.copper_pistons.CopperPistons;
import svenhjol.charmony.core.base.Mod;

@Mixin(PistonHeadRenderer.class)
public abstract class PistonHeadRendererMixin {
    @Redirect(
        method = "extractRenderState",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean charm$recognizeCopperHead(BlockState state, Block block) {
        var feature = Mod.tryGetSidedFeature(CopperPistons.class).orElse(null);
        if (feature != null && feature.enabled() && block == Blocks.PISTON_HEAD
            && state.is(feature.registers.copperPistonHeadBlock.get())) {
            return true;
        }
        return state.is(block);
    }
}
