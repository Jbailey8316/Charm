package svenhjol.charm.client.features.smooth_glowstone;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.client.ClientRegistry;
import svenhjol.charm.common.features.smooth_glowstone.SmoothGlowstone;

@FeatureDefinition(side = Side.Client)
public final class SmoothGlowstoneClient extends SidedFeature {
    public SmoothGlowstoneClient(Mod mod) {
        super(mod);
        var common = SmoothGlowstone.feature();
        ClientRegistry.forFeature(this).itemTab(common.item.get(), CreativeModeTabs.NATURAL_BLOCKS, Items.GLOWSTONE);
    }
}
