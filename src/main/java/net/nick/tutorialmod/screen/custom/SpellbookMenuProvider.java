package net.nick.tutorialmod.screen.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class SpellbookMenuProvider implements MenuProvider {
    private final ItemStack stack;

    public SpellbookMenuProvider(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Spellbook");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new SpellbookMenu(id, playerInventory, stack); // Pass buf or spell if needed
    }
}
