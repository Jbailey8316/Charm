package svenhjol.charm.client;

import net.fabricmc.api.ClientModInitializer;

public final class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        svenhjol.charmony.core.client.ClientInitializer.init();
    }
}
