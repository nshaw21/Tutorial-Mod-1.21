package net.nick.tutorialmod.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = "tutorialmod")
public class SoulHarvesterItem extends Item {
    public SoulHarvesterItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            int souls = getSoulCount(stack);
            player.sendSystemMessage(Component.literal("Souls collected: " + souls).withStyle(ChatFormatting.DARK_PURPLE));
        }
        return InteractionResultHolder.success(stack);
    }

    // Tooltip
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int souls = getSoulCount(stack);

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.literal("Souls: " + souls).withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.literal("Right-click to view soul count.").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("Kill mobs while holding to collect souls.").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.literal("Hold SHIFT for more info").withStyle(ChatFormatting.YELLOW));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    // Logic for when a mob dies
    @SubscribeEvent // Turn this class into an event class
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) { //If the thing that killed the entity is the player
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();

            // Check if player has Soul Harvester in either hand
            if (mainHand.getItem() instanceof SoulHarvesterItem) {
                addSoul(mainHand, player);
            } else if (offHand.getItem() instanceof SoulHarvesterItem) {
                addSoul(offHand, player);
            }
        }
    }

    // Add souls when killed with SoulHarvester
    private static void addSoul(ItemStack stack, Player player) {
        int currentSouls = getSoulCount(stack);
        setSoulCount(stack, currentSouls + 1);
    }

    // Particles (optional)
    private static void spawnSoulParticles(Player player) {
        for (int i = 0; i < 5; i++) {
            double offsetX = (player.getRandom().nextDouble() - 0.5) * 2.0;
            double offsetY = player.getRandom().nextDouble() * 2.0;
            double offsetZ = (player.getRandom().nextDouble() - 0.5) * 2.0;

            player.level().addParticle(ParticleTypes.SOUL,
                    player.getX() + offsetX,
                    player.getY() + 1.0 + offsetY,
                    player.getZ() + offsetZ,
                    0.0, 0.1, 0.0
            );
        }
    }

    private static int getSoulCount(ItemStack stack) {
        var customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.copyTag().getInt("souls");
    }

    private static void setSoulCount(ItemStack stack, int souls) {
        var currentData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        var newTag = currentData.copyTag();
        newTag.putInt("souls", souls);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(newTag));
    }
}
