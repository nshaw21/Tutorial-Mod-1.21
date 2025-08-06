package net.nick.tutorialmod.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PocketAnvilItem extends Item {
    public PocketAnvilItem(Properties pProperties) {
        super(pProperties);
    }

    // Right-clicking just opens the GUI
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            // Only on server side:
            if (player instanceof ServerPlayer serverPlayer) {
                MenuProvider menuProvider = new SimpleMenuProvider(
                        (id, inventory, p) -> new AnvilMenu(id, inventory),
                        Component.literal("Pocket Anvil")
                );
                serverPlayer.openMenu(menuProvider);
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
