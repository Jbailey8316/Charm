package svenhjol.charm.common.features.woodcutting;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import svenhjol.charmony.core.Charmony;

import java.util.List;

public final class WoodcutterNetworking {
    private WoodcutterNetworking() {}
    public record S2CRecipes(int menuId, int generation, List<ResourceLocation> recipes) implements CustomPacketPayload {
        public static final Type<S2CRecipes> TYPE = new Type<>(Charmony.id("woodcutter_recipes"));
        public static final StreamCodec<FriendlyByteBuf, S2CRecipes> CODEC = StreamCodec.of(S2CRecipes::encode, S2CRecipes::decode);
        private static void encode(FriendlyByteBuf b, S2CRecipes p) { b.writeVarInt(p.menuId); b.writeVarInt(p.generation); b.writeCollection(p.recipes, FriendlyByteBuf::writeResourceLocation); }
        private static S2CRecipes decode(FriendlyByteBuf b) { int id=b.readVarInt(), gen=b.readVarInt(); List<ResourceLocation> ids=b.readList(FriendlyByteBuf::readResourceLocation); return new S2CRecipes(id,gen,List.copyOf(ids)); }
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
    public record C2SSelect(int menuId, int generation, ResourceLocation recipe) implements CustomPacketPayload {
        public static final Type<C2SSelect> TYPE = new Type<>(Charmony.id("woodcutter_select"));
        public static final StreamCodec<FriendlyByteBuf, C2SSelect> CODEC = StreamCodec.of(C2SSelect::encode, C2SSelect::decode);
        private static void encode(FriendlyByteBuf b, C2SSelect p) { b.writeVarInt(p.menuId); b.writeVarInt(p.generation); b.writeResourceLocation(p.recipe); }
        private static C2SSelect decode(FriendlyByteBuf b) { return new C2SSelect(b.readVarInt(),b.readVarInt(),b.readResourceLocation()); }
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
    public static void send(ServerPlayer player, WoodcutterMenu menu) { ServerPlayNetworking.send(player, new S2CRecipes(menu.containerId, menu.generation(), menu.recipeIds())); }
}
