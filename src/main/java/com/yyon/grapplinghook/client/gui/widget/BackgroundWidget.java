package com.yyon.grapplinghook.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yyon.grapplinghook.grapplemod;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BackgroundWidget extends AbstractWidget {

    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(
            grapplemod.MODID,
            "textures/gui/guimodifier_bg.png"
    );

    public BackgroundWidget(int p_i232254_1_, int p_i232254_2_, int p_i232254_3_, int p_i232254_4_, Component p_i232254_5_) {
        super(p_i232254_1_, p_i232254_2_, p_i232254_3_, p_i232254_4_, p_i232254_5_);
        this.active = false;
    }

    public BackgroundWidget(int x, int y, int w, int h) {
        this(x, y, w, h, Component.literal(""));
    }

    public void renderButton(PoseStack matrix, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
        RenderSystem.setShaderTexture(0, BG_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(matrix, this.getX(), this.getY(), 0, 0, this.width, this.height);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) { }
}
