package svenhjol.charm.common.features.beekeepers;

import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Items;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;
import svenhjol.charmony.core.common.GenericTrades;

import java.util.function.Supplier;

public final class BeekeeperRegisters extends Setup<Beekeepers> {
    public BeekeeperRegisters(Beekeepers feature) {
        super(feature);
        CommonRegistry registry = CommonRegistry.forFeature(feature);
        Supplier<net.minecraft.resources.ResourceKey<VillagerProfession>> profession = feature::professionKey;
        registry.villagerTrade(profession, 1, () -> new GenericTrades.EmeraldsForTag<>(BeekeeperTags.SELL_FLOWERS, 14, 2, 1, 0, 2, 20));
        registry.villagerTrade(profession, 1, () -> new GenericTrades.ItemsForEmeralds(Items.GLASS_BOTTLE, 1, 3, 2, 20));
        registry.villagerTrade(profession, 2, () -> new GenericTrades.EmeraldsForItems(Items.HONEYCOMB, 9, 2, 1, 0, 10, 20));
        registry.villagerTrade(profession, 2, () -> new GenericTrades.TagForEmeralds<>(net.minecraft.tags.ItemTags.CANDLES, 3, 1, 1, 1, 5, 20));
        registry.villagerTrade(profession, 3, () -> new GenericTrades.EmeraldsForItems(Items.CHARCOAL, 15, 2, 1, 0, 5, 20));
        registry.villagerTrade(profession, 3, () -> new GenericTrades.ItemsForEmeralds(Items.CAMPFIRE, 2, 1, 10, 20));
        registry.villagerTrade(profession, 4, () -> new GenericTrades.EmeraldsForItems(Items.FLOWERING_AZALEA_LEAVES, 7, 1, 1, 0, 15, 20));
        registry.villagerTrade(profession, 4, () -> new GenericTrades.ItemsForEmeralds(Items.LEAD, 6, 1, 15, 20));
        registry.villagerTrade(profession, 5, () -> new BeekeeperTrades.TallFlowerForEmeralds());
        registry.villagerTrade(profession, 5, () -> new GenericTrades.ItemsForEmeralds(Items.SOUL_CAMPFIRE, 5, 1, 30, 20));
        registry.villagerTrade(profession, 5, BeekeeperTrades.EnchantedShearsForEmeralds::new);
        registry.villagerTrade(profession, 5, BeekeeperTrades.PopulatedBeehiveForEmeralds::new);
    }
}
