package svenhjol.charm.common.features.endermite_powder.common;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class EndermitePowderEntity extends Entity {
    private static final String TARGET_X = "targetX";
    private static final String TARGET_Z = "targetZ";
    private static final EntityDataAccessor<Integer> DATA_TARGET_X = SynchedEntityData.defineId(EndermitePowderEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_Z = SynchedEntityData.defineId(EndermitePowderEntity.class, EntityDataSerializers.INT);
    private int ticks;

    public EndermitePowderEntity(EntityType<? extends EndermitePowderEntity> type, Level level) {
        super(type, level);
    }

    public EndermitePowderEntity(EntityType<? extends EndermitePowderEntity> type, Level level, int targetX, int targetZ) {
        this(type, level);
        setTarget(targetX, targetZ);
    }

    private void setTarget(int x, int z) {
        entityData.set(DATA_TARGET_X, x);
        entityData.set(DATA_TARGET_Z, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_TARGET_X, 0);
        builder.define(DATA_TARGET_Z, 0);
    }

    @Override
    public void tick() {
        super.tick();
        var target = new Vec3(entityData.get(DATA_TARGET_X) - blockPosition().getX(), 0.0, entityData.get(DATA_TARGET_Z) - blockPosition().getZ());
        var direction = target.normalize().scale(0.2);
        setPos(blockPosition().getX() + direction.x * ticks, blockPosition().getY() + direction.y * ticks + ticks * 0.03, blockPosition().getZ() + direction.z * ticks);
        if (level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 18; i++) {
                serverLevel.sendParticles(ParticleTypes.PORTAL, getX() + (random.nextDouble() - 0.5) * 0.5, getY() + (random.nextDouble() - 0.5) * 0.5, getZ() + (random.nextDouble() - 0.5) * 0.5, 1, 0.2, 0.12, 0.1, 0.06);
            }
        }
        ticks++;
        if (ticks > 1000) {
            discard();
            ticks = 0;
        }
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        output.putInt(TARGET_X, entityData.get(DATA_TARGET_X));
        output.putInt(TARGET_Z, entityData.get(DATA_TARGET_Z));
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        setTarget(input.getIntOr(TARGET_X, 0), input.getIntOr(TARGET_Z, 0));
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }
}
