package net.nick.tutorialmod.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.nick.tutorialmod.entity.custom.KusarigamaEntity;

public class KusarigamaItem extends Item {
    private static final int MAX_US_DURATION = 72000;
    private static final int COOLDOWN_TICKS = 20; // 1 second

    public KusarigamaItem(Properties properties) {
        super(properties
                .attributes(SwordItem.createAttributes(Tiers.DIAMOND,1,-2.4f))
                .stacksTo(1)
                .durability(384) // Same as iron sword
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Doesn't work if on cooldown
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            // Create and launch the chin whip entity
            KusarigamaEntity kusarigama = new KusarigamaEntity(level, player);
            // Shoots the new entity
            kusarigama.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f,2.0f,0.5f);
            level.addFreshEntity(kusarigama); // Add the entity

            // Play sound effect
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FISHING_BOBBER_THROW, SoundSource.PLAYERS,1.0f,1.0f);

            // Damage the item
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

            // Set cooldown
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
        return MAX_US_DURATION;
    }
}
