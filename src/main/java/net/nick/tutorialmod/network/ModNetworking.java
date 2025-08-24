package net.nick.tutorialmod.network;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import net.nick.tutorialmod.TutorialMod;
import net.nick.tutorialmod.datacomponent.SpellbookDataComponents;
import net.nick.tutorialmod.datacomponent.SummoningStaffDataComponents;
import net.nick.tutorialmod.item.custom.SummoningStaffItem;

public class ModNetworking {

    public static final SimpleChannel INSTANCE = ChannelBuilder
            .named(TutorialMod.MOD_ID + ":main")
            .networkProtocolVersion(1)
            .clientAcceptedVersions(Channel.VersionTest.exact(1))
            .serverAcceptedVersions(Channel.VersionTest.exact(1))
            .simpleChannel();

    public static void register() {
        INSTANCE.messageBuilder(SpellSelectionPacket.class)
                .decoder(SpellSelectionPacket::new)
                .encoder(SpellSelectionPacket::write)
                .consumerMainThread(ModNetworking::handleSpellSelection)
                .add();

        INSTANCE.messageBuilder(CreatureSelectionPacket.class)
                .decoder(CreatureSelectionPacket::new)
                .encoder(CreatureSelectionPacket::write)
                .consumerMainThread(ModNetworking::handleCreatureSelection)
                .add();
    }

    public static void handleSpellSelection(SpellSelectionPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (!stack.isEmpty()) {
                    // Set the spell on the server side
                    stack.set(SpellbookDataComponents.SELECTED_SPELL.get(), packet.spell());
                    player.displayClientMessage(Component.literal("Server: Selected " + packet.spell()), true);
                }
            }
        });
        context.setPacketHandled(true);
    }

    public static void handleCreatureSelection(CreatureSelectionPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof SummoningStaffItem) {
                    // Ensure the staff has proper data structure
                    SummoningStaffItem.initializeStaffData(stack);
                    
                    // Set the selected creature
                    stack.set(SummoningStaffDataComponents.SELECTED_CREATURE.get(), packet.selectedCreature());
                    
                    // Send confirmation message
                    player.displayClientMessage(
                            Component.literal("Server: Selected creature: " + packet.selectedCreature()), true);
                }
            }
        });
        context.setPacketHandled(true);
    }

    public static void sendToServer(SpellSelectionPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

    public static void sendToServer(CreatureSelectionPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }
}