package net.nick.tutorialmod.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.nick.tutorialmod.TutorialMod;
import net.nick.tutorialmod.entity.custom.ScorchedProjectileEntity;

public class ScorchedProjectileRenderer extends EntityRenderer<ScorchedProjectileEntity> {

    public ScorchedProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(ScorchedProjectileEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        // Spawn flame particles around the projectile
        if (entity.level().isClientSide) {
            RandomSource random = entity.getRandom();

            // Spawn multiple flame particles for a better effect
            for (int i = 0; i < 3; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 0.2;
                double offsetY = (random.nextDouble() - 0.5) * 0.2;
                double offsetZ = (random.nextDouble() - 0.5) * 0.2;

                entity.level().addParticle(
                        ParticleTypes.FLAME,
                        entity.getX() + offsetX,
                        entity.getY() + offsetY,
                        entity.getZ() + offsetZ,
                        0.0, 0.0, 0.0
                );
            }

            // Add some smoke particles for extra effect
            if (random.nextInt(3) == 0) {
                entity.level().addParticle(
                        ParticleTypes.SMOKE,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        0.0, 0.0, 0.0
                );
            }
        }
    }


    @Override
    public ResourceLocation getTextureLocation(ScorchedProjectileEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(TutorialMod.MOD_ID, "resources/assets/tutorialmod/textures/particle/flame.png");
    }
}
