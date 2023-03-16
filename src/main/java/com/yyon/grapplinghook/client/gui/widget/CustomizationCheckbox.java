package com.yyon.grapplinghook.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yyon.grapplinghook.client.gui.GuiGrappleHookModifier;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

public class CustomizationCheckbox extends Checkbox implements CustomTooltipHandler {

    private final GuiGrappleHookModifier context;

    private final String option;
    private Component tooltipText;

    public CustomizationCheckbox(GuiGrappleHookModifier context, int x, int y, int w, int h, Component text, boolean val, String option, Component tooltip) {
        super(x, y, w, h, text, val);
        this.context = context;
        this.option = option;
        this.tooltipText = tooltip;
    }

    @Override
    public void onPress() {
        super.onPress();

        this.context.getCurrentCustomizations().setBoolean(option, this.selected());

        this.context.updateEnabled();
    }

    @Override
    public void renderWidget(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(matrix, mouseX, mouseY, partialTicks);
        if (this.isHovered) this.displayTooltip(this.context, matrix, mouseX, mouseY);
    }

    public Component getTooltip() {
        return this.tooltipText;
    }

    public void setTooltip(Component tooltipText) {
        this.tooltipText = tooltipText;
    }
}
