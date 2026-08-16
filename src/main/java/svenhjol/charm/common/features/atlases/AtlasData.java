package svenhjol.charm.common.features.atlases;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import java.util.List;

public record AtlasData(int scale, int activeMap, int emptyMaps, List<AtlasMapEntry> maps) {
    public static final int INITIAL_EMPTY_MAPS = 3;
    public static final int DEFAULT_SCALE = 0;
    public static final Codec<AtlasData> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.intRange(0, 4).fieldOf("scale").forGetter(AtlasData::scale),
        Codec.INT.fieldOf("active_map").forGetter(AtlasData::activeMap),
        Codec.intRange(0, INITIAL_EMPTY_MAPS).fieldOf("empty_maps").forGetter(AtlasData::emptyMaps),
        AtlasMapEntry.CODEC.listOf().fieldOf("maps").forGetter(AtlasData::maps)
    ).apply(i, AtlasData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AtlasData> STREAM_CODEC =
        StreamCodec.composite(ByteBufCodecs.VAR_INT, AtlasData::scale,
            ByteBufCodecs.VAR_INT, AtlasData::activeMap,
            ByteBufCodecs.VAR_INT, AtlasData::emptyMaps,
            AtlasMapEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), AtlasData::maps,
            AtlasData::new);

    public static AtlasData initial() { return new AtlasData(DEFAULT_SCALE, -1, INITIAL_EMPTY_MAPS, List.of()); }

    public AtlasData withActive(int index) { return new AtlasData(scale, index, emptyMaps, maps); }
    public AtlasData add(AtlasMapEntry entry) {
        var result = new java.util.ArrayList<>(maps);
        result.add(entry);
        return new AtlasData(scale, result.size() - 1, emptyMaps - 1, List.copyOf(result));
    }

}
