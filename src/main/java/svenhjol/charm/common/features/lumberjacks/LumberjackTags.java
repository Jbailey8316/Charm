package svenhjol.charm.common.features.lumberjacks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import svenhjol.charmony.core.Charmony;

public final class LumberjackTags {
    public static final TagKey<Block> OVERWORLD_STRIPPED_LOGS = TagKey.create(Registries.BLOCK, Charmony.id("overworld_stripped_logs"));
    public static final TagKey<Block> LADDERS = TagKey.create(Registries.BLOCK, Charmony.id("ladders"));
    public static final TagKey<Block> CHISELED_BOOKSHELVES = TagKey.create(Registries.BLOCK, Charmony.id("chiseled_bookshelves"));
    public static final TagKey<Block> BARRELS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "barrels"));
    private LumberjackTags() {}
}
