package svenhjol.charm.client.mixins.silence;

import com.mojang.realmsclient.RealmsAvailability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.common.features.silence.Silence;
import java.util.concurrent.CompletableFuture;

@Mixin(RealmsAvailability.class)
public class RealmsAvailabilityMixin {
    @Inject(method = "check", at = @At("HEAD"), cancellable = true)
    private static void charm$silenceRealms(CallbackInfoReturnable<CompletableFuture<RealmsAvailability.Result>> cir) {
        if (Silence.disableDevEnvironmentConnections()) cir.setReturnValue(CompletableFuture.supplyAsync(() -> new RealmsAvailability.Result(RealmsAvailability.Type.INCOMPATIBLE_CLIENT)));
    }
}
