package svenhjol.charm.common.features.silence;

import svenhjol.charm.common.features.silence.ConfigHelper;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

/** Disables selected development/privacy nagging and telemetry messages. */
@FeatureDefinition(side = Side.Common, enabledByDefault = false, description = "Disables selected nag messages, telemetry, and development connections.")
public final class Silence extends SidedFeature {
    @Configurable(name = "Disable chat message verification dialog", description = "Disables the chat verification warning when secure profiles are not enforced.")
    private static boolean disableChatMessageVerification = true;
    @Configurable(name = "Disable experimental screen dialog", description = "Disables the experimental-world warning dialog.")
    private static boolean disableExperimental = true;
    @Configurable(name = "Disable telemetry", description = "Prevents client telemetry from being sent.")
    private static boolean disableTelemetry = true;
    @Configurable(name = "Downgrade data fixer registered error", description = "Logs missing data-fixer registrations as informational messages.")
    private static boolean downgradeDataFixerError = true;
    @Configurable(name = "Disable development mode connections", description = "Disables Realms and other API connections in development environments.")
    private static boolean disableDevEnvironmentConnections = true;

    public Silence(Mod mod) {
        super(mod);
    }

    public static boolean disableChatMessageVerification() { return disableChatMessageVerification; }
    public static boolean disableExperimental() { return disableExperimental; }
    public static boolean disableTelemetry() { return disableTelemetry; }
    public static boolean downgradeDataFixerError() { return downgradeDataFixerError; }
    public static boolean disableDevEnvironmentConnections() {
        return disableDevEnvironmentConnections && ConfigHelper.isDevEnvironment();
    }

    public static Silence feature() { return Mod.getSidedFeature(Silence.class); }
}
