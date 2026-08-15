package svenhjol.charm.common.mixins.note_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.common.features.note_blocks.NoteBlocks;

@Mixin(NoteBlock.class)
public class NoteBlockMixin {
    @Inject(method = "triggerEvent", at = @At("HEAD"), cancellable = true)
    private void charm$amethystSound(BlockState state, Level level, BlockPos pos, int eventId, int note, CallbackInfoReturnable<Boolean> cir) {
        if (eventId == 0 && level.getBlockState(pos.below()).is(Blocks.AMETHYST_BLOCK)) {
            float pitch = (float)Math.pow(2.0D, (note - 12) / 12.0D);
            level.playSound(null, pos, NoteBlocks.feature().amethystSound.get(), SoundSource.RECORDS, 3.0F, pitch);
            cir.setReturnValue(true);
        }
    }
}
