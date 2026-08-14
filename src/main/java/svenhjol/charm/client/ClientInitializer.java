package svenhjol.charm.client;

import net.fabricmc.api.ClientModInitializer;
import svenhjol.charmony.api.core.Side;
import svenhjol.charm.CharmMod;
import svenhjol.charm.client.features.coral_squids.CoralSquids;
import svenhjol.charm.client.features.kilns.KilnsClient;
import svenhjol.charm.client.features.copper_pistons.CopperPistons;
import svenhjol.charm.client.features.woodcutting.WoodcuttersClient;
import svenhjol.charm.client.features.endermite_powder.EndermitePowderClient;
import svenhjol.charm.client.features.player_pressure_plates.PlayerPressurePlatesClient;

public final class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        svenhjol.charmony.core.client.ClientInitializer.init();
        var mod = CharmMod.instance();
        mod.addSidedFeature(CoralSquids.class);
        mod.addSidedFeature(KilnsClient.class);
        mod.addSidedFeature(CopperPistons.class);
        mod.addSidedFeature(WoodcuttersClient.class);
        mod.addSidedFeature(EndermitePowderClient.class);
        mod.addSidedFeature(PlayerPressurePlatesClient.class);
        mod.run(Side.Client);
    }
}
