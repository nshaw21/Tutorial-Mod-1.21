package net.nick.tutorialmod.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.nick.tutorialmod.TutorialMod;
import net.nick.tutorialmod.entity.custom.KusarigamaEntity;
import net.nick.tutorialmod.entity.custom.ScorchedProjectileEntity;

import java.util.function.Supplier;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TutorialMod.MOD_ID);

    public static final Supplier<EntityType<KusarigamaEntity>> KUSARIGAMA = ENTITY_TYPES.register("kusarigama",
            () -> EntityType.Builder.<KusarigamaEntity>of(KusarigamaEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("kusarigama"));

    public static final RegistryObject<EntityType<ScorchedProjectileEntity>> SCORCHED_PROJECTILE =
            ENTITY_TYPES.register("scorched_projectile", () -> EntityType.Builder.<ScorchedProjectileEntity>of(ScorchedProjectileEntity::new, MobCategory.MISC)
                    .sized(0.25f,0.25f)
                    .build("scorched_projectile"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
