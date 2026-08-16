package svenhjol.charm.common.features.raid_horns.common;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import svenhjol.charm.common.features.raid_horns.RaidHorns;
import svenhjol.charmony.core.common.GenericTrades;
import svenhjol.charmony.core.helpers.EnchantmentsHelper;

import java.util.List;

public final class RaidHornHandlers {
    public static final int DURATION = 140;
    private static final double DROP_CHANCE = 0.05D;
    private static final double LOOTING_MULTIPLIER = 0.1D;
    private final RaidHorns feature;

    public RaidHornHandlers(RaidHorns feature) { this.feature = feature; }

    public InteractionResult entityDrop(LivingEntity entity, DamageSource source) {
        if (!entity.level().isClientSide() && entity instanceof PatrollingMonster patroller && patroller.isPatrolLeader() && source.getEntity() instanceof Player && entity.level().random.nextDouble() <= DROP_CHANCE + EnchantmentsHelper.lootingLevel(source) * LOOTING_MULTIPLIER) {
            entity.level().addFreshEntity(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(feature.item.get())));
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult use(Item item, Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            var server = (ServerLevel) level;
            var pos = player.blockPosition();
            SoundEvent sound;
            if (server.isRaided(pos) && server.getRaidAt(pos) != null) {
                server.getRaidAt(pos).stop();
                sound = RaidHorns.feature().callOffRaidSound.get();
            } else if (trySpawnPatrol(server, pos)) {
                sound = RaidHorns.feature().callPatrolSound.get();
            } else {
                sound = RaidHorns.feature().failSound.get();
            }
            level.playSound(null, player, sound, SoundSource.RECORDS, 16.0F, 1.0F);
            player.getCooldowns().addCooldown(stack, DURATION);
            if (!player.getAbilities().instabuild) stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean trySpawnPatrol(ServerLevel level, BlockPos pos) {
        var random = level.getRandom();
        var target = pos.offset((24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1), 0, (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1));
        if (!level.hasChunksAt(target.getX() - 10, target.getY() - 10, target.getZ() - 10, target.getX() + 10, target.getY() + 10, target.getZ() + 10) || level.getBiome(target).is(BiomeTags.WITHOUT_PATROL_SPAWNS)) return false;
        target = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, target);
        target = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, target);
        var count = (int) Math.ceil(level.getCurrentDifficultyAt(target).getEffectiveDifficulty()) + 1;
        var spawned = false;
        for (var i = 0; i < count; i++) {
            var spawnPos = target.offset(level.getRandom().nextInt(11) - 5, 0, level.getRandom().nextInt(11) - 5);
            spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos);
            var pillager = EntityType.PILLAGER.create(level, EntitySpawnReason.PATROL);
            if (pillager == null) continue;
            pillager.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
            if (i == 0) {
                pillager.setPatrolLeader(true);
                pillager.findPatrolTarget();
            }
            pillager.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.PATROL, null);
            level.addFreshEntityWithPassengers(pillager);
            spawned = true;
        }
        return spawned;
    }

    public static final class RaidHornTrade implements net.minecraft.world.entity.npc.VillagerTrades.ItemListing {
        private final RaidHorns feature;
        public RaidHornTrade(RaidHorns feature) { this.feature = feature; }
        @Override public MerchantOffer getOffer(Entity merchant, net.minecraft.util.RandomSource random) {
            return new MerchantOffer(GenericTrades.getCost(random, net.minecraft.world.item.Items.EMERALD, 30, 0), new ItemStack(feature.item.get()), 1, 30, 0.2F);
        }
    }
}
