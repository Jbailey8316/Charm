package svenhjol.charm.common.mixins.anvils_last_longer;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.common.features.anvils_last_longer.AnvilsLastLonger;
import svenhjol.charmony.core.base.Mod;

/** Applies Charm's damage roll before vanilla advances the anvil state. */
@Mixin(AnvilBlock.class)
public abstract class AnvilBlockMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private static void charm$modifyDamageChance(BlockState state, CallbackInfoReturnable<BlockState> cir) {
        var feature = Mod.tryGetSidedFeature(AnvilsLastLonger.class).orElse(null);
        if (feature != null && feature.enabled() && RandomSource.create().nextFloat() >= feature.chanceToDamage()) {
            cir.setReturnValue(state);
        }
    }
}
