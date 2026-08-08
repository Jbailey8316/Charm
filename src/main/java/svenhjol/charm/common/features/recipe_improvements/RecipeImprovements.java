package svenhjol.charm.common.features.recipe_improvements;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.common.CommonRegistry;
import svenhjol.charmony.core.common.features.conditional_recipes.ConditionalRecipe;

import java.util.Map;
import java.util.function.Function;

@FeatureDefinition(side = Side.Common, description = "Adds historically documented Charm recipe improvements.")
public final class RecipeImprovements extends SidedFeature {
    @Configurable(name = "Ore block from raw ore block", description = "Adds blast-furnace recipes for raw ore blocks.")
    private static boolean rawOreBlocks = true;
    @Configurable(name = "Gilded Blackstone", description = "Adds the gold-nugget Gilded Blackstone recipe.")
    private static boolean gildedBlackstone = true;
    @Configurable(name = "Cyan Dye from warped roots", description = "Adds Cyan Dye from warped roots.")
    private static boolean cyanDye = true;
    @Configurable(name = "Green Dye from yellow and blue", description = "Adds Green Dye from yellow and blue dyes.")
    private static boolean greenDye = true;
    @Configurable(name = "Snowballs from snow blocks", description = "Adds the reverse Snow Block recipe.")
    private static boolean snowballs = true;
    @Configurable(name = "Quartz from quartz blocks", description = "Adds the reverse Quartz Block recipe.")
    private static boolean quartz = true;
    @Configurable(name = "Clay balls from clay blocks", description = "Adds the reverse Clay Block recipe.")
    private static boolean clay = true;
    @Configurable(name = "Simpler Soul Torch", description = "Adds the historical two-item Soul Torch recipe.")
    private static boolean soulTorch = true;
    @Configurable(name = "Shapeless bread", description = "Adds a shapeless bread recipe.")
    private static boolean bread = true;
    @Configurable(name = "Shapeless paper", description = "Adds a shapeless paper recipe.")
    private static boolean paper = true;
    @Configurable(name = "Unlock all recipes on join", description = "Unlocks all recipes when a player joins; disabled by default.")
    private static boolean unlockRecipes = false;

    public RecipeImprovements(Mod mod) { super(mod); }

    @Override
    public void run() {
        var registry = CommonRegistry.forFeature(this);
        Function<String, net.minecraft.resources.ResourceLocation> id = path -> id("recipe_improvements/" + path);
        if (rawOreBlocks) {
            blasting(registry, id.apply("copper_block_from_blasting_raw_copper_block"), "minecraft:raw_copper_block", "minecraft:copper_block");
            blasting(registry, id.apply("gold_block_from_blasting_raw_gold_block"), "minecraft:raw_gold_block", "minecraft:gold_block");
            blasting(registry, id.apply("iron_block_from_blasting_raw_iron_block"), "minecraft:raw_iron_block", "minecraft:iron_block");
        }
        registry.conditionalRecipe(new ConditionalRecipe(id.apply("gilded_blackstone"), r -> gildedBlackstone)
            .withPattern("GGG", "GBG", "GGG").withKey(Map.of("G", "minecraft:gold_nugget", "B", "minecraft:blackstone"))
            .withResult("minecraft:gilded_blackstone"));
        shapeless(registry, id.apply("cyan_dye"), cyanDye, new String[]{"minecraft:warped_roots"}, "minecraft:cyan_dye", 1);
        shapeless(registry, id.apply("green_dye"), greenDye, new String[]{"minecraft:yellow_dye", "minecraft:blue_dye"}, "minecraft:green_dye", 1);
        shapeless(registry, id.apply("snowballs_from_snow_block"), snowballs, new String[]{"minecraft:snow_block"}, "minecraft:snowball", 4);
        shapeless(registry, id.apply("quartz_from_quartz_block"), quartz, new String[]{"minecraft:quartz_block"}, "minecraft:quartz", 4);
        shapeless(registry, id.apply("clay_balls_from_clay_block"), clay, new String[]{"minecraft:clay"}, "minecraft:clay_ball", 4);
        registry.conditionalRecipe(new ConditionalRecipe(id.apply("soul_torch"), r -> soulTorch)
            .withPattern("S", "X").withKey(Map.of("S", "#minecraft:soul_fire_base_blocks", "X", "minecraft:stick"))
            .withResult("minecraft:soul_torch").withCount(2));
        shapeless(registry, id("shapeless_recipes/bread"), bread, new String[]{"minecraft:wheat", "minecraft:wheat", "minecraft:wheat"}, "minecraft:bread", 1);
        shapeless(registry, id("shapeless_recipes/paper"), paper, new String[]{"minecraft:sugar_cane", "minecraft:sugar_cane", "minecraft:sugar_cane"}, "minecraft:paper", 3);
        if (unlockRecipes) {
            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                Player player = handler.getPlayer();
                player.awardRecipes(server.getRecipeManager().getRecipes());
            });
        }
    }

    private static void blasting(CommonRegistry registry, net.minecraft.resources.ResourceLocation id, String ingredient, String result) {
        registry.conditionalRecipe(new ConditionalRecipe(id, r -> true).useBlasting().withIngredient(ingredient)
            .withResult(result).withExperience(6.3f).withCookingTime(600));
    }

    private static void shapeless(CommonRegistry registry, net.minecraft.resources.ResourceLocation id, boolean enabled, String[] ingredients, String result, int count) {
        registry.conditionalRecipe(new ConditionalRecipe(id, r -> enabled).useShapelessCrafting()
            .withIngredients(ingredients).withResult(result).withCount(count));
    }
}
