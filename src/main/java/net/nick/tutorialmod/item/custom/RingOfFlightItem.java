package net.nick.tutorialmod.item.custom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RingOfFlightItem extends Item {
    public RingOfFlightItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            ServerPlayer serverPlayer = ((ServerPlayer) player);
            boolean canFly = serverPlayer.getAbilities().mayfly;

            // Toggle Flight
            if (canFly) {
                serverPlayer.getAbilities().mayfly = false;
                serverPlayer.getAbilities().flying = false;
                serverPlayer.onUpdateAbilities();
            } else {
                serverPlayer.getAbilities().mayfly = true;
                serverPlayer.getAbilities().flying = true;
                serverPlayer.onUpdateAbilities();
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
