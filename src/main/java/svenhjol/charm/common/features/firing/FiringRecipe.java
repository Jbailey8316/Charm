package svenhjol.charm.common.features.firing;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import svenhjol.charmony.core.base.Mod;

public class FiringRecipe extends AbstractCookingRecipe {
    private static final Firing FIRING = Mod.getSidedFeature(Firing.class);
    public Item icon = Items.FURNACE;

    public FiringRecipe(String group, CookingBookCategory category, Ingredient input, ItemStack output, float experience, int cookTime) {
        super(group, category, input, output, experience, cookTime);
    }

    @Override public Item furnaceIcon() { return icon; }

    @Override public RecipeSerializer<? extends AbstractCookingRecipe> getSerializer() { return FIRING.recipeSerializer.get(); }
    @Override public net.minecraft.world.item.crafting.RecipeType<FiringRecipe> getType() { return FIRING.recipeType.get(); }
    @Override public RecipeBookCategory recipeBookCategory() { return FIRING.recipeBookCategory.get(); }
}
