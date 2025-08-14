package net.nick.tutorialmod.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.nick.tutorialmod.entity.ModEntityTypes;
import net.nick.tutorialmod.item.ModItems;

public class KusarigamaEntity extends AbstractArrow {
    private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(KusarigamaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final double MAX_RANGE = 12.0; // Maximum whip range
    private static final float DAMAGE = 6.0F; // Base damage
    private static final double PULL_FORCE = 0.8; // How strong the pull is

    private int lifeTime = 0;
    private boolean hasHitEntity = false;
    private Entity hitTarget = null;

    public KusarigamaEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        this.setBaseDamage(DAMAGE);
    }

    public KusarigamaEntity(Level level, LivingEntity shooter) {
        super(ModEntityTypes.KUSARIGAMA.get(), shooter, level, ItemStack.EMPTY, null);
        this.setBaseDamage(DAMAGE);
        this.setOwner(shooter);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        this.entityData.set(RETURNING, false);
    }

    @Override
    public void tick() {
        super.tick();
        lifeTime++;

        // Create particle trail
        if (this.level().isClientSide()) {
            this.level().addParticle(ParticleTypes.SMOKE,
                    this.getX(), this.getY(), this.getZ(),0.0,0.0,0.0);
        }

        Entity owner = this.getOwner();
        if (owner == null) {
            this.discard();
            return;
        }

        double distanceToOwner = this.distanceTo(owner);

        // Start returning if we've gone too far or hit something
        if (!this.isReturning() && (distanceToOwner > MAX_RANGE || hasHitEntity || lifeTime > 60)) {
            this.setReturning(true);
        }

        // Handle return movement
        if (this.isReturning()) {
            Vec3 ownerPos = owner.position().add(0, owner.getEyeHeight() * 0.5,0);
            Vec3 currentPos = this.position();
            Vec3 direction = ownerPos.subtract(currentPos).normalize();

            // Move towards owner
            this.setDeltaMovement(direction.scale(1.5));

            // Pull hit entity along i we have one
            if (hitTarget != null && hitTarget.isAlive() && hitTarget instanceof LivingEntity living) {
                Vec3 pullDirection = ownerPos.subtract(hitTarget.position().normalize());
                Vec3 pullVelocity = pullDirection.scale(PULL_FORCE);

                // Don't pull bosses or players in creative/spectator
                if (!living.isDeadOrDying() && canPullEntity(living)) {
                    hitTarget.setDeltaMovement(hitTarget.getDeltaMovement().add(pullVelocity));

                    // Add particles around pulled entity
                    if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.FLAME,
                                hitTarget.getX(), hitTarget.getY() + hitTarget.getBbHeight() * 0.5, hitTarget.getZ(),
                                5, 0.3, 0.3, 0.3, 0.0);
                    }
                }
            }

            // Remove when close to owner
            if (distanceToOwner < 1.5) {
                this.discard();
            }
        }

        // Remove if too old
        if (lifeTime > 200) {
            this.discard();
        }
    }

    private boolean canPullEntity(LivingEntity entity) {
        if (entity instanceof Player player) {
            return !player.isCreative() && !player.isSpectator();
        }

        // Don't pull bosses or very large entities
        return entity.getBbWidth() < 3.0f && entity.getBbHeight() < 3.0f &&
                !entity.getType().getCategory().isFriendly() || entity.getHealth() < 100;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity hitEntity = result.getEntity();
        Entity owner = this.getOwner();

        if (hitEntity == owner) {
            return; // Don't hit the owner
        }

        if (!this.level().isClientSide()) {
            // Deal damage
            DamageSource damageSource = this.damageSources().thrown(this, owner);
            if (hitEntity.hurt(damageSource, (float) this.getBaseDamage())) {
                // Play hit sound
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.2F);

                // Mark that we hit something and store the target
                this.hasHitEntity = true;
                if (hitEntity instanceof LivingEntity) {
                    this.hitTarget = hitEntity;
                }

                // Start returning immediately
                this.setReturning(true);
            }
        }
    }

    public boolean isReturning() {
        return this.entityData.get(RETURNING);
    }

    public void setReturning(boolean returning) {
        this.entityData.set(RETURNING, returning);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.KUSARIGAMA.get());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Returning", this.isReturning());
        compound.putInt("LifeTime", this.lifeTime);
        compound.putBoolean("HasHitEntity", this.hasHitEntity);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setReturning(compound.getBoolean("Returning"));
        this.lifeTime = compound.getInt("LifeTime");
        this.hasHitEntity = compound.getBoolean("HasHitEntity");
    }

    @Override
    protected boolean tryPickup(Player player) {
        // Only allow pickup by owner
        return this.getOwner() != null && this.getOwner().getUUID().equals(player.getUUID());
    }
}
