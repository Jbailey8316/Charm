package svenhjol.charm.common.features.raid_horns;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import svenhjol.charm.common.features.raid_horns.common.RaidHornHandlers;
import svenhjol.charm.common.features.raid_horns.common.RaidHornItem;
import svenhjol.charm.common.features.raid_horns.common.RaidHornRegisters;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, description = "Adds raid horns that can call off raids or summon pillager patrols.")
public final class RaidHorns extends SidedFeature {
    private static RaidHorns INSTANCE;
    public final Registerable<Item> item;
    public final Registerable<SoundEvent> callPatrolSound;
    public final Registerable<SoundEvent> callOffRaidSound;
    public final Registerable<SoundEvent> failSound;
    public final RaidHornHandlers handlers;
    public final RaidHornRegisters registers;

    public RaidHorns(Mod mod) {
        super(mod);
        INSTANCE = this;
        item = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.ITEM, id("raid_horn"), new RaidHornItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("raid_horn"))))));
        callPatrolSound = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.SOUND_EVENT, id("raid_horn_call_patrol"), SoundEvent.createVariableRangeEvent(id("raid_horn_call_patrol"))));
        callOffRaidSound = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.SOUND_EVENT, id("raid_horn_call_off_raid"), SoundEvent.createVariableRangeEvent(id("raid_horn_call_off_raid"))));
        failSound = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.SOUND_EVENT, id("raid_horn_squeak"), SoundEvent.createVariableRangeEvent(id("raid_horn_squeak"))));
        handlers = new RaidHornHandlers(this);
        registers = new RaidHornRegisters(this);
    }

    public static RaidHorns feature() { return INSTANCE; }
}
