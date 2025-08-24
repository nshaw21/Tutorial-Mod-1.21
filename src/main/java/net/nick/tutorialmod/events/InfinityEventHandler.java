package net.nick.tutorialmod.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.nick.tutorialmod.TutorialMod;
import net.nick.tutorialmod.effect.ModEffects;

@Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InfinityEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            // Check if player has infinity effect
            if (player.hasEffect(ModEffects.INFINITY_EFFECT.getHolder().get())) {
                DamageSource damageSource = event.getSource();

                // Block projectile damage and some other ranged attacks
                if (isBlockedByInfinity(damageSource)) {
                    event.setCanceled(true);

                    // Optional: Add some feedback
                    // player.displayClientMessage(
                    //     Component.literal("Infinity protected you!"),
                    //     true
                    // );
                }
            }
        }
    }

    private static boolean isBlockedByInfinity(DamageSource damageSource) {
        // Block projectile damage
        if (damageSource.is(DamageTypes.ARROW) ||
                damageSource.is(DamageTypes.FIREBALL) ||
                damageSource.is(DamageTypes.THROWN) ||
                damageSource.is(DamageTypes.TRIDENT)) {
            return true;
        }

        // You can add more damage types to block here
        // For example, you might want to block explosion damage too:
        // if (damageSource.is(DamageTypes.EXPLOSION) ||
        //     damageSource.is(DamageTypes.PLAYER_EXPLOSION)) {
        //     return true;
        // }
        return true;
    }
}
