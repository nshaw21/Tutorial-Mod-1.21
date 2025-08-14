package net.nick.tutorialmod.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.nick.tutorialmod.entity.custom.ScorchedProjectileEntity;

public class ScorchedBladeItem extends SwordItem {
    public ScorchedBladeItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide) {
            entity.igniteForTicks(50); // sets the entity on fire
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ScorchedProjectileEntity scorchedProjectile = new ScorchedProjectileEntity(player, level);

        // Set arrow position
        scorchedProjectile.setPos(
        player.getX() + player.getLookAngle().x * 1.5,
        player.getEyeY() + player.getLookAngle().y * 1.5,
        player.getZ() + player.getLookAngle().z * 1.5
        );

        // Move the projectile
        scorchedProjectile.setDeltaMovement(player.getLookAngle().scale(2.0f));

        // Adds it in the game
        level.addFreshEntity(scorchedProjectile);

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
