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
    public AtlasData withEmptyMaps(int count) {
        return new AtlasData(scale, activeMap, Math.max(0, Math.min(INITIAL_EMPTY_MAPS, count)), maps);
    }
    public AtlasData add(AtlasMapEntry entry) {
        var result = new java.util.ArrayList<>(maps);
        result.add(entry);
        return new AtlasData(scale, result.size() - 1, emptyMaps - 1, List.copyOf(result));
    }

    public AtlasData addImported(AtlasMapEntry entry) {
        var result = new java.util.ArrayList<>(maps);
        result.add(entry);
        return new AtlasData(scale, activeMap, emptyMaps, List.copyOf(result));
    }

    public AtlasData removeMap(int index) {
        if (index < 0 || index >= maps.size()) return this;
        var result = new java.util.ArrayList<>(maps);
        result.remove(index);
        int nextActive = activeMap;
        if (nextActive == index) nextActive = result.isEmpty() ? -1 : Math.min(index, result.size() - 1);
        else if (nextActive > index) nextActive--;
        return new AtlasData(scale, nextActive, emptyMaps, List.copyOf(result));
    }

    public AtlasData replaceMap(int index, AtlasMapEntry entry) {
        if (index < 0 || index >= maps.size()) return addImported(entry);
        var result = new java.util.ArrayList<>(maps);
        result.set(index, entry);
        return new AtlasData(scale, activeMap, emptyMaps, List.copyOf(result));
    }

}
