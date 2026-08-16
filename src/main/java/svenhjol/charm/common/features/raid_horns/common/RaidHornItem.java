package svenhjol.charm.common.features.raid_horns.common;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

public final class RaidHornItem extends Item {
    public RaidHornItem(Properties properties) { super(properties.durability(4)); }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return RaidHornHandlers.use(this, level, player, hand);
    }
    @Override public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.TOOT_HORN; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return RaidHornHandlers.DURATION; }
}
