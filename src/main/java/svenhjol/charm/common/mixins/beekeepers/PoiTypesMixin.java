package svenhjol.charm.common.mixins.beekeepers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Gives the vanilla bee-hive POI one villager ticket, as in historical Charm. */
@Mixin(net.minecraft.world.entity.ai.village.poi.PoiTypes.class)
public final class PoiTypesMixin {
    @ModifyArg(
        method = "bootstrap(Lnet/minecraft/core/Registry;)Lnet/minecraft/world/entity/ai/village/poi/PoiType;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiTypes;register(Lnet/minecraft/core/Registry;Lnet/minecraft/resources/ResourceKey;Ljava/util/Set;II)Lnet/minecraft/world/entity/ai/village/poi/PoiType;", ordinal = 15),
        index = 3
    )
    private static int charm$enableBeehiveJobSite(int maxTickets) {
        return 1;
    }
}
