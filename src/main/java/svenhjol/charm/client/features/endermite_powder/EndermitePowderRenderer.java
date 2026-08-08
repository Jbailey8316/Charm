package svenhjol.charm.client.features.endermite_powder;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import svenhjol.charm.common.features.endermite_powder.common.EndermitePowderEntity;

public final class EndermitePowderRenderer extends EntityRenderer<EndermitePowderEntity, EntityRenderState> {
    public EndermitePowderRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
