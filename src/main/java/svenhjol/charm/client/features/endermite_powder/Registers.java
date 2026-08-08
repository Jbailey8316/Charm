package svenhjol.charm.client.features.endermite_powder;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import svenhjol.charm.common.features.endermite_powder.EndermitePowder;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.client.ClientRegistry;

public final class Registers extends Setup<EndermitePowderClient> {
    public Registers(EndermitePowderClient feature) {
        super(feature);
        var registry = ClientRegistry.forFeature(feature);
        registry.entityRenderer(feature.common.entity.get(), EndermitePowderRenderer::new);
    }

    @Override
    public Runnable boot() {
        return () -> ClientRegistry.forFeature(feature()).itemTab(feature().common.item.get(), CreativeModeTabs.INGREDIENTS, Items.ENDER_PEARL);
    }
}
