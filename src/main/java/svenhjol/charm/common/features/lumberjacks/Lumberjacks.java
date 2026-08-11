package svenhjol.charm.common.features.lumberjacks;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import svenhjol.charm.common.features.woodcutting.Woodcutters;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

@FeatureDefinition(side = Side.Common, priority = 15, description = "Adds the Lumberjack villager profession.")
public final class Lumberjacks extends SidedFeature {
    private static Lumberjacks INSTANCE;
    @Configurable(name = "Custom ladders", description = "Uses Charm wood ladders in Lumberjack trades.")
    private static boolean customLadders = true;
    @Configurable(name = "Custom barrels", description = "Uses Charm wood barrels in Lumberjack trades.")
    private static boolean customBarrels = true;
    @Configurable(name = "Custom bookshelves", description = "Uses Charm chiseled bookshelves in Lumberjack trades.")
    private static boolean customBookshelves = true;
    public final Registerable<SoundEvent> workSound;
    public final Registerable<Holder<VillagerProfession>> profession;
    public final LumberjackRegisters registers;

    public Lumberjacks(Mod mod) {
        super(mod);
        INSTANCE = this;
        workSound = new Registerable<>(this, () -> Registry.register(BuiltInRegistries.SOUND_EVENT,
            registryId("lumberjack"), SoundEvent.createVariableRangeEvent(registryId("lumberjack"))));
        var professionKey = ResourceKey.create(Registries.VILLAGER_PROFESSION, registryId("lumberjack"));
        profession = new Registerable<>(this, () -> {
            var poi = Woodcutters.feature().poi.get();
            Predicate<Holder<PoiType>> matches = holder -> holder.unwrapKey().equals(poi.unwrapKey());
            var result = Registry.registerForHolder(BuiltInRegistries.VILLAGER_PROFESSION, professionKey,
                new VillagerProfession(Component.translatable("entity.minecraft.villager.charmony.lumberjack"),
                    matches, matches, ImmutableSet.of(), ImmutableSet.of(Woodcutters.feature().block.get()), workSound.get()));
            VillagerTrades.TRADES.put(professionKey, new Int2ObjectOpenHashMap<>());
            return result;
        });
        registers = new LumberjackRegisters(this);
    }
    public static Lumberjacks feature() { return INSTANCE; }
    public ResourceKey<VillagerProfession> professionKey() { return ResourceKey.create(Registries.VILLAGER_PROFESSION, registryId("lumberjack")); }
    public boolean customLadders() { return customLadders; }
    public boolean customBarrels() { return customBarrels; }
    public boolean customBookshelves() { return customBookshelves; }
    @Override public BooleanSupplier check() { return () -> Woodcutters.feature() != null && Woodcutters.feature().enabled(); }
}
