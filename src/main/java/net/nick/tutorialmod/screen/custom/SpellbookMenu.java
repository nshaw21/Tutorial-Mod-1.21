package net.nick.tutorialmod.screen.custom;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;

public class SpellbookMenu extends AbstractContainerMenu {
    public static final MenuType<SpellbookMenu> TYPE = IForgeMenuType.create(((windowId, inv, data) ->
            new SpellbookMenu(windowId, inv.player)));

    public SpellbookMenu(int id, Player player) {
        super(TYPE, id);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
