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

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, TutorialMod.MOD_ID);

    public static final RegistryObject<MenuType<PedestalMenu>> PEDESTAL_MENU =
            MENUS.register("pedestal_name", () -> IForgeMenuType.create(PedestalMenu::new));
    public static final RegistryObject<MenuType<SpellbookMenu>> SPELLBOOK_MENU =
            MENUS.register("spellbook_menu", () -> SpellbookMenu.TYPE);



    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
