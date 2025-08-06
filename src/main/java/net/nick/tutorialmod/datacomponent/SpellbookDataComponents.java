package net.nick.tutorialmod.datacomponent;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.nick.tutorialmod.TutorialMod;

@Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpellbookDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, TutorialMod.MOD_ID);

    public static final RegistryObject<DataComponentType<String>> SELECTED_SPELL =
            DATA_COMPONENTS.register("selected_spell", () ->
                    DataComponentType.<String>builder()
                            .persistent(Codec.STRING)
                            .build()
            );

    public static void register() {
        DATA_COMPONENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
