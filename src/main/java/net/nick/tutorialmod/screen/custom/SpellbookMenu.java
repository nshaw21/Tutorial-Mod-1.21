package net.nick.tutorialmod.screen.custom;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.nick.tutorialmod.screen.ModMenuTypes;

public class SpellbookMenu extends AbstractContainerMenu {
    private final ItemStack stack;

    // Constructor for MenuProvider (with ItemStack)
    public SpellbookMenu(int id, Inventory inv, ItemStack stack) {
        super(ModMenuTypes.SPELLBOOK_MENU.get(), id);
        this.stack = stack;
    }

    // Constructor for MenuType registration (without ItemStack)
    public SpellbookMenu(int id, Inventory inv) {
        this(id, inv, ItemStack.EMPTY); // Default to empty stack, will be set by MenuProvider
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}