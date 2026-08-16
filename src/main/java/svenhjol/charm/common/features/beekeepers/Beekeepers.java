package svenhjol.charm.common.features.beekeepers;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

@FeatureDefinition(side = Side.Common, priority = 15, description = "Adds the Beekeeper villager profession.")
public final class Beekeepers extends SidedFeature {
    private static Beekeepers INSTANCE;
    public final Registerable<Holder<VillagerProfession>> profession;
    public final BeekeeperRegisters registers;

    public Beekeepers(Mod mod) {
        super(mod);
        INSTANCE = this;
        var professionKey = professionKey();
        profession = new Registerable<>(this, () -> {
            var beeHomeKey = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE,
                net.minecraft.resources.ResourceLocation.withDefaultNamespace("beehive"));
            var poi = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getOrThrow(beeHomeKey);
            Predicate<Holder<PoiType>> matches = holder -> holder.unwrapKey().equals(poi.unwrapKey());
            var result = Registry.registerForHolder(BuiltInRegistries.VILLAGER_PROFESSION, professionKey,
                new VillagerProfession(Component.translatable("entity.minecraft.villager.charm.beekeeper"),
                    matches, matches, ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_FARMER));
            VillagerTrades.TRADES.put(professionKey, new Int2ObjectOpenHashMap<>());
            return result;
        });
        registers = new BeekeeperRegisters(this);
    }

    public static Beekeepers feature() { return INSTANCE; }
    public ResourceKey<VillagerProfession> professionKey() { return ResourceKey.create(Registries.VILLAGER_PROFESSION, id("beekeeper")); }
    @Override public BooleanSupplier check() { return () -> true; }
}
