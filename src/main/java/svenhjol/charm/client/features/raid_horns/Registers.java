package svenhjol.charm.client.features.raid_horns;

import net.minecraft.world.item.CreativeModeTabs;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.client.ClientRegistry;

public final class Registers extends Setup<RaidHornsClient> {
    public Registers(RaidHornsClient feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> ClientRegistry.forFeature(feature()).itemTab(feature().common.item.get(), CreativeModeTabs.TOOLS_AND_UTILITIES, null);
    }
}
