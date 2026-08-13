package svenhjol.charm.client.features.kilns;

import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import svenhjol.charm.common.features.kilns.Kilns;
import svenhjol.charm.common.features.firing.Firing;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.client.ClientRegistry;

import java.util.List;
import java.util.Optional;

@FeatureDefinition(side = Side.Client, description = "Client support for the Charm Kiln.")
public final class KilnsClient extends SidedFeature {
    public KilnsClient(Mod mod) {
        super(mod);
        ClientRegistry.forFeature(this).menuScreen((MenuType) Mod.getSidedFeature(Kilns.class).menu.get(),
            (MenuScreens.ScreenConstructor) (m, i, t) -> new KilnScreen((Kilns.KilnMenu) m, i, t));
        ClientRegistry.forFeature(this).itemTab(Mod.getSidedFeature(Kilns.class).item.get(), CreativeModeTabs.FUNCTIONAL_BLOCKS, Items.SMOKER);
    }

    public static final class KilnScreen extends AbstractFurnaceScreen<Kilns.KilnMenu> implements MenuAccess<Kilns.KilnMenu> {
        private static final ResourceLocation TEXTURE = ResourceLocation.parse("textures/gui/container/smoker.png");
        private static final ResourceLocation LIT_PROGRESS = ResourceLocation.parse("container/smoker/lit_progress");
        private static final ResourceLocation BURN_PROGRESS = ResourceLocation.parse("container/smoker/burn_progress");

        public KilnScreen(Kilns.KilnMenu menu, Inventory inventory, Component title) {
            super(menu, inventory, title, title, TEXTURE, LIT_PROGRESS, BURN_PROGRESS,
                List.of(new RecipeBookComponent.TabInfo(Items.FURNACE.getDefaultInstance(), Optional.<net.minecraft.world.item.ItemStack>empty(), Mod.getSidedFeature(Firing.class).recipeBookCategory.get())));
        }

        @Override public Kilns.KilnMenu getMenu() { return menu; }
    }
}
