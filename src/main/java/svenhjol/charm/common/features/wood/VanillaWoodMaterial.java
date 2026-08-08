package svenhjol.charm.common.features.wood;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

import java.util.Locale;

public enum VanillaWoodMaterial implements WoodMaterial {
    ACACIA(BlockSetType.ACACIA, WoodType.ACACIA, true),
    BAMBOO(BlockSetType.BAMBOO, WoodType.BAMBOO, true),
    BIRCH(BlockSetType.BIRCH, WoodType.BIRCH, true),
    CHERRY(BlockSetType.CHERRY, WoodType.CHERRY, true),
    CRIMSON(BlockSetType.CRIMSON, WoodType.CRIMSON, false),
    DARK_OAK(BlockSetType.DARK_OAK, WoodType.DARK_OAK, true),
    JUNGLE(BlockSetType.JUNGLE, WoodType.JUNGLE, true),
    MANGROVE(BlockSetType.MANGROVE, WoodType.MANGROVE, true),
    OAK(BlockSetType.OAK, WoodType.OAK, true),
    PALE_OAK(BlockSetType.PALE_OAK, WoodType.PALE_OAK, true),
    SPRUCE(BlockSetType.SPRUCE, WoodType.SPRUCE, true),
    WARPED(BlockSetType.WARPED, WoodType.WARPED, false);

    private final BlockSetType blockSetType;
    private final WoodType woodType;
    private final boolean flammable;

    VanillaWoodMaterial(BlockSetType blockSetType, WoodType woodType, boolean flammable) {
        this.blockSetType = blockSetType;
        this.woodType = woodType;
        this.flammable = flammable;
    }

    @Override
    public BlockSetType blockSetType() {
        return blockSetType;
    }

    @Override
    public boolean isFlammable() {
        return flammable;
    }

    @Override
    public SoundType soundType() {
        return SoundType.WOOD;
    }

    @Override
    public WoodType woodType() {
        return woodType;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
