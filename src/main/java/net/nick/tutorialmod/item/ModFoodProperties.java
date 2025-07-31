package net.nick.tutorialmod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    // Saturation = how many hunger bars | Saturation = how long hunger bar will stay full (idk) | 20 ticks in a second (400 = 20 secs)
    public static final FoodProperties KOHLRABI = new FoodProperties.Builder().nutrition(3).saturationModifier(0.25f)
            .effect(new MobEffectInstance(MobEffects.INVISIBILITY, 400),0.20f).build();


}
