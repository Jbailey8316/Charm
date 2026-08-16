package svenhjol.charm.common.mixins.beekeepers;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Selects Charm's profession-specific Hero gift table for Beekeepers. */
@Mixin(GiveGiftToHero.class)
public abstract class GiveGiftToHeroMixin {
    private static final ResourceKey<LootTable> CHARM_BEEKEEPER_GIFT = ResourceKey.create(
        net.minecraft.core.registries.Registries.LOOT_TABLE,
        ResourceLocation.fromNamespaceAndPath("charm", "gameplay/hero_of_the_village/beekeeper_gift")
    );

    @Inject(method = "getLootTableToThrow", at = @At("HEAD"), cancellable = true)
    private static void charm$selectBeekeeperGift(Villager villager,
                                                   CallbackInfoReturnable<ResourceKey<LootTable>> cir) {
        var profession = villager.getVillagerData().profession();
        if (profession.is(svenhjol.charm.common.features.beekeepers.Beekeepers.feature().professionKey())) {
            cir.setReturnValue(CHARM_BEEKEEPER_GIFT);
        }
    }
}
