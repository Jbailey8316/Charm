package svenhjol.charm.common.features.aerial_affinity;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.enchantment.Enchantment;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.common.CommonRegistry;

import java.util.function.Supplier;

@FeatureDefinition(side = Side.Common, description = "A boots enchantment that restores mining speed while airborne.")
public final class AerialAffinity extends SidedFeature {
    public final Supplier<Holder<Attribute>> attribute;
    public final ResourceKey<Enchantment> enchantment;

    public AerialAffinity(Mod mod) {
        super(mod);
        var registry = CommonRegistry.forFeature(this);
        // Aerial Affinity's data-driven enchantment lives in the Charm namespace.
        // CommonRegistry.registryId() is intentionally Charmony-scoped, so use the
        // feature's Charm id for both the resource key and its referenced attribute.
        enchantment = ResourceKey.create(net.minecraft.core.registries.Registries.ENCHANTMENT,
            id("aerial_affinity"));
        attribute = new Registerable<>(this, () -> Registry.registerForHolder(
            BuiltInRegistries.ATTRIBUTE,
            id("player.aerial_mining_speed"),
            new RangedAttribute("attribute.name.player.charm.aerial_mining_speed", 0.0, 0.0, 1.0)
                .setSyncable(true)));
        registry.entityAttribute(() -> EntityType.PLAYER, attribute);
    }

    public static AerialAffinity feature() {
        return Mod.getSidedFeature(AerialAffinity.class);
    }
}
