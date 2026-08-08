package svenhjol.charm.common.features.coral_squids.common;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluids;

public final class BucketItem extends MobBucketItem {
    public BucketItem(EntityType<? extends Mob> type, Properties properties) {
        super(type, Fluids.WATER, SoundEvents.BUCKET_EMPTY_AXOLOTL,
            properties.stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY));
    }
}
