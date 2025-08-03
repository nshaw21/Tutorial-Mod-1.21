package net.nick.tutorialmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nick.tutorialmod.TutorialMod;
import net.nick.tutorialmod.entity.custom.ScorchedProjectileEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TutorialMod.MOD_ID);

    public static final RegistryObject<EntityType<ScorchedProjectileEntity>> SCORCHED_PROJECTILE =
            ENTITY_TYPES.register("scorched_projectile", () -> EntityType.Builder.<ScorchedProjectileEntity>of(ScorchedProjectileEntity::new, MobCategory.MISC)
                    .sized(0.5f,1.5f)
                    .build("scorched_projectile"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
