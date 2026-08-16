package svenhjol.charm.common;

import net.fabricmc.api.ModInitializer;
import svenhjol.charm.CharmMod;
import svenhjol.charm.common.features.wood.VanillaWoodVariants;
import svenhjol.charm.common.features.coral_squids.CoralSquids;
import svenhjol.charm.common.features.recipe_improvements.RecipeImprovements;
import svenhjol.charm.common.features.aerial_affinity.AerialAffinity;
import svenhjol.charm.common.features.animal_armor_enchanting.AnimalArmorEnchanting;
import svenhjol.charm.common.features.anvils_last_longer.AnvilsLastLonger;
import svenhjol.charm.common.features.firing.Firing;
import svenhjol.charm.common.features.kilns.Kilns;
import svenhjol.charm.common.features.copper_pistons.CopperPistons;
import svenhjol.charm.common.features.woodcutting.Woodcutting;
import svenhjol.charm.common.features.woodcutting.Woodcutters;
import svenhjol.charm.common.features.lumberjacks.Lumberjacks;
import svenhjol.charm.common.features.endermite_powder.EndermitePowder;
import svenhjol.charm.common.features.arcane_purpur.ArcanePurpur;
import svenhjol.charm.common.features.player_pressure_plates.PlayerPressurePlates;
import svenhjol.charm.common.features.doors_open_together.DoorsOpenTogether;
import svenhjol.charm.common.features.smooth_glowstone.SmoothGlowstone;
import svenhjol.charm.common.features.redstone_sand.RedstoneSand;
import svenhjol.charm.common.features.coral_sea_lanterns.CoralSeaLanterns;
import svenhjol.charm.common.features.potion_of_radiance.PotionOfRadiance;
import svenhjol.charm.common.features.silence.Silence;
import svenhjol.charm.common.features.note_blocks.NoteBlocks;
import svenhjol.charm.common.features.suspicious_effect_improvements.SuspiciousEffectImprovements;
import svenhjol.charm.common.features.raid_horns.RaidHorns;
import svenhjol.charm.common.features.beekeepers.Beekeepers;
import svenhjol.charm.common.features.item_hover_sorting.ItemHoverSorting;
import svenhjol.charmony.api.core.Side;

public final class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        svenhjol.charmony.core.common.CommonInitializer.init();

        var mod = CharmMod.instance();
        mod.addSidedFeature(VanillaWoodVariants.class);
        mod.addSidedFeature(CoralSquids.class);
        mod.addSidedFeature(RecipeImprovements.class);
        mod.addSidedFeature(AerialAffinity.class);
        mod.addSidedFeature(AnimalArmorEnchanting.class);
        mod.addSidedFeature(AnvilsLastLonger.class);
        mod.addSidedFeature(Firing.class);
        mod.addSidedFeature(Kilns.class);
        mod.addSidedFeature(CopperPistons.class);
        mod.addSidedFeature(Woodcutting.class);
        mod.addSidedFeature(Woodcutters.class);
        mod.addSidedFeature(Lumberjacks.class);
        mod.addSidedFeature(EndermitePowder.class);
        mod.addSidedFeature(ArcanePurpur.class);
        mod.addSidedFeature(PlayerPressurePlates.class);
        mod.addSidedFeature(DoorsOpenTogether.class);
        mod.addSidedFeature(SmoothGlowstone.class);
        mod.addSidedFeature(RedstoneSand.class);
        mod.addSidedFeature(CoralSeaLanterns.class);
        mod.addSidedFeature(PotionOfRadiance.class);
        mod.addSidedFeature(Silence.class);
        mod.addSidedFeature(NoteBlocks.class);
        mod.addSidedFeature(SuspiciousEffectImprovements.class);
        mod.addSidedFeature(RaidHorns.class);
        mod.addSidedFeature(Beekeepers.class);
        mod.addSidedFeature(ItemHoverSorting.class);
        mod.run(Side.Common);
    }
}
