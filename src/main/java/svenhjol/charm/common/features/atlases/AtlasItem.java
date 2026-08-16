package svenhjol.charm.common.features.atlases;

import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class AtlasItem extends Item {
    public AtlasItem(Properties properties) { super(properties.stacksTo(1)); }

    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND && !Atlases.feature().openInOffHand()) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            player.openMenu(new SimpleMenuProvider((id, inventory, ignored) -> new AtlasMenu(id, inventory),
                Component.translatable("container.charm.atlas")));
        }
        return InteractionResult.SUCCESS;
    }
}
