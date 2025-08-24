package net.nick.tutorialmod.effect;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class InfinityEffect extends MobEffect {
    private static final double INFINITY_RANGE = 4.0; // Range in blocks
    private static final double SLOW_FACTOR = 0.02; // How slow projectiles become

    protected InfinityEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide && pLivingEntity instanceof Player player) {
            handleInfinityEffect(player, pAmplifier);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }

    private void handleInfinityEffect(Player player, int amplifier) {
        Level level = player.level();
        Vec3 playerPos = player.position();

        // Create a bounding box around the player
        AABB searchBox = new AABB(
                playerPos.x - INFINITY_RANGE,
                playerPos.y - INFINITY_RANGE,
                playerPos.z - INFINITY_RANGE,
                playerPos.x + INFINITY_RANGE,
                playerPos.y + INFINITY_RANGE,
                playerPos.z + INFINITY_RANGE
        );

        // Find all entities in range
        List<Entity> nearbyEntities = level.getEntities(player, searchBox);

        for (Entity entity : nearbyEntities) {
            if (isProjectile(entity)) {
                handleProjectile(entity, player, level);
            }
        }

        // Add visual particles around the player
        spawnInfinityParticles(player, level);
    }

    // Checking for all types of projectiles
    private boolean isProjectile(Entity entity) {
        return entity instanceof Projectile ||
                entity instanceof AbstractArrow ||
                entity instanceof ThrowableProjectile;
    }

    private void handleProjectile(Entity projectile, Player player, Level level) {
        Vec3 playerPos = player.position().add(0, player.getEyeY() / 2,0);
        Vec3 projectilePos = projectile.position();

        double distance = playerPos.distanceTo(projectilePos);

        // If projectile is close enough, start slowing it down
        if (distance < INFINITY_RANGE) {
            Vec3 currentvelocity = projectile.getDeltaMovement();

            // Calculate how much to slow down based on distance  (closer = slower)
            double slowAmount = Math.max(SLOW_FACTOR, (distance / INFINITY_RANGE) * 0.1);

            // If very close (within 1.5 blocks), almost stop completely
            if (distance < 1.5) {
                slowAmount = 0.001;

                // Add particle effect at projectile location
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleTypes.ENCHANT,
                            projectilePos.x, projectilePos.y, projectilePos.z,
                            3,0.2,0.2,0.2,0.02
                    );
                }
            }

            // Apply the slowing effect
            Vec3 newVelocity = currentvelocity.scale(slowAmount);
            projectile.setDeltaMovement(newVelocity);

            // Optional: Add a slight repelling force if projectile gets too close
            if (distance <= 1.0) {
                Vec3 repelDirection = projectilePos.subtract(playerPos).normalize();
                Vec3 repelForce = repelDirection.scale(0.05);
                projectile.setDeltaMovement(projectile.getDeltaMovement().add(repelForce));
            }
        }
    }

    private void spawnInfinityParticles(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        // Only spawn particles every few ticks to avoid spam
        if (level.getGameTime() % 10 == 0) {
            Vec3 playerPos = player.position().add(0, player.getEyeHeight() / 2, 0);

            // Create a subtle particle effect around the player
            for (int i = 0; i < 8; i++) {
                double angle = (Math.PI * 2 * i) / 8;
                double radius = 2.5;

                double x = playerPos.x + Math.cos(angle) * radius;
                double z = playerPos.z + Math.sin(angle) * radius;
                double y = playerPos.y + (Math.random() - 0.5) * 2;

                serverLevel.sendParticles(
                        ParticleTypes.END_ROD,
                        x, y, z,
                        1,0.1,0.1,0.1,0.01
                );
            }
        }
    }
}
