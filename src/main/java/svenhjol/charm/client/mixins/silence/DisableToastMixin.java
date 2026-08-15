package svenhjol.charm.client.mixins.silence;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import svenhjol.charm.common.features.silence.Silence;

@Mixin(ClientPacketListener.class)
public class DisableToastMixin {
    @Redirect(method = "handleLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/toasts/ToastManager;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"))
    private void charm$silenceVerificationToast(ToastManager instance, Toast toast) {
        if (!Silence.disableChatMessageVerification()) instance.addToast(toast);
    }
}
