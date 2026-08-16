package svenhjol.charm.client.mixins.atlases;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import svenhjol.charm.common.features.atlases.AtlasData;
import svenhjol.charm.common.features.atlases.AtlasMapEntry;
import svenhjol.charm.common.features.atlases.Atlases;

/** Substitutes the active vanilla map only for the held-item render path. */
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow private ItemStack mainHandItem;

    @ModifyVariable(method = "renderArmWithItem", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private ItemStack charm$renderActiveAtlasMap(ItemStack stack) {
        return charm$activeMap(stack);
    }

    @Redirect(method = "renderTwoHandedMap", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;mainHandItem:Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack charm$renderTwoHandedActiveAtlasMap(ItemInHandRenderer renderer) {
        return charm$activeMap(mainHandItem);
    }

    private ItemStack charm$activeMap(ItemStack stack) {
        var atlas = Atlases.feature();
        if (atlas == null || !stack.is(atlas.item.get())) return stack;
        AtlasData data = stack.get(atlas.data.get());
        if (data == null || data.activeMap() < 0 || data.activeMap() >= data.maps().size()) return stack;
        AtlasMapEntry entry = data.maps().get(data.activeMap());
        return entry.map();
    }
}
