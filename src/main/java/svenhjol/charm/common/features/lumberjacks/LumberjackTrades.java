package svenhjol.charm.common.features.lumberjacks;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import svenhjol.charmony.core.common.GenericTrades;

public final class LumberjackTrades {
    public static final class SaplingsForEmeralds implements VillagerTrades.ItemListing {
        private final List<? extends ItemLike> saplings;
        private final int baseEmeralds;
        private final int extraEmeralds;
        private final int xp;
        private final int maxUses;

        public SaplingsForEmeralds(List<? extends ItemLike> saplings, int baseEmeralds, int extraEmeralds, int xp, int maxUses) {
            this.saplings = saplings;
            this.baseEmeralds = baseEmeralds;
            this.extraEmeralds = extraEmeralds;
            this.xp = xp;
            this.maxUses = maxUses;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity merchant, RandomSource random) {
            var sapling = new ItemStack(saplings.get(random.nextInt(saplings.size())));
            return new MerchantOffer(GenericTrades.getCost(random, Items.EMERALD, baseEmeralds, extraEmeralds), sapling,
                maxUses, xp, 0.2F);
        }
    }

    public static final class BarkForLogs implements VillagerTrades.ItemListing {
        private final int baseCost;
        private final int extraCost;
        private final int xp;
        private final int maxUses;

        public BarkForLogs(int baseCost, int extraCost, int xp, int maxUses) {
            this.baseCost = baseCost;
            this.extraCost = extraCost;
            this.xp = xp;
            this.maxUses = maxUses;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity merchant, RandomSource random) {
            Map<Block, Block> map = new HashMap<>();
            map.put(Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD);
            map.put(Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD);
            map.put(Blocks.CHERRY_LOG, Blocks.CHERRY_WOOD);
            map.put(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD);
            map.put(Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD);
            map.put(Blocks.MANGROVE_LOG, Blocks.MANGROVE_WOOD);
            map.put(Blocks.OAK_LOG, Blocks.OAK_WOOD);
            map.put(Blocks.PALE_OAK_LOG, Blocks.PALE_OAK_WOOD);
            map.put(Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD);
            var logs = new ArrayList<>(map.keySet());
            var log = logs.get(random.nextInt(logs.size()));
            var wood = map.get(log);
            var count = baseCost + random.nextInt(extraCost + 1);
            return new MerchantOffer(GenericTrades.getCost(random, Items.EMERALD, 1, 0),
                Optional.of(GenericTrades.getCost(random, log, 1, 0)), new ItemStack(wood, count),
                maxUses, xp, 0.2F);
        }
    }

    private LumberjackTrades() {}
}
