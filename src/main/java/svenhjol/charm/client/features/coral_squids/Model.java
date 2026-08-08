package svenhjol.charm.client.features.coral_squids;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import svenhjol.charm.client.features.coral_squids.CoralSquidRenderState;

import java.util.Arrays;

public final class Model extends EntityModel<CoralSquidRenderState> {
    private final ModelPart[] tentacles = new ModelPart[8];
    private final ModelPart root;

    public Model(ModelPart root) { super(root); this.root = root; Arrays.setAll(tentacles, i -> root.getChild("tentacle" + i)); }

    public static LayerDefinition createBodyLayer() {
        var mesh = new MeshDefinition();
        var root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3, -4, -3, 6, 8, 6), PartPose.offset(0, 16, 0));
        var tentacle = CubeListBuilder.create().texOffs(48, 0).addBox(-0.5F, 0, -0.5F, 1, 7, 1);
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI * 2 / 8;
            float x = (float)Math.cos(angle) * 2.5F;
            float z = (float)Math.sin(angle) * 2.5F;
            double rotation = i * -Math.PI * 2 / 8 + Math.PI / 2;
            root.addOrReplaceChild("tentacle" + i, tentacle, PartPose.offsetAndRotation(x, 20, z, 0, (float)rotation, 0));
        }
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override public void setupAnim(CoralSquidRenderState state) {
        for (var tentacle : tentacles) tentacle.xRot = state.tentacleAngle;
    }
}
