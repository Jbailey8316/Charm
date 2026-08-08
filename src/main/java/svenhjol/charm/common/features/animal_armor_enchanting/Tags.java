package svenhjol.charm.common.features.animal_armor_enchanting;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public final class Tags {
    public static final TagKey<Item> HORSE_ARMOR = TagKey.create(Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("charm", "horse_armor"));
    public static final TagKey<Item> WOLF_ARMOR = TagKey.create(Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("charm", "wolf_armor"));
    public static final TagKey<Enchantment> ON_HORSE_ARMOR = TagKey.create(Registries.ENCHANTMENT,
        ResourceLocation.fromNamespaceAndPath("charm", "on_horse_armor"));
    public static final TagKey<Enchantment> ON_WOLF_ARMOR = TagKey.create(Registries.ENCHANTMENT,
        ResourceLocation.fromNamespaceAndPath("charm", "on_wolf_armor"));

    private Tags() {}
}
