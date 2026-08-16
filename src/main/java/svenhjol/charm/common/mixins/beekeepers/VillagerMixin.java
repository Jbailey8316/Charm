package svenhjol.charm.common.mixins.beekeepers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.common.features.beekeepers.Beekeepers;
import svenhjol.charmony.core.helpers.AdvancementHelper;

@Mixin(Villager.class)
public abstract class VillagerMixin {
    @Inject(method = "startTrading", at = @At("HEAD"))
    private void charm$triggerBeekeeperTrade(net.minecraft.world.entity.player.Player player, CallbackInfo ci) {
        if (player instanceof ServerPlayer serverPlayer
            && ((Villager) (Object) this).getVillagerData().profession().is(Beekeepers.feature().professionKey())) {
            AdvancementHelper.trigger("traded_with_beekeeper", serverPlayer);
        }
    }
}
