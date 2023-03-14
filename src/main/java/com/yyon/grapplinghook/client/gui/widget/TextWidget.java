package com.yyon.grapplinghook.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TextWidget extends AbstractWidget {

    public TextWidget(int p_i232254_1_, int p_i232254_2_, int p_i232254_3_, int p_i232254_4_, Component p_i232254_5_) {
        super(p_i232254_1_, p_i232254_2_, p_i232254_3_, p_i232254_4_, p_i232254_5_);
    }

    public TextWidget(Component text, int x, int y) {
        this(x, y, 50, 15 * text.getString().split("\n").length + 5, text);
    }

    public void renderButton(PoseStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        RenderSystem.setShaderTexture(0,WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int j = this.getFGColor();
        int lineno = 0;
        for (String s : this.getMessage().getString().split("\n")) {
            drawString(p_230431_1_, fontrenderer, Component.literal(s), this.getX(), this.getY() + lineno*15, j | Mth.ceil(this.alpha * 255.0F) << 24);
            lineno++;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) { }
}
