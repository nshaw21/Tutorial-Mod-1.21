// First, create a network packet to sync spell selection
// Create this file: src/main/java/net/nick/tutorialmod/network/SpellSelectionPacket.java

package net.nick.tutorialmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nick.tutorialmod.TutorialMod;

public record SpellSelectionPacket(String spell) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SpellSelectionPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TutorialMod.MOD_ID, "spell_selection"));

    public SpellSelectionPacket(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.spell);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}