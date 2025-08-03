package net.nick.tutorialmod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnderCompassItem extends Item {
    public EnderCompassItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // Basically saying this is the item they used, we can do stuff to it later if we want
        ItemStack stack = player.getItemInHand(hand);


        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            BlockPos playerPos = player.blockPosition(); // Get player coordinates

            // Find a nearby stronghold
            BlockPos strongholdPos = serverLevel.findNearestMapStructure(
                    StructureTags.EYE_OF_ENDER_LOCATED, // A tag for structures like strongholds
                    playerPos,                           // Starting position (player's position)
                    100,                                 // Search radius in chunks (100 chunks = ~1600 blocks)
                    false                                // Whether to skip unexplored areas (false = search everything)
            );

            // If it finds it
            if (strongholdPos != null) {
                // Get the coords to tp to in chat
                int x = strongholdPos.getX();
                int y = strongholdPos.getY();
                int z = strongholdPos.getZ();

                // Create a teleport command
                String command = "/tp @s " + x + " " + y + " " + z;

                // Build the chat component with a click event
                MutableComponent message = Component.literal("Stronghold at: ") // Starting part of message
                        .append(Component.literal(x + ", " + y + ", " + z) // append means to add (adding coords after)
                                .withStyle(Style.EMPTY
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)) // Letting you click it, and command is the tp one above
                                        .withUnderlined(true)) // Have it underlined cuz y not
                        );

                // Send the message
                player.sendSystemMessage(message);

                // Play a sound effect when found
                serverLevel.playSound(null, playerPos, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS, 1.0f, 10.f);
            } else {
                // Let the player know nothing was found
                player.sendSystemMessage(Component.translatable("No stronghold found nearby."));
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
