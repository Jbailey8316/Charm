package svenhjol.charm.client.features.copper_pistons;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.client.ClientRegistry;

@FeatureDefinition(side = Side.Client)
public final class CopperPistons extends SidedFeature {
    public final svenhjol.charm.common.features.copper_pistons.CopperPistons common;

    public CopperPistons(Mod mod) {
        super(mod);
        common = svenhjol.charm.common.features.copper_pistons.CopperPistons.feature();
        new Registers(this);
    }

    public static CopperPistons feature() { return Mod.getSidedFeature(CopperPistons.class); }

    private static final class Registers extends Setup<CopperPistons> {
        Registers(CopperPistons feature) { super(feature); }

        @Override public Runnable boot() { return () -> {
            var registry = ClientRegistry.forFeature(feature());
            registry.itemTab(feature().common.registers.copperPistonItem.get(), CreativeModeTabs.REDSTONE_BLOCKS, Items.PISTON);
            registry.itemTab(feature().common.registers.stickyCopperPistonItem.get(), CreativeModeTabs.REDSTONE_BLOCKS, feature().common.registers.copperPistonItem.get());
        }; }
    }
}
