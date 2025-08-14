package net.nick.tutorialmod.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.nick.tutorialmod.entity.ModEntityTypes;

public class ScorchedProjectileEntity extends AbstractArrow {
    public ScorchedProjectileEntity(EntityType<ScorchedProjectileEntity> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public ScorchedProjectileEntity(LivingEntity shooter, Level level) {
        super(ModEntityTypes.SCORCHED_PROJECTILE.get(), level);
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
    }

    @Override
    public void tick() {
        super.tick();

        // Spawn particles every tick for a consistent flame trail
        if (this.level().isClientSide) {
            // Main flame particles
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(
                        ParticleTypes.FLAME,
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.1,
                        this.getY() + (this.random.nextDouble() - 0.5) * 0.1,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.1,
                        0.0, 0.0, 0.0
                );
            }

            // Occasional smoke
            if (this.random.nextInt(4) == 0) {
                this.level().addParticle(
                        ParticleTypes.SMOKE,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        0.0, 0.01, 0.0
                );
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        entity.hurt(entity.damageSources().playerAttack(((Player) this.getOwner())),3);

        // Set the entity on fire
        entity.igniteForTicks(100);

        // Create a burst of flame particles on impact
        if (this.level().isClientSide) {
            for (int i = 0; i < 10; i++) {
                this.level().addParticle(
                        ParticleTypes.FLAME,
                        entity.getX() + (this.random.nextDouble() - 0.5) * 0.5,
                        entity.getY() + entity.getBbHeight() * 0.5 + (this.random.nextDouble() - 0.5) * 0.5,
                        entity.getZ() + (this.random.nextDouble() - 0.5) * 0.5,
                        (this.random.nextDouble() - 0.5) * 0.2,
                        this.random.nextDouble() * 0.2,
                        (this.random.nextDouble() - 0.5) * 0.2
                );
            }
        }

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        // Create flame particles when hitting a block
        if (this.level().isClientSide) {
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(
                        ParticleTypes.FLAME,
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.3,
                        this.getY() + (this.random.nextDouble() - 0.5) * 0.3,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.3,
                        (this.random.nextDouble() - 0.5) * 0.15,
                        this.random.nextDouble() * 0.15,
                        (this.random.nextDouble() - 0.5) * 0.15
                );
            }

            // Add some smoke particles
            for (int i = 0; i < 3; i++) {
                this.level().addParticle(
                        ParticleTypes.SMOKE,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        (this.random.nextDouble() - 0.5) * 0.1,
                        this.random.nextDouble() * 0.1,
                        (this.random.nextDouble() - 0.5) * 0.1
                );
            }
        }

        // Remove the projectile
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }
}
