package svenhjol.charm.common.features.item_hover_sorting.common;
import svenhjol.charm.common.features.item_hover_sorting.*;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;
public final class Registers extends Setup<ItemHoverSorting> {
    public Registers(ItemHoverSorting f){super(f);}
    @Override public Runnable boot(){return ()->{var r=CommonRegistry.forFeature(feature());r.packetSender(Side.Client,Networking.C2SSort.TYPE,Networking.C2SSort.CODEC);r.packetReceiver(Networking.C2SSort.TYPE,()->feature().handlers::sort);};}
}
