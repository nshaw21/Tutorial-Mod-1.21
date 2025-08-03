package net.nick.tutorialmod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nick.tutorialmod.TutorialMod;
import net.nick.tutorialmod.item.custom.ChiselItem;
import net.nick.tutorialmod.item.custom.FuelItem;
import net.nick.tutorialmod.item.custom.ScorchedBladeItem;

import java.util.List;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = // Teling minecraft that we want to register this as an item
            DeferredRegister.create(ForgeRegistries.ITEMS, TutorialMod.MOD_ID);



    public static final RegistryObject<Item> ALEXANDRITE = ITEMS.register("alexandrite",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_ALEXANDRITE = ITEMS.register("raw_alexandrite",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> AURORA_ASHES = ITEMS.register("aurora_ashes",
            () -> new FuelItem(new Item.Properties(), 1200));

    // Foods
    public static final RegistryObject<Item> KOHLRABI = ITEMS.register("kohlrabi",
            () -> new Item(new Item.Properties().food(ModFoodProperties.KOHLRABI)) {
                @Override
                public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
                    pTooltipComponents.add(Component.translatable("tooltip.tutorialmod.kohlrabi"));

                    super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
                }
            });


    // Custom Special Items
    public static final RegistryObject<Item> CHISEL = ITEMS.register("chisel",
            () -> new ChiselItem(new Item.Properties().durability(32))); // Durability also means only 1 per stack
    public static final RegistryObject<Item> SCORCHED_BLADE = ITEMS.register("scorched_blade",
            () -> new ScorchedBladeItem(Tiers.DIAMOND, new Item.Properties().attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.4F)).durability(100)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }



}
