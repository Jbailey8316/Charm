package svenhjol.charm.common.features.beekeepers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class BeekeeperTags {
    public static final TagKey<Item> SELL_FLOWERS = TagKey.create(Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("charm", "beekeepers_sell_flowers"));
    private BeekeeperTags() {}
}
