package svenhjol.charm.common.features.atlases;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.Registry;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.common.CommonRegistry;

@FeatureDefinition(side = Side.Common, description = "Adds map-holding Atlases.")
public final class Atlases extends SidedFeature {
    private static Atlases INSTANCE;
    public final Registerable<DataComponentType<AtlasData>> data;
    public final Registerable<Item> item;
    public final Registerable<MenuType<AtlasMenu>> menu;

    public Atlases(Mod mod) {
        super(mod);
        INSTANCE = this;
        data = CommonRegistry.forFeature(this).dataComponent(id("atlas"), () -> b -> b.persistent(AtlasData.CODEC).networkSynchronized(AtlasData.STREAM_CODEC));
        item = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.ITEM, id("atlas"),
            new AtlasItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("atlas"))).component(data.get(), AtlasData.initial()))));
        menu = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.MENU, id("atlas"),
            new MenuType<>(AtlasMenu::new, net.minecraft.world.flag.FeatureFlags.VANILLA_SET)));
        new AtlasRegisters(this);
    }

    public static Atlases feature() { return INSTANCE; }
    public boolean openInOffHand() { return false; }
    @Override public void run() { AtlasHandlers.register(); }
}
