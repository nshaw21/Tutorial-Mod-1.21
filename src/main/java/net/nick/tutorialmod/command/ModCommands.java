package net.nick.tutorialmod.command;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.nick.tutorialmod.TutorialMod;
import net.nick.tutorialmod.command.player.HomeCommand;
import net.nick.tutorialmod.command.player.ResetCooldownCommand;
import net.nick.tutorialmod.command.player.SetHomeCommand;

@Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        SetHomeCommand.register(event.getDispatcher());
        HomeCommand.register(event.getDispatcher());
        ResetCooldownCommand.register(event.getDispatcher());
    }
}

