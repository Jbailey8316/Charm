package svenhjol.charm.client.features.coral_sea_lanterns;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.client.ClientRegistry;
import svenhjol.charm.common.features.coral_sea_lanterns.CoralSeaLanterns;

@FeatureDefinition(side = Side.Client)
public final class CoralSeaLanternsClient extends SidedFeature {
    public CoralSeaLanternsClient(Mod mod) {
        super(mod);
        var common = CoralSeaLanterns.feature();
        var registry = ClientRegistry.forFeature(this);
        registry.itemTab(common.tubeItem.get(), CreativeModeTabs.BUILDING_BLOCKS, Items.SEA_LANTERN);
        registry.itemTab(common.brainItem.get(), CreativeModeTabs.BUILDING_BLOCKS, common.tubeItem.get());
        registry.itemTab(common.bubbleItem.get(), CreativeModeTabs.BUILDING_BLOCKS, common.brainItem.get());
        registry.itemTab(common.fireItem.get(), CreativeModeTabs.BUILDING_BLOCKS, common.bubbleItem.get());
        registry.itemTab(common.hornItem.get(), CreativeModeTabs.BUILDING_BLOCKS, common.fireItem.get());
    }
}
