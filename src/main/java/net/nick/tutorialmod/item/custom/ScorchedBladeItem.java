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
        level.addParticle(ParticleTypes.FLAME, player.getX() + 1, player.getY() + 1.5, player.getZ() + 0.4,
                -1,0,0);



        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
