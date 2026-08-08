package svenhjol.charm.common.mixins.copper_pistons;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
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

    @Unique
    private boolean isCopper() {
        var feature = Mod.tryGetSidedFeature(CopperPistons.class).orElse(null);
        return feature != null && getBlockState().is(feature.registers.movingCopperPistonBlock.get());
    }
}
