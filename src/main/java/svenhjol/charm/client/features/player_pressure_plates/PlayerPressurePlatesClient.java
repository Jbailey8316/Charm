package svenhjol.charm.client.features.player_pressure_plates;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.client.ClientRegistry;
import svenhjol.charm.common.features.player_pressure_plates.PlayerPressurePlates;

@FeatureDefinition(side = Side.Client)
public final class PlayerPressurePlatesClient extends SidedFeature {
    public final PlayerPressurePlates common;

    public PlayerPressurePlatesClient(Mod mod) {
        super(mod);
        common = PlayerPressurePlates.feature();
        ClientRegistry.forFeature(this).itemTab(common.item.get(), CreativeModeTabs.REDSTONE_BLOCKS, Items.STONE_PRESSURE_PLATE);
    }
}
