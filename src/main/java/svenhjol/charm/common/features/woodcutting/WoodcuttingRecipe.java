package svenhjol.charm.common.features.woodcutting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import svenhjol.charmony.core.base.Mod;

public final class WoodcuttingRecipe extends SingleItemRecipe {
    public Item icon = Items.AIR;
    public WoodcuttingRecipe(String group, Ingredient input, ItemStack output) {
        super(group, input, output);
    }
    public ItemStack getRecipeKindIcon() { return new ItemStack(icon); }
    @Override public RecipeType<WoodcuttingRecipe> getType() { return Woodcutting.feature().recipeType.get(); }
    @Override public RecipeSerializer<? extends SingleItemRecipe> getSerializer() { return Woodcutting.feature().recipeSerializer.get(); }
    @Override public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.STONECUTTER; }
    public static final class Serializer extends SingleItemRecipe.Serializer<WoodcuttingRecipe> {
        public Serializer() { super(WoodcuttingRecipe::new); }
    }
}
