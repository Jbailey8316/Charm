package svenhjol.charm.common.mixins.aerial_affinity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.common.features.aerial_affinity.AerialAffinity;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.helpers.AdvancementHelper;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void charm$applyAerialAffinity(net.minecraft.world.level.block.state.BlockState state,
                                           CallbackInfoReturnable<Float> cir) {
        Player player = (Player)(Object)this;
        var feature = Mod.tryGetSidedFeature(AerialAffinity.class).orElse(null);
        if (feature == null) return;
        if (!feature.enabled() || player.onGround()) return;

        var value = player.getAttributeValue(feature.attribute.get());
        if (value > 0.0d) {
            cir.setReturnValue(cir.getReturnValue() * 5.0f);
            if (player instanceof ServerPlayer) {
                AdvancementHelper.trigger("used_aerial_affinity", player);
            }
        }
    }
}
