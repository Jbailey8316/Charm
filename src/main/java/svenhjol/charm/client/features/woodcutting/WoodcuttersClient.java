package svenhjol.charm.client.features.woodcutting;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import svenhjol.charm.common.features.woodcutting.WoodcutterMenu;
import svenhjol.charm.common.features.woodcutting.WoodcutterNetworking;
import svenhjol.charm.common.features.woodcutting.Woodcutters;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.client.ClientRegistry;

@FeatureDefinition(side = Side.Client, description = "Client support for the Charm Woodcutter.")
public final class WoodcuttersClient extends SidedFeature {
    public WoodcuttersClient(Mod mod) {
        super(mod);
        ClientRegistry.forFeature(this).menuScreen((MenuType)Woodcutters.feature().menu.get(), (MenuScreens.ScreenConstructor)(menu, inventory, title) -> new Screen((WoodcutterMenu)menu, inventory, title));
        ClientRegistry.forFeature(this).itemTab(Woodcutters.feature().item.get(), CreativeModeTabs.FUNCTIONAL_BLOCKS, Items.STONECUTTER);
        ClientPlayNetworking.registerGlobalReceiver(WoodcutterNetworking.S2CRecipes.TYPE, (packet, context) -> context.client().execute(() -> {
            if (context.player().containerMenu instanceof WoodcutterMenu menu && menu.containerId == packet.menuId()) menu.acceptClientRecipes(packet.generation(), packet.recipes());
        }));
    }

    public static final class Screen extends AbstractContainerScreen<WoodcutterMenu> {
        private static final ResourceLocation TEXTURE = ResourceLocation.parse("textures/gui/container/stonecutter.png");
        private final Inventory inventory;
        private int displayedGeneration = -1;

        public Screen(WoodcutterMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); this.inventory = inventory; imageWidth = 176; imageHeight = 166; }

        @Override protected void init() { super.init(); rebuildRecipeButtons(); }
        @Override protected void containerTick() { super.containerTick(); if (displayedGeneration != menu.clientGeneration()) rebuildRecipeButtons(); }

        private void rebuildRecipeButtons() {
            clearWidgets();
            displayedGeneration = menu.clientGeneration();
            int y = topPos + 15;
            for (ResourceLocation id : menu.clientRecipeIds()) {
                String label = id.getPath().replace('_', ' ');
                addRenderableWidget(Button.builder(Component.literal(label), button -> ClientPlayNetworking.send(new WoodcutterNetworking.C2SSelect(menu.containerId, menu.clientGeneration(), id))).bounds(leftPos + 45, y, 90, 16).build());
                y += 17;
                if (y > topPos + 80) break;
            }
        }

        @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) { graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256); }
        @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { graphics.drawString(font, title, 8, 6, 4210752, false); graphics.drawString(font, inventory.getDisplayName(), 8, imageHeight - 94, 4210752, false); }
        @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { renderBackground(graphics, mouseX, mouseY, partialTick); super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
    }
}
