package net.nick.tutorialmod.screen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.nick.tutorialmod.TutorialMod;
import net.nick.tutorialmod.screen.custom.PedestalMenu;
import net.nick.tutorialmod.screen.custom.SpellbookMenu;
import net.nick.tutorialmod.screen.custom.SummoningStaffMenu;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, TutorialMod.MOD_ID);

    public static final RegistryObject<MenuType<PedestalMenu>> PEDESTAL_MENU =
            MENUS.register("pedestal_name", () -> IForgeMenuType.create(PedestalMenu::new));

    // Fixed: Use the simple constructor since we're passing ItemStack through MenuProvider
    public static final RegistryObject<MenuType<SpellbookMenu>> SPELLBOOK_MENU =
            MENUS.register("spellbook_menu", () -> IForgeMenuType.create(
                    (windowId, inv, data) -> {
                        // We don't need the data parameter since ItemStack comes through MenuProvider
                        // This will be handled in SpellbookMenuProvider.createMenu()
                        return new SpellbookMenu(windowId, inv, inv.player.getMainHandItem());
                    }
            ));

    public static final RegistryObject<MenuType<SummoningStaffMenu>> SUMMONING_STAFF_MENU =
            MENUS.register("summoning_staff_menu", () -> IForgeMenuType.create(
                    (windowId, inv, data) -> {
                        return new SummoningStaffMenu(windowId, inv, inv.player.getMainHandItem());
                    }
            ));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}