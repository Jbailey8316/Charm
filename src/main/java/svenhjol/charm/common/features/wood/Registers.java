package svenhjol.charm.common.features.wood;

import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;
import svenhjol.charmony.core.common.features.wood.WoodRegistry;

public final class Registers extends Setup<VanillaWoodVariants> {
    public Registers(VanillaWoodVariants feature) {
        super(feature);

        var woodRegistry = WoodRegistry.forRegistry(CommonRegistry.forFeature(feature));
        var materials = VanillaWoodMaterial.values();

        // Creative-tab insertion is anchored after the vanilla barrel. Register in reverse
        // so repeated addAfter calls produce the declared material order in the final tab.
        for (var i = materials.length - 1; i >= 0; i--) {
            woodRegistry.barrel(materials[i]);
        }
    }
}
