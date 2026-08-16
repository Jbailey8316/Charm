package svenhjol.charm.common.features.item_hover_sorting.common;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import svenhjol.charm.common.features.item_hover_sorting.*;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;
import java.util.*;
public final class Handlers extends Setup<ItemHoverSorting> {
    public Handlers(ItemHoverSorting f){super(f);}
    public void sort(net.minecraft.world.entity.player.Player player,Networking.C2SSort packet){
        if(!(player instanceof ServerPlayer server)||packet.slot()<0||packet.slot()>=player.containerMenu.slots.size())return;
        Slot slot=player.containerMenu.getSlot(packet.slot()); ItemStack stack=slot.getItem();
        if(stack.is(Items.BUNDLE)){var c=stack.get(DataComponents.BUNDLE_CONTENTS);if(c==null)return;List<ItemStack> a=new ArrayList<>();c.itemsCopy().forEach(a::add);sort(a,packet.direction());stack.set(DataComponents.BUNDLE_CONTENTS,new BundleContents(a));slot.setChanged();server.containerMenu.broadcastChanges();AdvancementHelper.trigger("sorted_items_while_hovering", server);return;}
        if(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock){var c=stack.get(DataComponents.CONTAINER);if(c==null)return;List<ItemStack>a=new ArrayList<>();c.nonEmptyItemsCopy().forEach(a::add);sort(a,packet.direction());stack.set(DataComponents.CONTAINER,ItemContainerContents.fromItems(a));slot.setChanged();server.containerMenu.broadcastChanges();AdvancementHelper.trigger("sorted_items_while_hovering", server);}
    }
    private static void sort(List<ItemStack>a,int d){var c=Comparator.comparing((ItemStack s)->s.getItem().builtInRegistryHolder().key().location().toString());a.sort(d<0?c.reversed():c);}
}
