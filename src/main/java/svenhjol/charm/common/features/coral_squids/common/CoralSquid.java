package svenhjol.charm.common.features.coral_squids.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import svenhjol.charm.common.features.coral_squids.CoralSquids;
import svenhjol.charmony.core.base.Mod;

import javax.annotation.Nullable;

public class CoralSquid extends WaterAnimal implements Bucketable {
    private static final String VARIANT_TAG = "Variant";
    private static final String FROM_BUCKET_TAG = "FromBucket";
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(CoralSquid.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(CoralSquid.class, EntityDataSerializers.INT);

    public float xBodyRot, xBodyRot0, zBodyRot, zBodyRot0;
    public float tentacleMovement, oldTentacleMovement, tentacleAngle, oldTentacleAngle;
    private float speed, tentacleSpeed, rotateSpeed, swimX, swimY, swimZ;

    public CoralSquid(EntityType<? extends CoralSquid> entityType, Level level) {
        super(entityType, level);
        random.setSeed(getId());
        tentacleSpeed = 1.0F / (random.nextFloat() + 1.0F) * 0.2f;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
        if (spawnReason != EntitySpawnReason.BUCKET) setVariant(Variant.randomly(level.getRandom()));
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }

    public static boolean canSpawn(EntityType<CoralSquid> type, LevelAccessor level, EntitySpawnReason reason,
                                   BlockPos pos, RandomSource random) {
        for (int y = 0; y > -16; y--) {
            if (level.getBlockState(pos.offset(0, y, 0)).getBlock() instanceof BaseCoralPlantTypeBlock) return pos.getY() > 20 && pos.getY() < level.getSeaLevel();
        }
        return false;
    }

    @Override public int getMaxSpawnClusterSize() { return 4; }

    public Variant getVariant() { return Variant.byId(entityData.get(DATA_VARIANT)); }
    public void setVariant(Variant variant) { entityData.set(DATA_VARIANT, variant.getId()); }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, 0);
        builder.define(FROM_BUCKET, false);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt(VARIANT_TAG, getVariant().getId());
        output.putBoolean(FROM_BUCKET_TAG, fromBucket());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        setVariant(Variant.byId(input.getIntOr(VARIANT_TAG, 0)));
        setFromBucket(input.getBooleanOr(FROM_BUCKET_TAG, false));
    }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new SquidRandomMovementGoal());
        goalSelector.addGoal(1, new SquidFleeGoal());
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean allowDrops) {
        super.dropCustomDeathLoot(level, source, allowDrops);
        var feature = Mod.tryGetSidedFeature(CoralSquids.class);
        if (feature.isPresent() && random.nextDouble() < feature.get().dropChance()) spawnAtLocation(level, getVariant().getDrop());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 5.0f);
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SQUID_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SQUID_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SQUID_DEATH; }
    @Override protected float getSoundVolume() { return 0.4F; }
    @Override public float getVoicePitch() { return 1.25f; }

    @Override public void aiStep() {
        super.aiStep();
        xBodyRot0 = xBodyRot; zBodyRot0 = zBodyRot; oldTentacleMovement = tentacleMovement; oldTentacleAngle = tentacleAngle;
        tentacleMovement += tentacleSpeed;
        if (tentacleMovement > Math.PI * 2) {
            if (level().isClientSide()) tentacleMovement = (float)Math.PI * 2;
            else { tentacleMovement -= (float)Math.PI * 2; if (random.nextInt(10) == 0) tentacleSpeed = 1.0f / (random.nextFloat() + 1.0f) * 0.2f; level().broadcastEntityEvent(this, (byte)19); }
        }
        if (isInWater()) {
            if (tentacleMovement < Math.PI) { float f = tentacleMovement / (float)Math.PI; tentacleAngle = Mth.sin(f * f * (float)Math.PI) * (float)Math.PI * 0.25f; if (f > 0.75) { speed = 1.0f; rotateSpeed = 1.0f; } else rotateSpeed *= 0.8f; }
            else { tentacleAngle = 0.0f; speed *= 0.9f; rotateSpeed *= 0.99f; }
            if (!level().isClientSide()) setDeltaMovement(swimX * speed, swimY * speed, swimZ * speed);
            Vec3 movement = getDeltaMovement(); double horizontal = movement.horizontalDistance(); yBodyRot += (-((float)Mth.atan2(movement.x, movement.z)) * 57.295776f - yBodyRot) * 0.1f; setYRot(yBodyRot); zBodyRot += (float)Math.PI * rotateSpeed * 1.5f; xBodyRot += (-((float)Mth.atan2(horizontal, movement.y)) * 57.295776f - xBodyRot) * 0.1f;
        } else { tentacleAngle = Mth.abs(Mth.sin(tentacleMovement)) * (float)Math.PI * 0.25f; if (!level().isClientSide()) { double y = getDeltaMovement().y; y = hasEffect(MobEffects.LEVITATION) ? 0.05 * (getEffect(MobEffects.LEVITATION).getAmplifier() + 1) : y - getGravity(); setDeltaMovement(0.0, y * 0.98f, 0.0); } xBodyRot += (-90.0f - xBodyRot) * 0.02f; }
    }

    @Override public void travel(Vec3 movementInput) { move(MoverType.SELF, getDeltaMovement()); }
    @Override public void handleEntityEvent(byte status) { if (status == 19) tentacleMovement = 0.0f; else super.handleEntityEvent(status); }
    public void setMovementVector(float x, float y, float z) { swimX = x; swimY = y; swimZ = z; }
    public boolean hasMovementVector() { return swimX != 0.0f || swimY != 0.0f || swimZ != 0.0f; }

    @Override protected InteractionResult mobInteract(net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }
    @Override public boolean fromBucket() { return entityData.get(FROM_BUCKET); }
    @Override public void setFromBucket(boolean value) { entityData.set(FROM_BUCKET, value); }
    @Override public void saveToBucketTag(ItemStack stack) { Bucketable.saveDefaultDataToBucketTag(this, stack); CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, tag -> tag.putInt(VARIANT_TAG, getVariant().getId())); }
    @Override public void loadFromBucketTag(CompoundTag tag) { Bucketable.loadDefaultDataFromBucketTag(this, tag); setVariant(Variant.byId(tag.getInt(VARIANT_TAG).orElse(0))); }
    @Override public ItemStack getBucketItemStack() { return new ItemStack(CoralSquids.feature().registers.bucketItem.get()); }
    @Override public SoundEvent getPickupSound() { return CoralSquids.feature().registers.coralSquidBucketFill.get(); }
    @Override public boolean requiresCustomPersistence() { return super.requiresCustomPersistence() || fromBucket(); }
    @Override public boolean removeWhenFarAway(double distanceSquared) { return !fromBucket() && !hasCustomName(); }

    private class SquidFleeGoal extends Goal {
        private int timer;
        @Override public boolean canUse() { var attacker = getLastHurtByMob(); return isInWater() && attacker != null && distanceToSqr(attacker) < 100.0f; }
        @Override public void start() { timer = 0; }
        @Override public void tick() { ++timer; var attacker = getLastHurtByMob(); if (attacker == null) return; var vec = new Vec3(getX() - attacker.getX(), getY() - attacker.getY(), getZ() - attacker.getZ()); var pos = blockPosition().offset((int)vec.x, (int)vec.y, (int)vec.z); var state = level().getBlockState(pos); var fluid = level().getFluidState(pos); if (fluid.is(FluidTags.WATER) || state.isAir()) { double d = vec.length(); if (d > 0) { vec = vec.normalize(); float f = d > 5 ? (float)(3.0 - (d - 5.0) / 5.0) : 3.0f; if (f > 0) vec = vec.scale(f); } if (state.isAir()) vec = vec.subtract(0, vec.y, 0); setMovementVector((float)vec.x / 20.0f, (float)vec.y / 20.0f, (float)vec.z / 20.0f); } if (timer % 10 == 5) level().addParticle(ParticleTypes.BUBBLE, getX(), getY(), getZ(), 0, 0, 0); }
    }

    private class SquidRandomMovementGoal extends Goal {
        @Override public boolean canUse() { return true; }
        @Override public void tick() { var noAction = getNoActionTime(); if (noAction > 100) setMovementVector(0, 0, 0); else if (getRandom().nextInt(30) == 0 || !wasTouchingWater || !hasMovementVector()) { var f = getRandom().nextFloat() * 6.2831855f; setMovementVector(Mth.cos(f) * 0.2f, -0.1F + getRandom().nextFloat() * 0.05f, Mth.sin(f) * 0.2f); } }
    }
}
