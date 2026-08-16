package svenhjol.charm.common.features.beekeepers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.Bees;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import svenhjol.charmony.core.helpers.EnchantmentsHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.util.RandomSource;

/** Small Beekeeper-specific listings retained where a generic listing cannot select a flower. */
public final class BeekeeperTrades {
    private BeekeeperTrades() {}

    public static final class TallFlowerForEmeralds implements VillagerTrades.ItemListing {
        @Override
        public MerchantOffer getOffer(Entity merchant, RandomSource random) {
            ItemStack flower = new ItemStack(switch (random.nextInt(4)) {
                case 0 -> Items.SUNFLOWER;
                case 1 -> Items.LILAC;
                case 2 -> Items.ROSE_BUSH;
                default -> Items.PEONY;
            });
            return new MerchantOffer(new net.minecraft.world.item.trading.ItemCost(Items.EMERALD, 3 + random.nextInt(2)),
                flower, 20, 15, 0.05F);
        }
    }

    public static final class EnchantedShearsForEmeralds implements VillagerTrades.ItemListing {
        @Override
        public MerchantOffer getOffer(Entity merchant, RandomSource random) {
            var holder = EnchantmentsHelper.holder(merchant.level().registryAccess(), Enchantments.UNBREAKING);
            if (holder == null) return null;
            int level = random.nextDouble() < 0.1 ? 3 : 1 + random.nextInt(2);
            int emeralds = 6 + (level > 1 ? level * level : 0);
            var shears = new ItemStack(Items.SHEARS);
            EnchantmentHelper.updateEnchantments(shears, mutable -> mutable.set(holder, level));
            return new MerchantOffer(new net.minecraft.world.item.trading.ItemCost(Items.EMERALD, emeralds), shears, 20, 15, 0.05F);
        }
    }

    public static final class PopulatedBeehiveForEmeralds implements VillagerTrades.ItemListing {
        @Override
        public MerchantOffer getOffer(Entity merchant, RandomSource random) {
            var hive = new ItemStack(Items.BEEHIVE);
            hive.set(DataComponents.BEES, new Bees(java.util.List.of(
                BeehiveBlockEntity.Occupant.create(0), BeehiveBlockEntity.Occupant.create(0))));
            hive.set(DataComponents.CUSTOM_NAME, Component.translatable("item.charm.populated_beehive"));
            return new MerchantOffer(new net.minecraft.world.item.trading.ItemCost(Items.EMERALD, 47), hive, 10, 30, 0.05F);
        }
    }
}
