package com.yyon.grapplinghook.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Optional;

public interface CustomTooltipHandler {

    default void displayTooltip(Screen context, PoseStack matrix, int mouseX, int mouseY) {
        String tooltipText = this.getTooltip().getString();
        ArrayList<Component> lines = new ArrayList<>();
        for (String line : tooltipText.split("\n")) {
            lines.add(Component.literal(line));
        }

        context.renderTooltip(matrix, lines, Optional.empty(), mouseX, mouseY);
    }

    Component getTooltip();
}
