package svenhjol.charm.common.features.endermite_powder.common;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import svenhjol.charm.common.features.endermite_powder.EndermitePowder;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.EnchantmentsHelper;

public final class Registers extends Setup<EndermitePowder> {
    public Registers(EndermitePowder feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            svenhjol.charmony.api.events.EntityKilledDropCallback.EVENT.register((entity, source) -> {
            if (entity.getType() != EntityType.ENDERMITE) return InteractionResult.PASS;
            var looting = EnchantmentsHelper.lootingLevel(source);
            var count = entity.getRandom().nextInt(Math.max(1, looting + 1));
            if (entity.getRandom().nextFloat() < 0.3f * feature().maxDrops()) count++;
            if (entity.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                entity.spawnAtLocation(serverLevel, new ItemStack(feature().item.get(), count));
            }
            return InteractionResult.PASS;
            });
            svenhjol.charmony.core.common.CommonRegistry.forFeature(feature()).wandererTrade(() -> new EndermitePowderTrade(), true);
        };
    }

    private final class EndermitePowderTrade implements VillagerTrades.ItemListing {
        @Override
        public MerchantOffer getOffer(Entity merchant, net.minecraft.util.RandomSource random) {
            return new MerchantOffer(new ItemCost(Items.EMERALD, 20), new ItemStack(feature().item.get(), 3), 1, 1, 1.0f);
        }
    }
}
