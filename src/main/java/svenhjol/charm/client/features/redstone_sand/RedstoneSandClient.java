package svenhjol.charm.client.features.redstone_sand;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.client.ClientRegistry;
import svenhjol.charm.common.features.redstone_sand.RedstoneSand;

@FeatureDefinition(side = Side.Client)
public final class RedstoneSandClient extends SidedFeature {
    public RedstoneSandClient(Mod mod) {
        super(mod);
        var common = RedstoneSand.feature();
        ClientRegistry.forFeature(this).itemTab(common.item.get(), CreativeModeTabs.REDSTONE_BLOCKS, Items.REDSTONE);
    }
}
