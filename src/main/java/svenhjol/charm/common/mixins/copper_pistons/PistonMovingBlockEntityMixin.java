package svenhjol.charm.common.mixins.copper_pistons;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charm.common.features.copper_pistons.CopperPistons;
import svenhjol.charmony.core.base.Mod;

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin extends BlockEntity {
    protected PistonMovingBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    @ModifyReceiver(method = "getCollisionRelatedBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;defaultBlockState()Lnet/minecraft/world/level/block/state/BlockState;"))
    private Block charm$useCopperHead(Block original) {
        var feature = Mod.tryGetSidedFeature(CopperPistons.class).orElse(null);
        return isCopper() && feature != null && feature.enabled() ? feature.registers.copperPistonHeadBlock.get() : original;
    }

    @org.spongepowered.asm.mixin.injection.Redirect(
        method = {"tick", "finalTick"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean charm$recognizeCopperMovingPiston(BlockState state, Block block) {
        var feature = Mod.tryGetSidedFeature(CopperPistons.class).orElse(null);
        if (feature != null && feature.enabled() && block == Blocks.MOVING_PISTON
            && state.is(feature.registers.movingCopperPistonBlock.get())) return true;
        return state.is(block);
    }

    @ModifyReceiver(
        method = "finalTick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;defaultBlockState()Lnet/minecraft/world/level/block/state/BlockState;"))
    private Block charm$useCopperHeadAtFinalTick(Block original) {
        var feature = Mod.tryGetSidedFeature(CopperPistons.class).orElse(null);
        return isCopper() && feature != null && feature.enabled() && original == Blocks.PISTON_HEAD
            ? feature.registers.copperPistonHeadBlock.get() : original;
    }

    @Unique
    private boolean isCopper() {
        var feature = Mod.tryGetSidedFeature(CopperPistons.class).orElse(null);
        return feature != null && getBlockState().is(feature.registers.movingCopperPistonBlock.get());
    }
}
