package svenhjol.charm.client.features.atlases;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import svenhjol.charm.common.features.atlases.Atlases;
import svenhjol.charm.common.features.atlases.AtlasData;
import svenhjol.charm.common.features.atlases.AtlasMapEntry;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.client.ClientRegistry;

@FeatureDefinition(side = Side.Client)
public final class AtlasesClient extends SidedFeature {
    public AtlasesClient(Mod mod) {
        super(mod);
        ClientRegistry.forFeature(this).menuScreen((MenuType)Atlases.feature().menu.get(),
            (MenuScreens.ScreenConstructor)(menu, inventory, title) -> new Screen((svenhjol.charm.common.features.atlases.AtlasMenu)menu, inventory, title));
        new Setup<AtlasesClient>(this) {
            @Override public Runnable boot() {
                return () -> ClientRegistry.forFeature(AtlasesClient.this)
                    .itemTab(Atlases.feature().item.get(), CreativeModeTabs.TOOLS_AND_UTILITIES, null);
            }
        };
    }

    public static final class Screen extends AbstractContainerScreen<svenhjol.charm.common.features.atlases.AtlasMenu> {
        private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("charm", "textures/gui/atlas.png");
        private static final ResourceLocation SPRITES = ResourceLocation.fromNamespaceAndPath("charm", "textures/gui/sprites/widget/atlases/");
        private AtlasMapEntry singleMap;
        private int mapDistance;
        private int cornerX;
        private int cornerZ;
        private boolean hasCorner;
        private ResourceLocation lastDimension;
        private int minGridX, minGridZ, maxGridX, maxGridZ, maxMapDistance;
        public Screen(svenhjol.charm.common.features.atlases.AtlasMenu menu, Inventory inventory, Component title) {
            super(menu, inventory, title);
            // Historical AtlasScreen dimensions (the background is a 256x256
            // atlas texture with a 175x168 container region).
            imageWidth = 175;
            imageHeight = 168;
        }

