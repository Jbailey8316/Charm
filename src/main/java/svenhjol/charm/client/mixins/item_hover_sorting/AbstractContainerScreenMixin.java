package svenhjol.charm.client.mixins.item_hover_sorting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.common.features.item_hover_sorting.Networking;
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
 @Accessor("hoveredSlot") protected abstract Slot charm$getHoveredSlot();
 @Inject(method="mouseScrolled",at=@At("HEAD"),cancellable=true)
 private void charm$itemHoverSort(double x,double y,double horizontal,double vertical,CallbackInfoReturnable<Boolean> cir){var slot=((AbstractContainerScreenMixin)(Object)this).charm$getHoveredSlot();if(slot!=null){var s=slot.getItem();if(s.is(Items.BUNDLE)||net.minecraft.world.level.block.Block.byItem(s.getItem()) instanceof net.minecraft.world.level.block.ShulkerBoxBlock){Networking.C2SSort.send(slot.index,vertical>0?1:-1);cir.setReturnValue(true);}}}
}
