package svenhjol.charm.common.features.lumberjacks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;
import svenhjol.charmony.core.common.GenericTrades;

import java.util.List;
import java.util.function.Supplier;

public final class LumberjackRegisters extends Setup<Lumberjacks> {
    public LumberjackRegisters(Lumberjacks feature) {
        super(feature);
        CommonRegistry registry = CommonRegistry.forFeature(feature);
        Supplier<ResourceKey<VillagerProfession>> profession = feature::professionKey;
        registry.villagerTrade(profession, 1, () -> new GenericTrades.EmeraldsForTag<>(LumberjackTags.OVERWORLD_STRIPPED_LOGS, 8, 1, 1, 0, 2, 20));
        registry.villagerTrade(profession, 1, () -> new GenericTrades.EmeraldsForTag<>(BlockTags.OVERWORLD_NATURAL_LOGS, 8, 1, 1, 0, 2, 20));
        registry.villagerTrade(profession, 1, () -> new LumberjackTrades.SaplingsForEmeralds(List.of(Items.OAK_SAPLING, Items.BIRCH_SAPLING, Items.SPRUCE_SAPLING), 1, 0, 2, 20));
        registry.villagerTrade(profession, 1, () -> feature.customLadders() ? new GenericTrades.TagForEmeralds<>(LumberjackTags.LADDERS, 1, 0, 1, 0, 2, 20) : new GenericTrades.ItemsForEmeralds(Items.LADDER, 1, 1, 2, 20));
        registry.villagerTrade(profession, 2, () -> new GenericTrades.EmeraldsForItems(Items.BONE, 23, 0, 2, 0, 1, 5));
        registry.villagerTrade(profession, 2, () -> new GenericTrades.TagForEmeralds<>(BlockTags.BEDS, 3, 0, 2, 0, 7, 20));
        registry.villagerTrade(profession, 2, () -> new GenericTrades.TagForEmeralds<>(BlockTags.WOODEN_FENCES, 2, 0, 1, 0, 6, 20));
        registry.villagerTrade(profession, 2, () -> new GenericTrades.TagForEmeralds<>(BlockTags.FENCE_GATES, 2, 0, 1, 0, 6, 20));
        registry.villagerTrade(profession, 3, () -> new GenericTrades.EmeraldsForTag<>(BlockTags.WARPED_STEMS, 7, 0, 1, 0, 1, 10));
        registry.villagerTrade(profession, 3, () -> new GenericTrades.EmeraldsForTag<>(BlockTags.CRIMSON_STEMS, 7, 0, 1, 0, 1, 10));
        registry.villagerTrade(profession, 3, () -> new LumberjackTrades.SaplingsForEmeralds(List.of(Items.ACACIA_SAPLING, Items.DARK_OAK_SAPLING), 2, 1, 10, 20));
        registry.villagerTrade(profession, 3, () -> new LumberjackTrades.BarkForLogs(10, 12, 10, 10));
        registry.villagerTrade(profession, 3, () -> new GenericTrades.TagForEmeralds<>(BlockTags.WOODEN_DOORS, 2, 0, 1, 0, 1, 10));
        registry.villagerTrade(profession, 4, () -> feature.customBarrels() ? new GenericTrades.TagForEmeralds<>(LumberjackTags.BARRELS, 4, 0, 1, 0, 1, 15) : new GenericTrades.ItemsForEmeralds(Items.BARREL, 4, 1, 15, 20));
        registry.villagerTrade(profession, 4, () -> feature.customBookshelves() ? new GenericTrades.TagForEmeralds<>(LumberjackTags.CHISELED_BOOKSHELVES, 4, 0, 1, 0, 1, 15) : new GenericTrades.ItemsForEmeralds(Items.BOOKSHELF, 4, 1, 15, 20));
        registry.villagerTrade(profession, 4, () -> new GenericTrades.ItemsForEmeralds(Blocks.NOTE_BLOCK, 7, 0, 1, 0, 1, 15));
        registry.villagerTrade(profession, 5, () -> new GenericTrades.ItemsForEmeralds(Blocks.JUKEBOX, 11, 0, 3, 0, 1, 15));
        registry.villagerTrade(profession, 5, () -> new GenericTrades.ItemsForEmeralds(Blocks.CARTOGRAPHY_TABLE, 5, 0, 1, 0, 1, 30));
        registry.villagerTrade(profession, 5, () -> new GenericTrades.ItemsForEmeralds(Blocks.LOOM, 4, 0, 1, 0, 1, 30));
        registry.villagerTrade(profession, 5, () -> new GenericTrades.ItemsForEmeralds(Blocks.COMPOSTER, 3, 0, 1, 0, 1, 30));
    }
}
