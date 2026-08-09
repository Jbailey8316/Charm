package svenhjol.charm.client.features.woodcutting;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import java.util.LinkedHashMap;
import java.util.Map;
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
        ClientRegistry.forFeature(this).blockRenderType(Woodcutters.feature().block.get(), ChunkSectionLayer.CUTOUT);
        ClientRegistry.forFeature(this).itemTab(Woodcutters.feature().item.get(), CreativeModeTabs.FUNCTIONAL_BLOCKS, Items.STONECUTTER);
        ClientPlayNetworking.registerGlobalReceiver(WoodcutterNetworking.S2CRecipes.TYPE, (packet, context) -> context.client().execute(() -> {
            if (context.player().containerMenu instanceof WoodcutterMenu menu && menu.containerId == packet.menuId()) menu.acceptClientRecipes(packet.generation(), packet.recipes(), packet.outputs());
        }));
    }

    public static final class Screen extends AbstractContainerScreen<WoodcutterMenu> {
        private static final ResourceLocation TEXTURE = ResourceLocation.parse("textures/gui/container/stonecutter.png");
        private final Inventory inventory;
        private final Map<Button, net.minecraft.world.item.ItemStack> recipeButtons = new LinkedHashMap<>();
        private ResourceLocation selectedRecipe;
        private int displayedGeneration = -1;

        public Screen(WoodcutterMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); this.inventory = inventory; imageWidth = 176; imageHeight = 166; }

        @Override protected void init() { super.init(); rebuildRecipeButtons(); }
        @Override protected void containerTick() { super.containerTick(); if (displayedGeneration != menu.clientGeneration()) rebuildRecipeButtons(); }

        private void rebuildRecipeButtons() {
            clearWidgets();
            recipeButtons.clear();
            displayedGeneration = menu.clientGeneration();
            int x = leftPos + 52;
            int y = topPos + 14;
            for (int index = 0; index < menu.clientRecipeIds().size(); index++) {
                ResourceLocation id = menu.clientRecipeIds().get(index);
                int column = index % 4;
                int row = (index / 4) % 3;
                if (index >= 12) break;
                var button = Button.builder(Component.empty(), ignored -> {
                    selectedRecipe = id;
                    ClientPlayNetworking.send(new WoodcutterNetworking.C2SSelect(menu.containerId, menu.clientGeneration(), id));
                }).bounds(x + column * 16, y + row * 18, 16, 18).build();
                addRenderableWidget(button);
                recipeButtons.put(button, menu.clientRecipeOutput(index));
            }
        }

        @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) { graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256); }
        @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { graphics.drawString(font, title, 8, 6, 4210752, false); graphics.drawString(font, inventory.getDisplayName(), 8, imageHeight - 94, 4210752, false); }
        @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            renderBackground(graphics, mouseX, mouseY, partialTick);
            super.render(graphics, mouseX, mouseY, partialTick);
            Button hovered = null;
            for (var entry : recipeButtons.entrySet()) {
                var button = entry.getKey();
                var stack = entry.getValue();
                if (selectedRecipe != null && menu.clientRecipeIds().contains(selectedRecipe)
                    && menu.clientRecipeIds().indexOf(selectedRecipe) == recipeButtons.keySet().stream().toList().indexOf(button)) {
                    graphics.fill(button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + 1, 0xff00aaff);
                    graphics.fill(button.getX(), button.getY() + button.getHeight() - 1, button.getX() + button.getWidth(), button.getY() + button.getHeight(), 0xff00aaff);
                }
                if (!stack.isEmpty()) graphics.renderItem(stack, button.getX() + (button.getWidth() - 16) / 2, button.getY());
                if (button.isHovered()) hovered = button;
            }
            if (hovered != null) {
                var stack = recipeButtons.get(hovered);
                if (stack != null && !stack.isEmpty()) {
                    var tooltipLines = stack.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL)
                        .stream().map(line -> ClientTooltipComponent.create(line.getVisualOrderText())).toList();
                    graphics.renderTooltip(font, tooltipLines, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
                }
            }
            renderTooltip(graphics, mouseX, mouseY);
        }
    }
}
