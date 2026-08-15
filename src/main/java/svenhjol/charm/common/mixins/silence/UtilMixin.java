package svenhjol.charm.common.mixins.silence;

import net.minecraft.Util;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import svenhjol.charm.common.features.silence.Silence;

@Mixin(Util.class)
public class UtilMixin {
    @Redirect(method = "doFetchChoiceType", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private static void charm$silenceDataFixerError(Logger logger, String message, Object entity) {
        if (Silence.downgradeDataFixerError()) logger.info(message, entity);
        else logger.error(message, entity);
    }
}
