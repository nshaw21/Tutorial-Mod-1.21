package net.nick.tutorialmod.command.player;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;

public class ResetCooldownCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("resetcooldown")
                .requires(source -> source.hasPermission(0)) // permission 0 = all players
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    boolean reset = false;

                    // Check both hands
                    for (InteractionHand hand : InteractionHand.values()) {
                        Item item = player.getItemInHand(hand).getItem();
                        if (player.getCooldowns().isOnCooldown(item)) {
                            player.getCooldowns().removeCooldown(item);
                            reset = true;
                        }
                    }

                    if (reset) {
                        player.sendSystemMessage(Component.literal("Cooldown has been reset."));
                    } else {
                        player.sendSystemMessage(Component.literal("No cooldown to reset."));
                    }

                    return 1;
                }));
    }
}
