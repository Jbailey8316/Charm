package svenhjol.charm.common.features.coral_squids.common;

import net.minecraft.world.level.ItemLike;
import svenhjol.charm.common.features.coral_squids.CoralSquids;
import svenhjol.charmony.api.tweaks.WandererTrade;
import svenhjol.charmony.api.tweaks.WandererTradeProvider;
import svenhjol.charmony.core.base.Setup;

import java.util.List;

public final class Providers extends Setup<CoralSquids> implements WandererTradeProvider {
    public Providers(CoralSquids feature) { super(feature); }

    @Override
    public List<WandererTrade> getRareWandererTrades() {
        return List.of(new WandererTrade() {
            @Override public ItemLike getItem() { return feature().registers.bucketItem.get(); }
            @Override public int getCount() { return 1; }
            @Override public int getCost() { return 12; }
        });
    }
}
