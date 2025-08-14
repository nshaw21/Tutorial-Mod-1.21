package net.nick.tutorialmod.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.nick.tutorialmod.TutorialMod;
import net.nick.tutorialmod.entity.custom.KusarigamaEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class KusarigamaRenderer extends EntityRenderer<KusarigamaEntity> {
    private static final ResourceLocation KUSARIGAMA_TEXTURE = ResourceLocation.fromNamespaceAndPath(TutorialMod.MOD_ID, "textures/entity/kusarigama.png");
    private static final RenderType KUSARIGAMA_RENDER_TYPE = RenderType.entityCutout(KUSARIGAMA_TEXTURE);

    public KusarigamaRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(KusarigamaEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Entity owner = entity.getOwner();
        if (owner == null) {
            return;
        }

        poseStack.pushPose();

        // Get positions
        Vec3 ownerPos = owner.position().add(0, owner.getEyeHeight() * 0.8, 0);
        Vec3 whipPos = entity.position();

        // Calculate the chain direction and length
        Vec3 kusarigamaVector = whipPos.subtract(ownerPos);
        double kusarigamaLength = kusarigamaVector.length();

        if (kusarigamaLength > 0.1) {
            // Normalize direction
            kusarigamaVector = kusarigamaVector.normalize();

            // Rotate to face the direction
            float yaw = (float) (Mth.atan2(kusarigamaVector.x, kusarigamaVector.z) * 180.0 / Math.PI);
            float pitch = (float) (Mth.abs((float) kusarigamaVector.y) * 180.0 / Math.PI);

            poseStack.translate(ownerPos.x - entity.getX(), ownerPos.y - entity.getY(), ownerPos.z - entity.getZ());
            poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch));

            // Render the chain
            VertexConsumer vertexConsumer = buffer.getBuffer(KUSARIGAMA_RENDER_TYPE);
            Matrix4f matrix4f = poseStack.last().pose();
            Matrix3f matrix3f = poseStack.last().normal();

            // Draw chain segments
            int segments = Math.max(1, (int) (kusarigamaLength * 4)); // 4 segments per block
            float segmentLength = (float) kusarigamaLength / segments;

            for (int i = 0; i < segments; i++) {
                float z1 = i * segmentLength;
                float z2 = (i + 1) * segmentLength;

                // Simple quad for each segment
                drawChainSegment(vertexConsumer, matrix4f, matrix3f, z1, z2, packedLight);
            }

            // Render whip head (small cube)
            poseStack.translate(0, 0, kusarigamaLength);
            poseStack.scale(0.1f, 0.1f, 0.1f);
            drawWhipHead(vertexConsumer, matrix4f, matrix3f, packedLight);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void drawChainSegment(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float z1, float z2, int light) {
        float width = 0.03f;

        // Front face
        consumer.addVertex(pose, -width, -width, z1).setColor(64, 64, 64, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY);
        consumer.addVertex(pose, width, -width, z1).setColor(64, 64, 64, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY);
        consumer.addVertex(pose, width, width, z1).setColor(64, 64, 64, 255).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY);
        consumer.addVertex(pose, -width, width, z1).setColor(64, 64, 64, 255).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY);

        // Back face
        consumer.addVertex(pose, -width, width, z2).setColor(64, 64, 64, 255).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
        consumer.addVertex(pose, width, width, z2).setColor(64, 64, 64, 255).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY);
        consumer.addVertex(pose, width, -width, z2).setColor(64, 64, 64, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY);
        consumer.addVertex(pose, -width, -width, z2).setColor(64, 64, 64, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY);
    }

    private void drawWhipHead(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int light) {
        float size = 1.0f;

        // Simple cube for whip head - just front and back faces for simplicity
        consumer.addVertex(pose, -size, -size, -size).setColor(96, 64, 32, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY);
        consumer.addVertex(pose, size, -size, -size).setColor(96, 64, 32, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY);
        consumer.addVertex(pose, size, size, -size).setColor(96, 64, 32, 255).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY);
        consumer.addVertex(pose, -size, size, -size).setColor(96, 64, 32, 255).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
    }

    @Override
    public ResourceLocation getTextureLocation(KusarigamaEntity entity) {
        return KUSARIGAMA_TEXTURE;
    }
}
