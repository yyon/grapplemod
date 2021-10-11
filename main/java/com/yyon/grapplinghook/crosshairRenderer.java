package com.yyon.grapplinghook;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.yyon.grapplinghook.items.grappleBow;

import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class crosshairRenderer {
	public Minecraft mc;
	
	float zLevel = -90.0F;
	
	public crosshairRenderer() {
	    MinecraftForge.EVENT_BUS.register(this);
	    this.mc = Minecraft.getInstance();
	}
	
	@SubscribeEvent
	public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
		MatrixStack mStack = event.getMatrixStack();
		
		GameSettings gamesettings = this.mc.options;
        if (!gamesettings.getCameraType().isFirstPerson()) return;
        if (this.mc.player.isSpectator()) return;
        if (gamesettings.renderDebug && !gamesettings.hideGui && !this.mc.player.isReducedDebugInfo() && !gamesettings.reducedDebugInfo) return;

		if (event.getType() == ElementType.CROSSHAIRS) {
			ClientPlayerEntity player = this.mc.player;
			ItemStack bow = null;
			if ((player.getItemInHand(Hand.MAIN_HAND) != null && player.getItemInHand(Hand.MAIN_HAND).getItem() instanceof grappleBow)) {
				bow = player.getItemInHand(Hand.MAIN_HAND);
			} else if ((player.getItemInHand(Hand.OFF_HAND) != null && player.getItemInHand(Hand.OFF_HAND).getItem() instanceof grappleBow)) {
				bow = player.getItemInHand(Hand.OFF_HAND);
			}
			
			if (bow != null) {
				GrappleCustomization custom = ((grappleBow) grapplemod.grapplebowitem).getCustomization(bow);
            	double angle = Math.toRadians(custom.angle);//((grappleBow) grapplemod.grapplebowitem).getAngle(player, bow));
            	double verticalangle = Math.toRadians(custom.verticalthrowangle);
            	if (player.isCrouching()) {
            		angle = Math.toRadians(custom.sneakingangle);
            		verticalangle = Math.toRadians(custom.sneakingverticalthrowangle);
            	}
            	
            	if (!custom.doublehook) {
            		angle = 0;
            	}
            	
				MainWindow resolution = event.getWindow();
	            int w = resolution.getGuiScaledWidth();
	            int h = resolution.getGuiScaledHeight();

            	double fov = Math.toRadians(gamesettings.fov);
            	fov *= player.getFieldOfViewModifier();
            	double l = ((double) h/2) / Math.tan(fov/2);
            	
            	if (!((verticalangle == 0) && (!custom.doublehook || angle == 0))) {
	//				float partialticks = event.getPartialTicks();
					
            		/*
                    GlStateManager._clear(256, true);
                    GlStateManager._matrixMode(5889);
                    GlStateManager._loadIdentity();
                    GlStateManager._ortho(0.0D, w, h, 0.0D, 1000.0D, 3000.0D);
                    GlStateManager._matrixMode(5888);
                    GlStateManager._loadIdentity();
                    GlStateManager._translatef(0.0F, 0.0F, -2000.0F);
//			        mc.entityRenderer.setupOverlayRendering();
			        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		//	        mc.getTextureManager().bindTexture(Gui.ICONS);
			        RenderSystem.enableBlend();
			        */
			        
	            	int offset = (int) (Math.tan(angle) * l);
	            	int verticaloffset = (int) (-Math.tan(verticalangle) * l);
	            	
	            	drawCrosshair(mStack, w / 2 + offset, h / 2 + verticaloffset);
	                if (angle != 0) {
		            	drawCrosshair(mStack, w / 2 - offset, h / 2 + verticaloffset);
	                }

	                /*
	            	RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	            	RenderSystem.enableAlphaTest();
	                this.drawTexturedModalRect(w / 2 - 7 + offset, h / 2 - 7 + verticaloffset, 0, 0, 16, 16);
	                if (angle != 0) {
		                this.drawTexturedModalRect(w / 2 - 7 - offset, h / 2 - 7 + verticaloffset, 0, 0, 16, 16);
	                }
//	                */
		        }
            	
            	if (custom.rocket && custom.rocket_vertical_angle != 0) {
	            	int verticaloffset = (int) (-Math.tan(Math.toRadians(custom.rocket_vertical_angle)) * l);
	            	drawCrosshair(mStack, w / 2, h / 2 + verticaloffset);
//	            	RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//	            	RenderSystem.enableAlphaTest();
//	                this.drawTexturedModalRect(w / 2 - 7, h / 2 - 7 + verticaloffset, 0, 0, 16, 16);
            	}
			}

	    	double rocketFuel = ((ClientProxyClass) grapplemod.proxy).rocketFuel;
	
	    	if (rocketFuel < 1) {
				MainWindow resolution = event.getWindow();
	            int w = resolution.getGuiScaledWidth();
	            int h = resolution.getGuiScaledHeight();
	            
	    		int totalbarlength = w / 8;
	    		
		        RenderSystem.pushMatrix();
//		        mc.entityRenderer.setupOverlayRendering();
//		        GlStateManager.color(0.5F, 1.0F, 1.0F, 0.5F);
	//	        mc.getTextureManager().bindTexture(Gui.ICONS);
//		        GlStateManager.enableBlend();
//	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//	            GlStateManager.enableAlpha();

		        RenderSystem.disableLighting();
		        RenderSystem.disableDepthTest();
		        RenderSystem.disableTexture();
//				GlStateManager.enableBlend();
//				GlStateManager.enableAlpha();

				//	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				
	            this.drawRect(w / 2 - totalbarlength / 2, h * 3 / 4, totalbarlength, 2, 50, 100);
	            this.drawRect(w / 2 - totalbarlength / 2, h * 3 / 4, (int) (totalbarlength * rocketFuel), 2, 200, 255);
	            
//				GlStateManager.disableBlend();
//				GlStateManager.disableAlpha();
	            RenderSystem.enableTexture();
//				GlStateManager.enableLighting();
//				GlStateManager.enableDepth();

	            RenderSystem.popMatrix();
//	            GlStateManager.disableAlpha();
//	            GlStateManager.disableBlend();
	    	}
		}
	}
	
    private void drawCrosshair(MatrixStack mStack, int x, int y) {
//        RenderSystem.pushMatrix();
//        RenderSystem.translatef(x, y, (float)Minecraft.getInstance().gui.getBlitOffset());
//        ActiveRenderInfo activerenderinfo = Minecraft.getInstance().gameRenderer.getMainCamera();
//        RenderSystem.rotatef(activerenderinfo.getXRot(), -1.0F, 0.0F, 0.0F);
//        RenderSystem.rotatef(activerenderinfo.getYRot(), 0.0F, 1.0F, 0.0F);
//        RenderSystem.scalef(-1.0F, -1.0F, -1.0F);
//        RenderSystem.renderCrosshair(10);
//        RenderSystem.popMatrix();
//    	grapplemod.LOGGER.info("crosshair: " + Integer.toString(x) + ", " + Integer.toString(y));
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        int i = 15;
        Minecraft.getInstance().gui.blit(mStack, (int) (x - (15.0F/2)), (int) (y - (15.0F/2)), 0, 0, 15, 15);
        /*
        if (Minecraft.getInstance().options.attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
           float f = Minecraft.getInstance().player.getAttackStrengthScale(0.0F);
           boolean flag = false;
           if (Minecraft.getInstance().crosshairPickEntity != null && Minecraft.getInstance().crosshairPickEntity instanceof LivingEntity && f >= 1.0F) {
              flag = Minecraft.getInstance().player.getCurrentItemAttackStrengthDelay() > 5.0F;
              flag = flag & Minecraft.getInstance().crosshairPickEntity.isAlive();
           }

           int j = x - 7 + 16;
           int k = y - 8;
           if (flag) {
        	   Minecraft.getInstance().gui.blit(mStack, k, j, 68, 94, 16, 16);
           } else if (f < 1.0F) {
              int l = (int)(f * 17.0F);
              Minecraft.getInstance().gui.blit(mStack, k, j, 36, 94, 16, 4);
              Minecraft.getInstance().gui.blit(mStack, k, j, 52, 94, l, 4);
           }
        }
        */
	}

	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.vertex((double)(x + 0), (double)(y + height), (double)this.zLevel).uv(((float)(textureX + 0) * f), ((float)(textureY + height) * f1)).endVertex();
        bufferbuilder.vertex((double)(x + width), (double)(y + height), (double)this.zLevel).uv(((float)(textureX + width) * f), ((float)(textureY + height) * f1)).endVertex();
        bufferbuilder.vertex((double)(x + width), (double)(y + 0), (double)this.zLevel).uv(((float)(textureX + width) * f), ((float)(textureY + 0) * f1)).endVertex();
        bufferbuilder.vertex((double)(x + 0), (double)(y + 0), (double)this.zLevel).uv(((float)(textureX + 0) * f), ((float)(textureY + 0) * f1)).endVertex();
        tessellator.end();
    }
    public void drawRect(int x, int y, int width, int height, int g, int a)
    {
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        GL11.glLineWidth(4.0F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.vertex((double)(x + 0), (double)(y + height), (double)this.zLevel).color(g, g, g, a).endVertex();
        bufferbuilder.vertex((double)(x + width), (double)(y + height), (double)this.zLevel).color(g, g, g, a).endVertex();
        bufferbuilder.vertex((double)(x + width), (double)(y + 0), (double)this.zLevel).color(g, g, g, a).endVertex();
        bufferbuilder.vertex((double)(x + 0), (double)(y + 0), (double)this.zLevel).color(g, g, g, a).endVertex();
        tessellator.end();
    }
}
