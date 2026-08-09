package svenhjol.charm.common.features.woodcutting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.List;

public final class WoodcutterMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final Container input = new SimpleContainer(1) {
        @Override public void setChanged() {
            super.setChanged();
            WoodcutterMenu.this.slotsChanged(this);
        }
    };
    private final ResultContainer result = new ResultContainer();
    private final DataSlot selected = DataSlot.standalone();
    private final Inventory inventory;
    private int generation;
    private int clientGeneration = -1;
    private List<ResourceLocation> clientRecipeIds = List.of();
    private List<ItemStack> clientRecipeOutputs = List.of();
    private List<RecipeHolder<WoodcuttingRecipe>> recipes = List.of();

    public WoodcutterMenu(int id, Inventory inv) { this(id, inv, ContainerLevelAccess.NULL); }
    public WoodcutterMenu(int id, Inventory inv, ContainerLevelAccess access) {
        super(Woodcutters.feature().menu.get(), id);
        this.inventory = inv;
        this.access = access;
        addDataSlot(selected);
        addSlot(new Slot(input, 0, 20, 33) {
            @Override public boolean mayPlace(ItemStack stack) {
                return !stack.isEmpty();
            }
        });
        addSlot(new Slot(result, 0, 143, 33) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
            @Override public void onTake(Player player, ItemStack stack) { takeResult(player); }
        });
        addStandardInventorySlots(inv, 8, 84);
    }

    private List<RecipeHolder<WoodcuttingRecipe>> findRecipes(ItemStack stack) {
        if (stack.isEmpty() || !(inventory.player instanceof ServerPlayer server)) return List.of();
        List<RecipeHolder<WoodcuttingRecipe>> found = new ArrayList<>();
        var allRecipes = server.level().getServer().getRecipeManager().getRecipes();
        allRecipes.forEach(holder -> {
            if (holder.value() instanceof WoodcuttingRecipe recipe && recipe.input().test(stack)) {
                @SuppressWarnings("unchecked") RecipeHolder<WoodcuttingRecipe> wood = (RecipeHolder<WoodcuttingRecipe>)(RecipeHolder<?>) holder;
                found.add(wood);
            }
        });
        return found;
    }

    private RecipeHolder<WoodcuttingRecipe> selectedRecipe() {
        return selected.get() >= 0 && selected.get() < recipes.size() ? recipes.get(selected.get()) : null;
    }

    private void refresh() {
        if (inventory.player.level().isClientSide()) return;
        ResourceLocation previous = selectedRecipe() == null ? null : selectedRecipe().id().location();
        recipes = findRecipes(input.getItem(0));
        int index = 0;
        if (previous != null) for (int i = 0; i < recipes.size(); i++) if (recipes.get(i).id().location().equals(previous)) { index = i; break; }
        selected.set(recipes.isEmpty() ? -1 : index);
        refreshResult();
        generation++;
        if (inventory.player instanceof ServerPlayer player) WoodcutterNetworking.send(player, this);
    }

    private void refreshResult() {
        RecipeHolder<WoodcuttingRecipe> holder = selectedRecipe();
        result.setItem(0, holder == null ? ItemStack.EMPTY : holder.value().assemble(new SingleRecipeInput(input.getItem(0)), inventory.player.level().registryAccess()));
    }

    private boolean reconcileRecipes() {
        List<RecipeHolder<WoodcuttingRecipe>> current = findRecipes(input.getItem(0));
        if (current.stream().map(holder -> holder.id().location()).toList().equals(recipeIds())) return true;
        recipes = current;
        selected.set(-1);
        refreshResult();
        generation++;
        if (inventory.player instanceof ServerPlayer player) WoodcutterNetworking.send(player, this);
        return false;
    }

    public int generation() { return generation; }
    public List<ResourceLocation> recipeIds() { return recipes.stream().map(holder -> holder.id().location()).toList(); }
    public int clientGeneration() { return clientGeneration; }
    public List<ResourceLocation> clientRecipeIds() { return clientRecipeIds; }
    public List<ItemStack> recipeOutputs() {
        var registryAccess = inventory.player.level().registryAccess();
        return recipes.stream().map(holder -> holder.value().assemble(new SingleRecipeInput(input.getItem(0)), registryAccess).copy()).toList();
    }
    public ItemStack clientRecipeOutput(int index) { return index >= 0 && index < clientRecipeOutputs.size() ? clientRecipeOutputs.get(index) : ItemStack.EMPTY; }
    public void acceptClientRecipes(int token, List<ResourceLocation> ids, List<ItemStack> outputs) {
        if (token >= clientGeneration) {
            clientGeneration = token;
            clientRecipeIds = List.copyOf(ids);
            clientRecipeOutputs = outputs.size() == ids.size() ? outputs.stream().map(ItemStack::copy).toList() : List.of();
        }
    }

    public void select(int token, ResourceLocation id) {
        if (token != generation || !stillValid(inventory.player) || !reconcileRecipes()) return;
        for (int i = 0; i < recipes.size(); i++) if (recipes.get(i).id().location().equals(id)) { selected.set(i); refreshResult(); return; }
    }

    private void takeResult(Player player) {
        if (player.level().isClientSide()) return;
        if (!stillValid(player) || !reconcileRecipes()) return;
        RecipeHolder<WoodcuttingRecipe> holder = selectedRecipe();
        if (holder == null || !holder.value().input().test(input.getItem(0))) return;
        input.removeItem(0, 1);
        refresh();
    }

    @Override public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (inventory.player.level().isClientSide()) {
            return;
        }
        refresh();
    }
    @Override public boolean stillValid(Player player) { return stillValid(access, player, Woodcutters.feature().block.get()); }

    private boolean canAccept(ItemStack stack) {
        int needed = stack.getCount();
        for (int i = 2; i < 38; i++) {
            Slot slot = slots.get(i);
            if (!slot.mayPlace(stack)) continue;
            ItemStack present = slot.getItem();
            if (present.isEmpty()) needed -= Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
            else if (ItemStack.isSameItemSameComponents(present, stack)) needed -= Math.min(present.getMaxStackSize(), slot.getMaxStackSize()) - present.getCount();
            if (needed <= 0) return true;
        }
        return false;
    }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack copy = slot.getItem().copy();
        if (index == 1) {
            if (!canAccept(copy) || !moveItemStackTo(slot.getItem(), 2, 38, true)) return ItemStack.EMPTY;
            slot.onTake(player, copy);
        } else if (index == 0) {
            if (!moveItemStackTo(slot.getItem(), 2, 38, false)) return ItemStack.EMPTY;
        } else if (!findRecipes(slot.getItem()).isEmpty()) {
            if (!moveItemStackTo(slot.getItem(), 0, 1, false)) return ItemStack.EMPTY;
        } else if (index < 29) {
            if (!moveItemStackTo(slot.getItem(), 29, 38, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(slot.getItem(), 2, 29, false)) return ItemStack.EMPTY;
        if (slot.getItem().isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }

    @Override public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) clearContainer(player, input);
    }
}
