package io.github.flemmli97.improvedmobs.client;

import io.github.flemmli97.improvedmobs.ImprovedMobs;
import io.github.flemmli97.improvedmobs.common.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.Locale;

public class ClientEvents {

    private static double clientDifficulty;
    private static final Identifier BAR = ImprovedMobs.modRes("textures/gui/difficulty_bar.png");

    public static void displayDifficulty(GuiGraphicsExtractor graphics) {
        if (!Config.ClientConfig.showDifficultyServerSync || !Config.ClientConfig.showDifficulty || Minecraft.getInstance().getDebugOverlay().showDebugScreen())
            return;
        graphics.pose().pushMatrix();
        Font font = Minecraft.getInstance().font;
        MutableComponent txt = Component.translatable("improvedmobs.overlay.difficulty", String.format(Locale.US, "%.1f", clientDifficulty)).withStyle(Config.ClientConfig.color);
        float scale = Config.ClientConfig.scale;
        graphics.pose().scale(scale, scale);
        int width = font.width(txt);
        int x = Config.ClientConfig.guiX;
        int y = Config.ClientConfig.guiY;
        switch (Config.ClientConfig.location) {
            case TOPRIGHT ->
                    x = Minecraft.getInstance().getWindow().getGuiScaledWidth() - 7 - width - Config.ClientConfig.guiX;
            case BOTTOMRIGHT -> {
                x = Minecraft.getInstance().getWindow().getGuiScaledWidth() - 7 - width - Config.ClientConfig.guiX;
                y = Minecraft.getInstance().getWindow().getGuiScaledHeight() - 17 - Config.ClientConfig.guiY;
            }
            case BOTTOMLEFT ->
                    y = Minecraft.getInstance().getWindow().getGuiScaledHeight() - 17 - Config.ClientConfig.guiY;
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, BAR, x, y, 0, 0, 4 + width, 17, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BAR, x + 4 + width, y, 183, 0, 3, 17, 256, 256);
        graphics.text(font, Component.translatable("improvedmobs.overlay.difficulty", String.format(java.util.Locale.US, "%.1f", clientDifficulty)).withStyle(Config.ClientConfig.color), x + 4, y + 5, 0xFF000000, false);
        graphics.pose().popMatrix();
    }

    public static void updateClientDifficulty(double difficulty) {
        clientDifficulty = difficulty;
    }
}
