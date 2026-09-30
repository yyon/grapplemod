package com.yyon.grapplemod.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.yyon.grapplemod.init.ItemInit;
import com.yyon.grapplemod.items.GrapplehookItem;
import com.yyon.grapplemod.utils.GrappleCustomization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class CrosshairRenderer {
	protected static final ResourceLocation CROSSHAIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair");
	public static CrosshairRenderer instance = null;
	public Minecraft mc;

	public CrosshairRenderer() {
		instance = this;
	    this.mc = Minecraft.getInstance();
	}

	public void onRenderCrosshair(GuiGraphics guiGraphics) {
		Options gamesettings = this.mc.options;
        if (!gamesettings.getCameraType().isFirstPerson()) return;
        if (this.mc.player == null || this.mc.player.isSpectator()) return;
        if (this.mc.getDebugOverlay().showDebugScreen() && !gamesettings.hideGui && !this.mc.player.isReducedDebugInfo() && !gamesettings.reducedDebugInfo().get()) return;

		LocalPlayer player = this.mc.player;
		ItemStack grapplehookItemStack = null;
		if ((player.getItemInHand(InteractionHand.MAIN_HAND) != null && player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof GrapplehookItem)) {
			grapplehookItemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
		} else if ((player.getItemInHand(InteractionHand.OFF_HAND) != null && player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof GrapplehookItem)) {
			grapplehookItemStack = player.getItemInHand(InteractionHand.OFF_HAND);
		}

		if (grapplehookItemStack != null) {
			GrappleCustomization custom = ItemInit.GRAPPLING_HOOK.get().getCustomization(grapplehookItemStack);
        	double angle = Math.toRadians(custom.angle);
        	double verticalangle = Math.toRadians(custom.verticalthrowangle);
        	if (player.isCrouching()) {
        		angle = Math.toRadians(custom.sneakingangle);
        		verticalangle = Math.toRadians(custom.sneakingverticalthrowangle);
        	}

        	if (!custom.doublehook) {
        		angle = 0;
        	}

			Window resolution = this.mc.getWindow();
            int w = resolution.getGuiScaledWidth();
            int h = resolution.getGuiScaledHeight();

        	double fov = Math.toRadians(gamesettings.fov().get());
        	fov *= player.getFieldOfViewModifier();
        	double l = ((double) h/2) / Math.tan(fov/2);

        	if (!((verticalangle == 0) && (!custom.doublehook || angle == 0))) {
            	int offset = (int) (Math.tan(angle) * l);
            	int verticaloffset = (int) (-Math.tan(verticalangle) * l);

            	drawCrosshair(guiGraphics, w / 2 + offset, h / 2 + verticaloffset);
                if (angle != 0) {
	            	drawCrosshair(guiGraphics, w / 2 - offset, h / 2 + verticaloffset);
                }
	        }

        	if (custom.rocket && custom.rocket_vertical_angle != 0) {
            	int verticaloffset = (int) (-Math.tan(Math.toRadians(custom.rocket_vertical_angle)) * l);
            	drawCrosshair(guiGraphics, w / 2, h / 2 + verticaloffset);
        	}
		}

    	double rocketFuel = ClientControllerManager.instance.rocketFuel;

    	if (rocketFuel < 1) {
			Window resolution = this.mc.getWindow();
            int w = resolution.getGuiScaledWidth();
            int h = resolution.getGuiScaledHeight();

    		int totalbarlength = w / 8;

            this.drawRect(guiGraphics, w / 2 - totalbarlength / 2, h * 3 / 4, totalbarlength, 2, 50, 100);
            this.drawRect(guiGraphics, w / 2 - totalbarlength / 2, h * 3 / 4, (int) (totalbarlength * rocketFuel), 2, 200, 255);
    	}
	}

    private void drawCrosshair(GuiGraphics guiGraphics, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		guiGraphics.blitSprite(CROSSHAIR_SPRITE, (int) (x - (15.0F/2)), (int) (y - (15.0F/2)), 15, 15);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
	}

    public void drawRect(GuiGraphics guiGraphics, int x, int y, int width, int height, int g, int a) {
        guiGraphics.fill(x, y, x + width, y + height, (a << 24) | (g << 16) | (g << 8) | g);
    }
}
