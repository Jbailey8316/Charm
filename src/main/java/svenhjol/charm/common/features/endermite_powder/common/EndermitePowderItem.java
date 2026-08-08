package svenhjol.charm.common.features.endermite_powder.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import svenhjol.charm.common.features.endermite_powder.EndermitePowder;

public final class EndermitePowderItem extends Item {
    public EndermitePowderItem(Properties properties) { super(properties); }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (level.dimension() != Level.END) return InteractionResult.PASS;
        player.getCooldowns().addCooldown(stack, 40);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        if (level.isClientSide()) {
            level.playSound(player, player.blockPosition(), EndermitePowder.feature().launchSound.get(), SoundSource.PLAYERS, 0.4f, 0.9f + level.random.nextFloat() * 0.2f);
            return InteractionResult.SUCCESS;
        }
        var serverLevel = (ServerLevel) level;
        var tag = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath("charmony", "endermite_powder_located"));
        var target = serverLevel.findNearestMapStructure(tag, player.blockPosition(), 1500, false);
        if (target == null) return InteractionResult.FAIL;
        var look = player.getLookAngle();
        var entity = new EndermitePowderEntity(EndermitePowder.feature().entity.get(), serverLevel, target.getX(), target.getZ());
        entity.setPos(player.getX() + look.x * 2.0, player.getBlockY() + 0.5, player.getZ() + look.z * 2.0);
        serverLevel.addFreshEntity(entity);
        return InteractionResult.SUCCESS;
    }
}
