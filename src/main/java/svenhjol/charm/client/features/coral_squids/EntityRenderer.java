package svenhjol.charm.client.features.coral_squids;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import svenhjol.charm.common.features.coral_squids.common.CoralSquid;

public final class EntityRenderer extends MobRenderer<CoralSquid, CoralSquidRenderState, Model> {
    public EntityRenderer(EntityRendererProvider.Context context) { super(context, new Model(context.bakeLayer(CoralSquids.feature().registers.layer)), 0.7F); }
    @Override public CoralSquidRenderState createRenderState() { return new CoralSquidRenderState(); }
    @Override public void extractRenderState(CoralSquid squid, CoralSquidRenderState state, float partialTick) { super.extractRenderState(squid, state, partialTick); state.variant = squid.getVariant(); state.tentacleAngle = Mth.lerp(partialTick, squid.oldTentacleAngle, squid.tentacleAngle); state.xBodyRot = Mth.lerp(partialTick, squid.xBodyRot0, squid.xBodyRot); state.zBodyRot = Mth.lerp(partialTick, squid.zBodyRot0, squid.zBodyRot); }
    @Override public ResourceLocation getTextureLocation(CoralSquidRenderState state) { return state.variant.getTexture(); }
    @Override protected void setupRotations(CoralSquidRenderState state, PoseStack pose, float yaw, float partialTick) { pose.translate(0, 0.25, 0); pose.mulPose(Axis.YP.rotationDegrees(180 - yaw)); pose.mulPose(Axis.XP.rotationDegrees(state.xBodyRot)); pose.mulPose(Axis.YP.rotationDegrees(state.zBodyRot)); pose.translate(0, -0.6, 0); }
}
