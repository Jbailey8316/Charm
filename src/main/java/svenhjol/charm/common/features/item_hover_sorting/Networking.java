package svenhjol.charm.common.features.item_hover_sorting;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import svenhjol.charmony.core.Charmony;
public final class Networking {
    public record C2SSort(int slot, int direction) implements CustomPacketPayload {
        public static final Type<C2SSort> TYPE = new Type<>(Charmony.id("item_hover_sort"));
        public static final StreamCodec<FriendlyByteBuf, C2SSort> CODEC = StreamCodec.of((b,p)->{b.writeInt(p.slot);b.writeInt(p.direction);}, b->new C2SSort(b.readInt(),b.readInt()));
        public static void send(int slot,int direction){ClientPlayNetworking.send(new C2SSort(slot,direction));}
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    private Networking() {}
}
