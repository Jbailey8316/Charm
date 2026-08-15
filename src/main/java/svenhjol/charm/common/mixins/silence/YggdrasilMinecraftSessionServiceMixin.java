package svenhjol.charm.common.mixins.silence;

import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.common.features.silence.Silence;
import java.util.UUID;

@Mixin(YggdrasilMinecraftSessionService.class)
public class YggdrasilMinecraftSessionServiceMixin {
    @Inject(method = "fetchProfileUncached", at = @At("HEAD"), cancellable = true, remap = false)
    private void charm$silenceProfileFetch(UUID profileId, boolean requireSecure, CallbackInfoReturnable<ProfileResult> cir) {
        if (Silence.disableDevEnvironmentConnections()) cir.setReturnValue(null);
    }
}
