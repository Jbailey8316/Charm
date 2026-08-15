package svenhjol.charm.common.features.silence;

import net.fabricmc.loader.api.FabricLoader;

final class ConfigHelper {
    private ConfigHelper() {}

    static boolean isDevEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
