package svenhjol.charm;

import svenhjol.charmony.api.core.ModDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;

@ModDefinition(
    id = CharmMod.ID,
    sides = {Side.Client, Side.Common},
    name = "Charm",
    description = "Adds tweaks and features in keeping with vanilla Minecraft.")
public final class CharmMod extends Mod {
    public static final String ID = "charm";
    private static CharmMod instance;

    private CharmMod() {}

    public static CharmMod instance() {
        if (instance == null) {
            instance = new CharmMod();
        }
        return instance;
    }
}
