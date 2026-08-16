package svenhjol.charm.common.mixins.atlases;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charm.common.features.atlases.AtlasHandlers;

import java.util.function.Predicate;

/** Lets vanilla map decoration tracking recognize MapIds nested in an Atlas. */
@Mixin(MapItemSavedData.class)
public abstract class MapItemSavedDataMixin {
    @ModifyExpressionValue(
        method = "tickCarriedBy",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;contains(Ljava/util/function/Predicate;)Z")
    )
    private boolean charm$atlasContainsMap(boolean contains, @Local(argsOnly = true) Player player,
                                            @Local Predicate<ItemStack> matcher) {
        return contains || AtlasHandlers.atlasContainsMap(player, matcher);
    }
}
