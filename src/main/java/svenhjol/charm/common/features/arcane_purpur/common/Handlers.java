package svenhjol.charm.common.features.arcane_purpur.common;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import svenhjol.charm.common.features.arcane_purpur.ArcanePurpur;
import svenhjol.charm.common.features.arcane_purpur.common.Tags;
import svenhjol.charmony.core.base.Setup;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Handlers extends Setup<ArcanePurpur> {
    public Handlers(ArcanePurpur feature) { super(feature); }

    /** Source-backed bounded scan; mutation/teleport is added only after this compiles. */
    public BlockPos findTarget(ServerLevel level, BlockPos pos) {
        var range = feature().teleportRange();
        if (range == 0) return null;
        Map<Double, BlockPos> candidates = new HashMap<>();
        BlockPos.betweenClosedStream(pos.offset(-range, -range, -range), pos.offset(range, range, range)).forEach(candidate -> {
            var above = candidate.above();
            if (!level.getBlockState(candidate).is(Tags.CHORUS_TELEPORTS)) return;
            if (above.equals(pos)) return;
            if (!level.getBlockState(candidate.above()).isAir() || !level.getBlockState(candidate.above(2)).isAir()) return;
            candidates.put(distanceSquared(pos, above), above.immutable());
        });
        return candidates.isEmpty() ? null : candidates.get(Collections.min(candidates.keySet()));
    }

    /** Performs only the source-backed random teleport once a validated target exists. */
    public boolean performTeleport(net.minecraft.world.entity.LivingEntity entity, BlockPos target) {
        if (!(entity.level() instanceof ServerLevel level)) return false;
        var x = target.getX() + 0.5d;
        var y = target.getY();
        var z = target.getZ() + 0.5d;
        if (!entity.randomTeleport(x, y, z, true)) return false;
        level.playSound(null, x, y, z, net.minecraft.sounds.SoundEvents.CHORUS_FRUIT_TELEPORT, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
        entity.playSound(net.minecraft.sounds.SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f);
        return true;
    }

    public boolean tryChorusTeleport(net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.item.ItemStack stack) {
        if (entity.level().isClientSide() || !(entity.level() instanceof ServerLevel level)) return false;
        var target = findTarget(level, entity.blockPosition());
        if (target == null || !performTeleport(entity, target)) return false;
        if (entity instanceof net.minecraft.server.level.ServerPlayer player) {
            player.getCooldowns().addCooldown(stack, 20);
            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        return true;
    }

    private static double distanceSquared(BlockPos from, BlockPos to) {
        var dx = from.getX() - to.getX();
        var dz = from.getZ() - to.getZ();
        return dx * dx + dz * dz;
    }
}
