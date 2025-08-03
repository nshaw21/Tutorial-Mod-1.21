package net.nick.tutorialmod.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class TimeFreezingClockItem extends Item {
    private static final int COOLDOWN_TICKS = 20 * 30; // 30 seconds (20 ticks a second)

    public TimeFreezingClockItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack clock = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // Check cooldown
            if (!player.getCooldowns().isOnCooldown(this)) {
                //Freeze all nearby mobs (excluding players)
                double radius = 10.0; // Radius in blocks around player that we will check
                /*
                This line below does 3 things:

                LivingEntity.class: Only affects living creatures (like zombies, skeletons, animals, etc.).

                player.getBoundingBox().inflate(radius): Gets a cube-shaped area around the player.

                inflate(radius) expands the area in all directions.

                The entity -> ... part is a filter:

                entity != player: We don’t want to freeze the player.

                entity.isAlive(): No point freezing dead things.
                 */
                List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius),
                        entity -> entity != player && entity.isAlive());

                for (LivingEntity entity : nearbyEntities) {
                    // Apply Slowless V and Mining Fatique V for 5 seconds (amplifiers start at 0)
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,20 * 5,9));
                    entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,20 * 5,9));
                }

                // Start cooldown
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

                // Play a sound
                level.playSound(null, player.blockPosition(), SoundEvents.SNOW_BREAK, SoundSource.PLAYERS, 1.0f,0.5f);

            } else {
                player.displayClientMessage(Component.literal("Clock is recharging."), true);
            }
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
