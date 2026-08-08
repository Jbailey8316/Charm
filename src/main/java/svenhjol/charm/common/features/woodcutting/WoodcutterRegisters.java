package svenhjol.charm.common.features.woodcutting;

import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;

public final class WoodcutterRegisters extends Setup<Woodcutters> {
    public WoodcutterRegisters(Woodcutters feature) { super(feature); }
    @Override public Runnable boot() { return () -> { var r=CommonRegistry.forFeature(feature()); r.packetSender(Side.Common, WoodcutterNetworking.S2CRecipes.TYPE, WoodcutterNetworking.S2CRecipes.CODEC); r.packetSender(Side.Client, WoodcutterNetworking.C2SSelect.TYPE, WoodcutterNetworking.C2SSelect.CODEC); r.packetReceiver(WoodcutterNetworking.C2SSelect.TYPE, () -> feature().handlers::select); }; }
}