        @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos,
                0, 0, imageWidth, imageHeight, 256, 256);
            renderActiveMap(graphics);
            renderControls(graphics, mouseX, mouseY);
        }

        /**
         * The historical screen used map clicks rather than a row of visible
         * filled-map slots.  Keep the server-authoritative menu slots as the
         * transport, but do not render those implementation slots over the
         * map viewport.
         */
        @Override protected void renderSlot(GuiGraphics graphics, Slot slot) {
            if (slot.index >= 3 && slot.index < 12) return;
            super.renderSlot(graphics, slot);
        }

        @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            // Historical AtlasScreen attaches Back at MapGui (82,-12),
            // which becomes GUI-local (156,4) after the (74,16) origin.
            // Handle that control before the map-area hit test.
            if (event.button() == 0 && singleMap != null
                && event.x() >= leftPos + 156 && event.x() < leftPos + 172
                && event.y() >= topPos + 4 && event.y() < topPos + 20) {
                singleMap = null;
                return true;
            }
            if (event.button() == 0 && (clickControl(event.x(), event.y()) || clickMap(event.x(), event.y()))) return true;
            return super.mouseClicked(event, doubleClick);
        }

        private boolean clickMap(double mouseX, double mouseY) {
            if (mouseX < leftPos + 74 || mouseX >= leftPos + 164
                || mouseY < topPos + 16 || mouseY >= topPos + 106) return false;
            if (clickControl(mouseX, mouseY)) return true;
            var level = Minecraft.getInstance().level;
            if (level == null || Minecraft.getInstance().gameMode == null) return false;
            var atlas = menu.playerInventory().getSelectedItem();
            if (!atlas.is(Atlases.feature().item.get())) atlas = menu.playerInventory().getItem(Inventory.SLOT_OFFHAND);
            var data = atlas.get(Atlases.feature().data.get());
            if (data == null) return false;
            var current = data.maps().stream().filter(e -> e.dimension().equals(level.dimension().location())).toList();
            int distance = 1;
            if (current.size() > 1) {
                int diameter = 128 * (1 << data.scale());
                int minX = current.stream().mapToInt(e -> Math.floorDiv(e.centerX(), diameter)).min().orElse(0);
                int minZ = current.stream().mapToInt(e -> Math.floorDiv(e.centerZ(), diameter)).min().orElse(0);
                int maxX = current.stream().mapToInt(e -> Math.floorDiv(e.centerX(), diameter)).max().orElse(minX);
                int maxZ = current.stream().mapToInt(e -> Math.floorDiv(e.centerZ(), diameter)).max().orElse(minZ);
                distance = Math.max(1, Math.min(8, Math.max(maxX - minX + 1, maxZ - minZ + 1)));
            }
            float scale = 0.375f / distance;
            int diameter = 128 * (1 << data.scale());
            int minX = current.stream().mapToInt(e -> Math.floorDiv(e.centerX(), diameter)).min().orElse(0);
            int minZ = current.stream().mapToInt(e -> Math.floorDiv(e.centerZ(), diameter)).min().orElse(0);
            if (singleMap != null) {
                var selected = current.stream().filter(e -> e.mapId().equals(singleMap.mapId())).findFirst();
                if (selected.isEmpty()) { singleMap = null; hasCorner = false; }
                else {
                    if (carriedMap(menu.getCarried())) return transferToFirstEmpty();
                    if (Minecraft.getInstance().options.keyShift.isDown()) return clickSlotFor(selected.get(), current, true);
                    return clickSlotFor(selected.get(), current, false);
                }
            }
            if (mapDistance < 1) mapDistance = Math.max(1, Math.min(8, Math.max(
                current.stream().mapToInt(e -> Math.floorDiv(e.centerX(), diameter)).max().orElse(minX) - minX + 1,
                current.stream().mapToInt(e -> Math.floorDiv(e.centerZ(), diameter)).max().orElse(minZ) - minZ + 1)));
            if (!hasCorner) { cornerX = minX; cornerZ = minZ; hasCorner = true; }
            double mapX = (mouseX - leftPos - 74) / scale;
            double mapZ = (mouseY - topPos - 16) / scale;
            int hit = -1;
            for (int i = 0; i < current.size(); i++) {
                var entry = current.get(i);
                double x = (Math.floorDiv(entry.centerX(), diameter) - minX) * 128.0;
                double z = (Math.floorDiv(entry.centerZ(), diameter) - minZ) * 128.0;
                if (mapX >= x && mapX < x + 128 && mapZ >= z && mapZ < z + 128) { hit = i; break; }
            }
            ItemStack carried = menu.getCarried();
            if (hit >= 0 && carried.isEmpty() && current.size() > 1) {
                singleMap = current.get(hit);
                return true;
            }
            int slot = hit >= 0 ? 3 + hit : -1;
            if (slot < 0 && carried.is(Items.FILLED_MAP)) {
                for (int i = 0; i < 9; i++) if (!menu.getSlot(3 + i).hasItem()) { slot = 3 + i; break; }
            }
            if (slot < 0) return false;
            Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, slot, 0,
                ClickType.PICKUP, Minecraft.getInstance().player);
            return true;
        }

        private boolean carriedMap(ItemStack stack) { return stack.is(Items.FILLED_MAP); }

        private boolean transferToFirstEmpty() {
            for (int i = 0; i < 9; i++) if (!menu.getSlot(3 + i).hasItem()) {
                Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, 3 + i, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                return true;
            }
            return false;
        }

        private boolean clickSlotFor(AtlasMapEntry entry, java.util.List<AtlasMapEntry> current, boolean shift) {
            int index = current.indexOf(entry);
            if (index < 0) return false;
            Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, 3 + index, 0,
                shift ? ClickType.QUICK_MOVE : ClickType.PICKUP, Minecraft.getInstance().player);
            if (!shift) singleMap = null;
            return true;
        }

        private boolean clickControl(double mouseX, double mouseY) {
            int x = (int) mouseX - leftPos - 74;
            int y = (int) mouseY - topPos - 16;
            if (singleMap != null && x >= 82 && x < 98 && y >= 0 && y < 16) {
                singleMap = null;
                return true;
            }
            if (singleMap != null) return false;
            if (x >= -12 && x < -3 && y >= 19 && y < 28) { if (buttonEnabled(0)) cornerX -= mapDistance; return true; }
            if (x >= 51 && x < 60 && y >= 19 && y < 28) { if (buttonEnabled(2)) cornerX += mapDistance; return true; }
            if (x >= 19 && x < 28 && y >= -12 && y < -3) { if (buttonEnabled(1)) cornerZ -= mapDistance; return true; }
            if (x >= 19 && x < 28 && y >= 51 && y < 60) { if (buttonEnabled(3)) cornerZ += mapDistance; return true; }
            if (x >= 79 && x < 87 && y >= 46 && y < 55) { if (buttonEnabled(5)) mapDistance--; return true; }
            if (x >= 87 && x < 95 && y >= 46 && y < 55) { if (buttonEnabled(6)) mapDistance++; return true; }
            return false;
        }

        private void renderControls(GuiGraphics graphics, int mouseX, int mouseY) {
            // Historical controls are supplied as standalone sprites.  The
            // viewport remains authoritative; controls only change client
            // view state and never mutate AtlasData.
            if (singleMap != null) {
                blitSprite(graphics, "back_button", 82, -12, 16, 16, mouseX, mouseY, true);
                return;
            }
            blitSprite(graphics, "left_button", -12, 19, 9, 9, mouseX, mouseY, buttonEnabled(0));
            blitSprite(graphics, "up_button", 19, -12, 9, 9, mouseX, mouseY, buttonEnabled(1));
            blitSprite(graphics, "right_button", 51, 19, 9, 9, mouseX, mouseY, buttonEnabled(2));
            blitSprite(graphics, "down_button", 19, 51, 9, 9, mouseX, mouseY, buttonEnabled(3));
            blitSprite(graphics, "zoom_out_button", 79, 46, 8, 9, mouseX, mouseY, buttonEnabled(5));
            blitSprite(graphics, "zoom_in_button", 87, 46, 8, 9, mouseX, mouseY, buttonEnabled(6));
        }

        private boolean buttonEnabled(int button) {
            if (button == 5) return mapDistance > 1;
            if (button == 6) return mapDistance < maxMapDistance;
            if (!hasCorner) return false;
            return switch (button) {
                case 0 -> cornerX > minGridX;
                case 1 -> cornerZ > minGridZ;
                case 2 -> cornerX + mapDistance <= maxGridX;
                case 3 -> cornerZ + mapDistance <= maxGridZ;
                default -> false;
            };
        }

        private void blitSprite(GuiGraphics graphics, String name, int x, int y, int width, int height, int mouseX, int mouseY, boolean enabled) {
            if (!enabled) name += "_disabled";
            String suffix = mouseX >= leftPos + 74 + x && mouseX < leftPos + 74 + x + width
                && mouseY >= topPos + 16 + y && mouseY < topPos + 16 + y + height && enabled ? "_highlighted" : "";
            var id = ResourceLocation.fromNamespaceAndPath("charm", "textures/gui/sprites/widget/atlases/" + name + suffix + ".png");
            graphics.blit(RenderPipelines.GUI_TEXTURED, id, leftPos + 74 + x, topPos + 16 + y, 0, 0, width, height, width, height);
        }

        private void renderActiveMap(GuiGraphics graphics) {
            if (!(Minecraft.getInstance().level instanceof ClientLevel level)) return;
            var atlas = menu.playerInventory().getSelectedItem();
            if (!atlas.is(Atlases.feature().item.get())) {
                atlas = menu.playerInventory().getItem(Inventory.SLOT_OFFHAND);
            }
            if (!atlas.is(Atlases.feature().item.get())) return;
            MapRenderer renderer = Minecraft.getInstance().getMapRenderer();
            AtlasData data = atlas.get(Atlases.feature().data.get());
            if (data == null) return;
            var dimension = level.dimension().location();
            if (!dimension.equals(lastDimension)) {
                lastDimension = dimension;
                singleMap = null;
                mapDistance = 0;
                hasCorner = false;
            }
            var current = data.maps().stream().filter(e -> e.dimension().equals(dimension)).toList();
            if (current.isEmpty()) { singleMap = null; hasCorner = false; return; }
            if (current.size() == 1) {
                singleMap = null;
                hasCorner = false;
            } else if (singleMap != null && current.stream().noneMatch(e -> e.mapId().equals(singleMap.mapId()))) {
                singleMap = null;
                hasCorner = false;
                mapDistance = 0;
            }
            // Charm's historical map background is 240x240 logical pixels,
            // rendered through the 0.375 outer transform (90x90 on screen).
            // Clip to that complete viewport so map pixels/decorations cannot
            // cover the supply slots or player inventory.
            graphics.enableScissor(leftPos + 74, topPos + 16, leftPos + 164, topPos + 106);
            if (current.size() == 1 || singleMap != null) {
                var selected = singleMap == null ? current.get(0) : current.stream()
                    .filter(e -> e.mapId().equals(singleMap.mapId())).findFirst().orElse(current.get(0));
                renderMap(graphics, renderer, level, selected, 0, 0, 0.375f);
                graphics.disableScissor();
                return;
            }
            int diameter = 128 * (1 << data.scale());
            int minX = current.stream().mapToInt(e -> Math.floorDiv(e.centerX(), diameter)).min().orElse(0);
            int minZ = current.stream().mapToInt(e -> Math.floorDiv(e.centerZ(), diameter)).min().orElse(0);
            int maxX = current.stream().mapToInt(e -> Math.floorDiv(e.centerX(), diameter)).max().orElse(minX);
            int maxZ = current.stream().mapToInt(e -> Math.floorDiv(e.centerZ(), diameter)).max().orElse(minZ);
            maxMapDistance = Math.max(1, Math.min(8, Math.max(maxX - minX + 1, maxZ - minZ + 1)));
            minGridX = minX; minGridZ = minZ; maxGridX = maxX; maxGridZ = maxZ;
            if (mapDistance < 1) mapDistance = maxMapDistance;
            mapDistance = Math.min(mapDistance, maxMapDistance);
            if (!hasCorner) {
                cornerX = minX;
                cornerZ = minZ;
                hasCorner = true;
            }
            cornerX = Math.max(minX, Math.min(cornerX, maxX - mapDistance + 1));
            cornerZ = Math.max(minZ, Math.min(cornerZ, maxZ - mapDistance + 1));
            int distance = mapDistance;
            float scale = 0.375f / distance;
            for (AtlasMapEntry entry : current) {
                int gridX = Math.floorDiv(entry.centerX(), diameter);
                int gridZ = Math.floorDiv(entry.centerZ(), diameter);
                if (gridX < cornerX || gridX >= cornerX + distance || gridZ < cornerZ || gridZ >= cornerZ + distance) continue;
                int x = gridX - cornerX;
                int z = gridZ - cornerZ;
                renderMap(graphics, renderer, level, entry, x * 128, z * 128, scale);
            }
            renderWorldMapGrid(graphics, distance);
            graphics.disableScissor();
        }

        /** Historical WorldMap.drawLines: cell dividers are drawn over the
         * map background after the constituent maps are rendered. */
        private void renderWorldMapGrid(GuiGraphics graphics, int distance) {
            if (distance <= 1) return;
            int originX = leftPos + 74;
            int originZ = topPos + 16;
            int extent = Math.round(48.0f);
            for (int i = 1; i < distance; i++) {
                int x = originX + Math.round(extent * i / (float) distance);
                int z = originZ + Math.round(extent * i / (float) distance);
                graphics.fill(x, originZ, x + 1, originZ + extent, 0xff000000);
                graphics.fill(originX, z, originX + extent, z + 1, 0xff000000);
            }
        }

        private void renderMap(GuiGraphics graphics, MapRenderer renderer, ClientLevel level,
                               AtlasMapEntry entry, int offsetX, int offsetZ, float scale) {
            var saved = level.getMapData(entry.mapId());
            if (saved == null) return;
            var mapState = new MapRenderState();
            // MapRenderState is populated by MapRenderer.  In particular,
            // extractRenderState prepares the map texture and copies the
            // current decorations.  A client map may not have received its
            // saved data/texture yet (for example immediately after a
            // dimension change), so defer that map for this frame instead
            // of submitting an incomplete state to GuiGraphics.
            renderer.extractRenderState(entry.mapId(), saved, mapState);
            if (mapState.texture == null) return;
            graphics.pose().pushMatrix();
            graphics.pose().translate(leftPos + 74.0f + offsetX * scale, topPos + 16.0f + offsetZ * scale);
            graphics.pose().scale(scale, scale);
            graphics.submitMapRenderState(mapState);
            graphics.pose().popMatrix();
        }
    }
}
