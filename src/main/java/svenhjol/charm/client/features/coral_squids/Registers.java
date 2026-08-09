package svenhjol.charm.client.features.coral_squids;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import svenhjol.charm.common.features.coral_squids.common.CoralSquid;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.client.ClientRegistry;

public final class Registers extends Setup<CoralSquids> {
    public final ModelLayerLocation layer;
    public Registers(CoralSquids feature) {
        super(feature);
        var registry = ClientRegistry.forFeature(feature);
        layer = new ModelLayerLocation(feature.id("coral_squid"), "main");
        registry.modelLayer(layer, Model::createBodyLayer);
        registry.entityRenderer(feature.common.registers.entity.get(), EntityRenderer::new);
    }
    @Override public Runnable boot() { return () -> {
        var registry = ClientRegistry.forFeature(feature());
        registry.itemTab(feature().common.registers.bucketItem.get(), CreativeModeTabs.TOOLS_AND_UTILITIES, Items.AXOLOTL_BUCKET);
        registry.itemTab(feature().common.registers.spawnEggItem.get(), CreativeModeTabs.SPAWN_EGGS, Items.AXOLOTL_SPAWN_EGG);
    }; }
}
