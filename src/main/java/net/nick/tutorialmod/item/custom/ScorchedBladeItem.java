package net.nick.tutorialmod.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.nick.tutorialmod.entity.custom.ScorchedProjectileEntity;

public class ScorchedBladeItem extends SwordItem {
    public ScorchedBladeItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    // Left click logic
    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide) {
            entity.igniteForTicks(50); // sets the entity on fire
        }

        return super.onLeftClickEntity(stack, player, entity);
    }

    // Right click logic
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // Look direction vector
        double dx = -Math.sin(Math.toRadians(player.getYRot()));
        double dz = Math.cos(Math.toRadians(player.getYRot()));
        double dy = -Math.sin(Math.toRadians(player.getXRot()));

        // Starting position (plus a little offset so it doesn't spawn inside head)
        double px = player.getX() + dx;
        double py = player.getEyeY();
        double pz = player.getZ() + dz;

        // Particle velocity (same as look direction, scaled)
        double speed = 0.2;
        level.addParticle(ParticleTypes.FLAME, px, py, pz, dx * speed, dy * speed, dz * speed);

        level.playSound(player, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);

        if (!level.isClientSide) {
            ScorchedProjectileEntity projectile = new ScorchedProjectileEntity(player, level);

            // Set position at player's eye
            projectile.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());

            // Shoot it forward like an arrow
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.0F, 1.0F);

            projectile.setOwner(player); // Optional but good for team checks or source tracking
            level.addFreshEntity(projectile);
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

}
