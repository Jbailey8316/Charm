package svenhjol.charm.common.features.woodcutting;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, priority = 5, description = "Registers the Woodcutting recipe type.")
public final class Woodcutting extends SidedFeature {
    public final Registerable<RecipeType<WoodcuttingRecipe>> recipeType;
    public final Registerable<RecipeSerializer<WoodcuttingRecipe>> recipeSerializer;
    public Woodcutting(Mod mod) {
        super(mod);
        recipeType = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.RECIPE_TYPE, id("woodcutting"), new RecipeType<>() { public String toString() { return "charm:woodcutting"; }}));
        recipeSerializer = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id("woodcutting"), new WoodcuttingRecipe.Serializer()));
    }
    @Override public boolean canBeDisabled() { return false; }
    public static Woodcutting feature() { return Mod.getSidedFeature(Woodcutting.class); }
}
