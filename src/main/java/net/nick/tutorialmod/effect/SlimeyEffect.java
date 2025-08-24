package net.nick.tutorialmod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

// Climbs up walls (like a spider)
public class SlimeyEffect extends MobEffect {
    public SlimeyEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.horizontalCollision) {
            Vec3 initialVec = pLivingEntity.getDeltaMovement(); // Movement that the entity has right now
            Vec3 climbVec = new Vec3(initialVec.x, 0.2D, initialVec.z); // Direction moving them up
            pLivingEntity.setDeltaMovement(climbVec.scale(0.97D));
            return true;
        }

        return super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    // This just makes sure the applyEffectTick is called every tick
    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }
}
