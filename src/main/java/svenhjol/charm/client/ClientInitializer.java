package svenhjol.charm.client;

import net.fabricmc.api.ClientModInitializer;
import svenhjol.charmony.api.core.Side;
import svenhjol.charm.CharmMod;
import svenhjol.charm.client.features.coral_squids.CoralSquids;

public final class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        svenhjol.charmony.core.client.ClientInitializer.init();
        var mod = CharmMod.instance();
        mod.addSidedFeature(CoralSquids.class);
        mod.run(Side.Client);
    }
}
