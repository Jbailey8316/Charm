package svenhjol.charm.common.features.item_hover_sorting;
import svenhjol.charm.common.features.item_hover_sorting.common.Handlers;
import svenhjol.charm.common.features.item_hover_sorting.common.Registers;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
@FeatureDefinition(side = Side.Common, description = "Sorts supported container items while hovering over them.")
public final class ItemHoverSorting extends SidedFeature {
    private static ItemHoverSorting INSTANCE;
    public final Handlers handlers; public final Registers registers;
    public ItemHoverSorting(Mod mod) { super(mod); INSTANCE = this; handlers = new Handlers(this); registers = new Registers(this); }
    public static ItemHoverSorting feature() { return INSTANCE; }
}
