package svenhjol.charm.common.features.note_blocks;

import net.minecraft.sounds.SoundEvent;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, description = "Adds an amethyst note-block instrument.")
public final class NoteBlocks extends SidedFeature {
    public final Registerable<SoundEvent> amethystSound;

    public NoteBlocks(Mod mod) {
        super(mod);
        amethystSound = new Registerable<>(this, () -> net.minecraft.core.Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT, id("amethyst"),
            SoundEvent.createVariableRangeEvent(id("amethyst"))));
    }

    public static NoteBlocks feature() { return Mod.getSidedFeature(NoteBlocks.class); }
}
