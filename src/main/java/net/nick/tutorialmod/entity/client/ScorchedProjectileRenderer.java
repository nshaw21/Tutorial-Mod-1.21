package net.nick.tutorialmod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.nick.tutorialmod.TutorialMod;
import net.nick.tutorialmod.entity.custom.ScorchedProjectileEntity;

public class ScorchedProjectileRenderer extends EntityRenderer<ScorchedProjectileEntity> {

    public ScorchedProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(ScorchedProjectileEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        // Do nothing = truly invisible
    }


    @Override
    public ResourceLocation getTextureLocation(ScorchedProjectileEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(TutorialMod.MOD_ID, "textures/particle/flame.png");
    }
}
