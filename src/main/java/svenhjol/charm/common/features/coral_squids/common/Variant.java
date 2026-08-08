package svenhjol.charm.common.features.coral_squids.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import svenhjol.charmony.core.Charmony;

import java.util.function.IntFunction;

public enum Variant implements StringRepresentable {
    TUBE(0, "tube", Items.TUBE_CORAL),
    BRAIN(1, "brain", Items.BRAIN_CORAL),
    BUBBLE(2, "bubble", Items.BUBBLE_CORAL),
    FIRE(3, "fire", Items.FIRE_CORAL),
    HORN(4, "horn", Items.HORN_CORAL);

    private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    private final int id;
    private final String name;
    private final Item drop;

    Variant(int id, String name, Item drop) {
        this.id = id;
        this.name = name;
        this.drop = drop;
    }

    public static Variant byId(int id) { return BY_ID.apply(id); }
    public static Variant randomly(RandomSource random) { return values()[random.nextInt(values().length)]; }
    public int getId() { return id; }
    public String getName() { return name; }
    public Item getDrop() { return drop; }
    public ResourceLocation getTexture() { return Charmony.id("textures/entity/coral_squid/" + name + ".png"); }
    @Override public String getSerializedName() { return name; }
}
