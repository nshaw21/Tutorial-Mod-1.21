package net.nick.tutorialmod.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nick.tutorialmod.TutorialMod;
import net.nick.tutorialmod.item.custom.ChiselItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = // Teling minecraft that we want to register this as an item
            DeferredRegister.create(ForgeRegistries.ITEMS, TutorialMod.MOD_ID);



    public static final RegistryObject<Item> ALEXANDRITE = ITEMS.register("alexandrite",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_ALEXANDRITE = ITEMS.register("raw_alexandrite",
            () -> new Item(new Item.Properties()));


    // Custom Special Items
    public static final RegistryObject<Item> CHISEL = ITEMS.register("chisel",
            () -> new ChiselItem(new Item.Properties().durability(32))); // Durability also means only 1 per stack


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }



}
