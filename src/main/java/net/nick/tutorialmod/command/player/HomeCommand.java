package net.nick.tutorialmod.command.player;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class HomeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("home")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(0))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException(); // Gets who is doing the command
                    UUID uuid = player.getUUID();

                    if (SetHomeCommand.HOME_POSITIONS.containsKey(uuid)) {
                        Vec3 homePos = SetHomeCommand.HOME_POSITIONS.get(uuid); // Set the homePos to the one we did in /sethome
                        player.teleportTo(homePos.x, homePos.y, homePos.z);
                        player.sendSystemMessage(Component.literal("Teleported home."));
                        return 1;
                    } else {
                        player.sendSystemMessage(Component.literal("No home set. Use /sethome first."));
                        return 0;
                    }
                }));
    }
}
