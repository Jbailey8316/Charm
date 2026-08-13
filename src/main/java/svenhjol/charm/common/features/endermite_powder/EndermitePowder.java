package svenhjol.charm.common.features.endermite_powder;

import net.minecraft.world.item.Item;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import svenhjol.charm.common.features.endermite_powder.common.EndermitePowderEntity;
import svenhjol.charm.common.features.endermite_powder.common.EndermitePowderItem;
import svenhjol.charm.common.features.endermite_powder.common.Advancements;
import svenhjol.charm.common.features.endermite_powder.common.Registers;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.common.CommonRegistry;

@FeatureDefinition(side = Side.Common, description = "Endermites drop endermite powder that can be used to locate an End City.")
public final class EndermitePowder extends SidedFeature {
    @Configurable(name = "Maximum drops", description = "Maximum endermite powder dropped when endermite is killed.")
    private static int maxDrops = 2;
    public final Registerable<Item> item;
    public final Registerable<EntityType<EndermitePowderEntity>> entity;
    public final Registerable<SoundEvent> launchSound;
    public final Advancements advancements;
    public final Registers registers;

    public EndermitePowder(Mod mod) {
        super(mod);
        item = CommonRegistry.forFeature(this).item("endermite_powder", key -> new EndermitePowderItem(new Item.Properties().setId(key)));
        var registry = CommonRegistry.forFeature(this);
        entity = registry.entity("endermite_powder", () -> EntityType.Builder.<EndermitePowderEntity>of((type, level) -> new EndermitePowderEntity(type, level), MobCategory.MISC).sized(2.0f, 2.0f).clientTrackingRange(80).updateInterval(10));
        launchSound = registry.sound("endermite_powder_launch");
        advancements = new Advancements(this);
        registers = new Registers(this);
    }

    public static EndermitePowder feature() { return Mod.getSidedFeature(EndermitePowder.class); }
    public int maxDrops() { return maxDrops; }
}
