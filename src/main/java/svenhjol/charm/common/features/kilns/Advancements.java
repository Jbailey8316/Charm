package svenhjol.charm.common.features.kilns;

import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public final class Advancements extends Setup<Kilns> {
    public Advancements(Kilns feature) { super(feature); }
    public void firedItem(Player player) { AdvancementHelper.trigger("fired_item", player); }
}
