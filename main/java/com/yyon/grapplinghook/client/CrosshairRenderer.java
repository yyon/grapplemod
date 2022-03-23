package com.yyon.grapplinghook.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class CrosshairRenderer {
	public Minecraft mc;
	
	float zLevel = -90.0F;
	
	public CrosshairRenderer() {
	    MinecraftForge.EVENT_BUS.register(this);
	    this.mc = Minecraft.getInstance();
	}
	
	@SubscribeEvent
	public void onRenderGameOverlayPost(RenderGameOverlayEvent.PostLayer event) {
		PoseStack mStack = event.getMatrixStack();
		
		Options gamesettings = this.mc.options;
        if (!gamesettings.getCameraType().isFirstPerson()) return;
        if (this.mc.player.isSpectator()) return;
        if (gamesettings.renderDebug && !gamesettings.hideGui && !this.mc.player.isReducedDebugInfo() && !gamesettings.reducedDebugInfo) return;

		if (event.getOverlay() == ForgeIngameGui.CROSSHAIR_ELEMENT) {
			LocalPlayer player = this.mc.player;
			ItemStack grapplehookItemStack = null;
			if ((player.getItemInHand(InteractionHand.MAIN_HAND) != null && player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof GrapplehookItem)) {
				grapplehookItemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
			} else if ((player.getItemInHand(InteractionHand.OFF_HAND) != null && player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof GrapplehookItem)) {
				grapplehookItemStack = player.getItemInHand(InteractionHand.OFF_HAND);
			}
			
			if (grapplehookItemStack != null) {
				GrappleCustomization custom = ((GrapplehookItem) CommonSetup.grapplingHookItem).getCustomization(grapplehookItemStack);
            	double angle = Math.toRadians(custom.angle);
            	double verticalangle = Math.toRadians(custom.verticalthrowangle);
            	if (player.isCrouching()) {
            		angle = Math.toRadians(custom.sneakingangle);
            		verticalangle = Math.toRadians(custom.sneakingverticalthrowangle);
            	}
            	
            	if (!custom.doublehook) {
            		angle = 0;
            	}
            	
				Window resolution = event.getWindow();
	            int w = resolution.getGuiScaledWidth();
	            int h = resolution.getGuiScaledHeight();

            	double fov = Math.toRadians(gamesettings.fov);
            	fov *= player.getFieldOfViewModifier();
            	double l = ((double) h/2) / Math.tan(fov/2);
            	
            	if (!((verticalangle == 0) && (!custom.doublehook || angle == 0))) {
	            	int offset = (int) (Math.tan(angle) * l);
	            	int verticaloffset = (int) (-Math.tan(verticalangle) * l);
	            	
	            	drawCrosshair(mStack, w / 2 + offset, h / 2 + verticaloffset);
	                if (angle != 0) {
		            	drawCrosshair(mStack, w / 2 - offset, h / 2 + verticaloffset);
	                }
		        }
            	
            	if (custom.rocket && custom.rocket_vertical_angle != 0) {
	            	int verticaloffset = (int) (-Math.tan(Math.toRadians(custom.rocket_vertical_angle)) * l);
	            	drawCrosshair(mStack, w / 2, h / 2 + verticaloffset);
            	}
			}

	    	double rocketFuel = ClientControllerManager.instance.rocketFuel;
	
	    	if (rocketFuel < 1) {
				Window resolution = event.getWindow();
	            int w = resolution.getGuiScaledWidth();
	            int h = resolution.getGuiScaledHeight();
	            
	    		int totalbarlength = w / 8;
	    		
		        RenderSystem.getModelViewStack().pushPose();
//		        RenderSystem.disableDepthTest();
//		        RenderSystem.disableTexture();
		        
	            this.drawRect(w / 2 - totalbarlength / 2, h * 3 / 4, totalbarlength, 2, 50, 100);
	            this.drawRect(w / 2 - totalbarlength / 2, h * 3 / 4, (int) (totalbarlength * rocketFuel), 2, 200, 255);

//	            RenderSystem.enableTexture();
	            RenderSystem.getModelViewStack().popPose();
	    	}
		}
	}
	
    private void drawCrosshair(PoseStack mStack, int x, int y) {
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Minecraft.getInstance().gui.blit(mStack, (int) (x - (15.0F/2)), (int) (y - (15.0F/2)), 0, 0, 15, 15);
        RenderSystem.defaultBlendFunc();
	}

	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tesselator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex((double)(x + 0), (double)(y + height), (double)this.zLevel).uv(((float)(textureX + 0) * f), ((float)(textureY + height) * f1)).endVertex();
        bufferbuilder.vertex((double)(x + width), (double)(y + height), (double)this.zLevel).uv(((float)(textureX + width) * f), ((float)(textureY + height) * f1)).endVertex();
        bufferbuilder.vertex((double)(x + width), (double)(y + 0), (double)this.zLevel).uv(((float)(textureX + width) * f), ((float)(textureY + 0) * f1)).endVertex();
        bufferbuilder.vertex((double)(x + 0), (double)(y + 0), (double)this.zLevel).uv(((float)(textureX + 0) * f), ((float)(textureY + 0) * f1)).endVertex();
        tessellator.end();
    }
    public void drawRect(int x, int y, int width, int height, int g, int a)
    {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
//        GL11.glLineWidth(4.0F);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex((double)(x + 0), (double)(y + height), (double)this.zLevel).color(g, g, g, a).endVertex();
        bufferbuilder.vertex((double)(x + width), (double)(y + height), (double)this.zLevel).color(g, g, g, a).endVertex();
        bufferbuilder.vertex((double)(x + width), (double)(y + 0), (double)this.zLevel).color(g, g, g, a).endVertex();
        bufferbuilder.vertex((double)(x + 0), (double)(y + 0), (double)this.zLevel).color(g, g, g, a).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
    }
}
