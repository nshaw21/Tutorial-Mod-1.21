package net.nick.tutorialmod.command.player;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.nick.tutorialmod.data.HomeData;

import java.util.HashMap;
import java.util.UUID;

public class SetHomeCommand {
    // Stores player homes
    public final HashMap<UUID, Vec3> HOME_POSITIONS = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sethome")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(0)) // 0 - everyone
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    Vec3 currentPos = player.position(); // Get current 3D position (doubles, so more accurate than BlockPos)

                    HomeData homeData = HomeData.get(player.serverLevel());
                    homeData.setHome(player.getUUID(), currentPos);

                    player.sendSystemMessage(Component.literal("Home set at " + (int) currentPos.x + " " +
                            (int) currentPos.y + " " + (int) currentPos.z));
                    return 1; // This just means "the command ran successfully"
                }));
    }
}
