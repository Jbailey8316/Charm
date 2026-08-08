package svenhjol.charm.common.features.coral_squids.common;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.levelgen.Heightmap;
import svenhjol.charm.common.features.coral_squids.CoralSquids;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;

import java.util.function.Supplier;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public final class Registers extends Setup<CoralSquids> {
    public final Supplier<SpawnEggItem> spawnEggItem;
    public final Supplier<Item> bucketItem;
    public final Supplier<EntityType<CoralSquid>> entity;
    public final Supplier<SoundEvent> coralSquidBucketFill;
    public final Map<Variant, Supplier<CoralSquidHeadBlock>> headBlocks = new EnumMap<>(Variant.class);
    public final Map<Variant, Supplier<BlockItem>> headItems = new EnumMap<>(Variant.class);

    public Registers(CoralSquids feature) {
        super(feature);
        var registry = CommonRegistry.forFeature(feature);
        entity = registry.entity("coral_squid", () -> EntityType.Builder.of(CoralSquid::new, MobCategory.WATER_AMBIENT).sized(0.54f, 0.54f).clientTrackingRange(10));
        coralSquidBucketFill = registry.sound("coral_squid_bucket_fill");
        bucketItem = registry.item("coral_squid_bucket", key -> new BucketItem(entity.get(), new Item.Properties().setId(key)));
        spawnEggItem = registry.item("coral_squid_spawn_egg", key -> new SpawnEggItem(new Item.Properties().spawnEgg(entity.get()).setId(key)));
        for (var variant : Variant.values()) {
            Supplier<CoralSquidHeadBlock> block = registry.block(variant.getName() + "_coral_squid_head", CoralSquidHeadBlock::new);
            var item = registry.item(variant.getName() + "_coral_squid_head", key -> new BlockItem(block.get(), new Item.Properties().setId(key)));
            headBlocks.put(variant, block);
            headItems.put(variant, item);
        }
        registry.biomeSpawn(holder -> holder.is(Tags.SPAWNS_CORAL_SQUIDS), MobCategory.WATER_AMBIENT, entity, 50, 2, 4);
        registry.mobSpawnPlacement(entity, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CoralSquid::canSpawn);
        registry.mobAttributes(entity, CoralSquid::createAttributes);
    }

    @Override public Runnable boot() { return () -> svenhjol.charmony.api.events.EntityKilledDropCallback.EVENT.register((entity, source) -> {
        if (!(entity instanceof CoralSquid squid) || !(source.getEntity() instanceof net.minecraft.server.level.ServerPlayer)) return net.minecraft.world.InteractionResult.PASS;
        var mobDrops = svenhjol.charmony.core.base.Mod.tryGetFeature(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("charmony-tweaks", "mob_drops"));
        if (mobDrops.isEmpty() || !mobDrops.get().enabled()) return net.minecraft.world.InteractionResult.PASS;
        var chance = 0.05d + (svenhjol.charmony.core.helpers.EnchantmentsHelper.lootingLevel(source) * 0.01d);
        if (squid.getRandom().nextDouble() < chance) squid.level().addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(squid.level(), squid.getX(), squid.getY(), squid.getZ(), new net.minecraft.world.item.ItemStack(headItems.get(squid.getVariant()).get())));
        return net.minecraft.world.InteractionResult.PASS;
    }); }
}
