package net.nick.tutorialmod.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.nick.tutorialmod.entity.ModEntities;
import net.nick.tutorialmod.item.ModItems;

import java.util.Objects;

public class ScorchedProjectileEntity extends AbstractArrow {
    public ScorchedProjectileEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ScorchedProjectileEntity(LivingEntity shooter, Level level) {
        super(ModEntities.SCORCHED_PROJECTILE.get(), level);
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
    }

    // ADDS FLAME PARTICLES
//    @Override
//    public void tick() {
//        super.tick();
//
//        if (this.level().isClientSide) {
//            for (int i = 0; i < 2; i++) {
//                // Get current velocity of projectile
//                double vx = this.getDeltaMovement().x;
//                double vy = this.getDeltaMovement().y;
//                double vz = this.getDeltaMovement().z;
//
//                // Spawn particle slightly behind projectile for a trailing effect
//                double px = this.getX() - vx * 0.1;
//                double py = this.getX() - vy * 0.1;
//                double pz = this.getX() - vz * 0.1;
//
//                // Spawn flame particle moving with projectile's velocity
//                this.level().addParticle(ParticleTypes.FLAME, px, py, pz, vx ,vy, vz);
//            }
//        }
//    }






    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        entity.hurt(entity.damageSources().playerAttack((Player) Objects.requireNonNull(this.getOwner())),4);

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }
}
