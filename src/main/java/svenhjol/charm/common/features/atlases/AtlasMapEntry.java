package svenhjol.charm.common.features.atlases;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapId;

public record AtlasMapEntry(ItemStack map, MapId mapId, int centerX, int centerZ, ResourceLocation dimension) {
    public static final Codec<AtlasMapEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
        ItemStack.CODEC.fieldOf("map").forGetter(AtlasMapEntry::map),
        MapId.CODEC.fieldOf("map_id").forGetter(AtlasMapEntry::mapId),
        Codec.INT.fieldOf("center_x").forGetter(AtlasMapEntry::centerX),
        Codec.INT.fieldOf("center_z").forGetter(AtlasMapEntry::centerZ),
        ResourceLocation.CODEC.fieldOf("dimension").forGetter(AtlasMapEntry::dimension)
    ).apply(i, AtlasMapEntry::new));

    public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, AtlasMapEntry> STREAM_CODEC =
        StreamCodec.composite(ItemStack.STREAM_CODEC, AtlasMapEntry::map,
            MapId.STREAM_CODEC, AtlasMapEntry::mapId,
            ByteBufCodecs.INT, AtlasMapEntry::centerX,
            ByteBufCodecs.INT, AtlasMapEntry::centerZ,
            ResourceLocation.STREAM_CODEC, AtlasMapEntry::dimension,
            AtlasMapEntry::new);
}
