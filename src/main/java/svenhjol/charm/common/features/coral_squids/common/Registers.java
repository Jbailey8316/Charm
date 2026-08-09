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

public final class Registers extends Setup<CoralSquids> {
    public final Supplier<SpawnEggItem> spawnEggItem;
    public final Supplier<Item> bucketItem;
    public final Supplier<EntityType<CoralSquid>> entity;
    public final Supplier<SoundEvent> coralSquidBucketFill;

    public Registers(CoralSquids feature) {
        super(feature);
        var registry = CommonRegistry.forFeature(feature);
        entity = registry.entity("coral_squid", () -> EntityType.Builder.of(CoralSquid::new, MobCategory.WATER_AMBIENT).sized(0.54f, 0.54f).clientTrackingRange(10));
        coralSquidBucketFill = registry.sound("coral_squid_bucket_fill");
        bucketItem = registry.item("coral_squid_bucket", key -> new BucketItem(entity.get(), new Item.Properties().setId(key)));
        spawnEggItem = registry.item("coral_squid_spawn_egg", key -> new SpawnEggItem(new Item.Properties().spawnEgg(entity.get()).setId(key)));
        registry.biomeSpawn(holder -> holder.is(Tags.SPAWNS_CORAL_SQUIDS), MobCategory.WATER_AMBIENT, entity, 50, 2, 4);
        registry.mobSpawnPlacement(entity, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CoralSquid::canSpawn);
        registry.mobAttributes(entity, CoralSquid::createAttributes);
    }

}
