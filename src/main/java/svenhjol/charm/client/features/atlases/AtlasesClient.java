package svenhjol.charm.client.features.atlases;

import net.minecraft.world.item.CreativeModeTabs;
import svenhjol.charm.common.features.atlases.Atlases;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.client.ClientRegistry;

@FeatureDefinition(side = Side.Client)
public final class AtlasesClient extends SidedFeature {
    public AtlasesClient(Mod mod) {
        super(mod);
        new Setup<AtlasesClient>(this) {
            @Override public Runnable boot() {
                return () -> ClientRegistry.forFeature(AtlasesClient.this)
                    .itemTab(Atlases.feature().item.get(), CreativeModeTabs.TOOLS_AND_UTILITIES, null);
            }
        };
    }
}
