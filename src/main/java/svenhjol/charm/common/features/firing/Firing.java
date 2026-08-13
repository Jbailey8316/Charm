package svenhjol.charm.common.features.firing;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, priority = 5, description = "Registers the Charm firing recipe type.")
public final class Firing extends SidedFeature {
    public final Registerable<RecipeType<FiringRecipe>> recipeType;
    public final Registerable<RecipeSerializer<FiringRecipe>> recipeSerializer;
    public final Registerable<RecipeBookCategory> recipeBookCategory;

    public Firing(Mod mod) {
        super(mod);
        recipeType = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.RECIPE_TYPE,
            id("firing"), new RecipeType<>() { @Override public String toString() { return "charm:firing"; } }));
        recipeSerializer = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
            id("firing"), new AbstractCookingRecipe.Serializer<>(FiringRecipe::new, 100)));
        recipeBookCategory = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY,
            id("kiln"), new RecipeBookCategory()));
    }

    @Override public boolean canBeDisabled() { return false; }
}
