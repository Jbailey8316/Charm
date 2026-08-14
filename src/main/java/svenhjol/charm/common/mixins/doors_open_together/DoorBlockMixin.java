package svenhjol.charm.common.mixins.doors_open_together;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.redstone.Orientation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charm.common.features.doors_open_together.DoorsOpenTogether;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin {
    @Inject(method = "setOpen", at = @At("HEAD"))
    private void charm$synchronizeNeighbour(Entity entity, Level level, BlockState state, BlockPos pos,
                                             boolean open, CallbackInfo ci) {
        var feature = Mod.tryGetSidedFeature(DoorsOpenTogether.class).orElse(null);
        if (feature != null && !level.isClientSide() && !feature.handlers.isSynchronizing(pos)) {
            feature.handlers.synchronizeNeighbour(level, state, pos, open);
        }
    }

    @Inject(method = "useWithoutItem", at = @At("RETURN"))
    private void charm$synchronizeManualInteraction(BlockState state, Level level, BlockPos pos, Player player,
                                                     BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        var feature = Mod.tryGetSidedFeature(DoorsOpenTogether.class).orElse(null);
        if (feature != null && !level.isClientSide() && cir.getReturnValue().consumesAction()) {
            feature.handlers.synchronizeNeighbour(level, state, pos,
                level.getBlockState(pos).getValue(DoorBlock.OPEN));
        }
    }

    @Inject(method = "neighborChanged", at = @At("RETURN"))
    private void charm$synchronizeRedstone(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block,
                                            Orientation orientation, boolean notify, CallbackInfo ci) {
        var feature = Mod.tryGetSidedFeature(DoorsOpenTogether.class).orElse(null);
        if (feature != null && !level.isClientSide() && !feature.handlers.isSynchronizing(pos)) {
            var current = level.getBlockState(pos);
            if (current.getBlock() instanceof DoorBlock) {
                feature.handlers.synchronizeNeighbour(level, current, pos, current.getValue(DoorBlock.OPEN));
            }
        }
    }

}
