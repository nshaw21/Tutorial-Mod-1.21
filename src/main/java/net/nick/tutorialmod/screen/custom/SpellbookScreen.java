package net.nick.tutorialmod.screen.custom;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.nick.tutorialmod.TutorialMod;

public class SpellbookScreen extends Screen {
    protected int imageWidth = 176;
    protected int imageHeight = 166;
    protected int leftPos;
    protected int topPos;

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TutorialMod.MOD_ID, "textures/gui/spellbook/spellbook_gui.png");

    public SpellbookScreen(ItemStack pTitle) {
        super(Component.literal("Spellbook"));
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (this.width - imageWidth) /2;
        topPos = (this.height - imageHeight) /2;

        // Add spell buttons here
        this.addRenderableWidget(Button.builder(Component.literal("Fireball Spell"), pButton -> {
            // Trigger fireball spell logic
            sendSelectedSpell("fireball");
            this.minecraft.setScreen(null); // Close screen
        }).bounds(leftPos + 10, topPos + 20, 120,20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Teleport Spell"), pButton -> {
            sendSelectedSpell("teleport");
            this.minecraft.setScreen(null); // Close screen
        }).bounds(leftPos + 10, topPos + 50, 120,20).build());
    }

    private void sendSelectedSpell(String spellId) {
        // Custom packet you'll create in step 3
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        pGuiGraphics.blit(TEXTURE, leftPos, topPos,0,0, imageWidth, imageHeight);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void renderBackground(GuiGraphics pGuiGraphics) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
