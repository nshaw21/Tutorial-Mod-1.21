package net.nick.tutorialmod.screen.custom;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.nick.tutorialmod.datacomponent.SpellbookDataComponents;
import net.nick.tutorialmod.network.ModNetworking;
import net.nick.tutorialmod.network.SpellSelectionPacket;

import java.util.List;

public class SpellbookScreen extends AbstractContainerScreen<SpellbookMenu> {
//    private static final ResourceLocation TEXTURE =
//            ResourceLocation.fromNamespaceAndPath(TutorialMod.MOD_ID, "textures/gui/spellbook/spellbook_gui.png");
    private static final List<String> SPELLS = List.of("fireball", "smite", "teleport", "infinity");

    public SpellbookScreen(SpellbookMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        int buttonY = this.topPos + 20;
        int buttonX = this.leftPos + 10;

        for (String spell : SPELLS) { // For each spell
            addRenderableWidget( // Add a button for it
                    Button.builder(Component.literal(spell), pButton -> { // Make the buttons name the spells
                                // FIXED: Send packet to server to sync spell selection
                                if (minecraft != null && minecraft.player != null) {
                                    // Send network packet to server
                                    ModNetworking.sendToServer(new SpellSelectionPacket(spell));

                                    // Set on client side for immediate feedback
                                    ItemStack playerStack = minecraft.player.getMainHandItem();
                                    if (!playerStack.isEmpty()) {
                                        playerStack.set(SpellbookDataComponents.SELECTED_SPELL.get(), spell);
                                    }

                                    minecraft.player.displayClientMessage(Component.literal("Client: Selected spell: " + spell), true);
                                }
                                this.minecraft.player.closeContainer(); // Close the GUI when clicking the button
                            })
                            .pos(buttonX, buttonY) // Where to put the buttons
                            .size(120,20) // Button sizes
                            .build() // Builds the buttons
            );
            buttonY += 24; // Adds space in between each button
        }
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
//        RenderSystem.setShaderTexture(0, TEXTURE);
//        pGuiGraphics.blit(TEXTURE, leftPos, topPos,0,0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(font, title, 10,10,0x404040, false);

        // Show current selected spell
        if (menu.getStack() != ItemStack.EMPTY) {
            String currentSpell = menu.getStack().get(SpellbookDataComponents.SELECTED_SPELL.get());
            if (currentSpell != null && !currentSpell.isEmpty()) {
                pGuiGraphics.drawString(font, "Current: " + currentSpell, 10, 140, 0x404040, false);
            }
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
