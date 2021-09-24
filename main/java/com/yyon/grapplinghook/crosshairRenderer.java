package com.yyon.grapplinghook;

import com.yyon.grapplinghook.items.grappleBow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class crosshairRenderer {
	public Minecraft mc;
	
	float zLevel = -90.0F;
	
	public crosshairRenderer() {
	    MinecraftForge.EVENT_BUS.register(this);
	    this.mc = Minecraft.getMinecraft();
	}
	
	@SubscribeEvent
	public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
		GameSettings gamesettings = this.mc.gameSettings;
        if (gamesettings.thirdPersonView != 0) return;
        if (this.mc.playerController.isSpectator() && this.mc.pointedEntity == null) return;
        if (gamesettings.showDebugInfo && !gamesettings.hideGUI && !this.mc.player.hasReducedDebug() && !gamesettings.reducedDebugInfo) return;

		if (event.getType() == ElementType.CROSSHAIRS) {
			EntityPlayerSP player = this.mc.player;
			ItemStack bow = null;
			if ((player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) != null && player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() instanceof grappleBow)) {
				bow = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
			} else if ((player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND) != null && player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem() instanceof grappleBow)) {
				bow = player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
			}
			
			if (bow != null) {
				GrappleCustomization custom = ((grappleBow) grapplemod.grapplebowitem).getCustomization(bow);
            	double angle = Math.toRadians(custom.angle);//((grappleBow) grapplemod.grapplebowitem).getAngle(player, bow));
            	double verticalangle = Math.toRadians(custom.verticalthrowangle);
            	if (player.isSneaking()) {
            		angle = Math.toRadians(custom.sneakingangle);
            		verticalangle = Math.toRadians(custom.sneakingverticalthrowangle);
            	}
            	
            	if (!custom.doublehook) {
            		angle = 0;
            	}
            	
				ScaledResolution resolution = event.getResolution();
	            int w = resolution.getScaledWidth();
	            int h = resolution.getScaledHeight();

            	double fov = Math.toRadians(gamesettings.fovSetting);
            	fov *= player.getFovModifier();
            	double l = ((double) h/2) / Math.tan(fov/2);
            	            	
            	if (!((verticalangle == 0) && (!custom.doublehook || angle == 0))) {
	//				float partialticks = event.getPartialTicks();
					
			        mc.entityRenderer.setupOverlayRendering();
			        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		//	        mc.getTextureManager().bindTexture(Gui.ICONS);
			        GlStateManager.enableBlend();
			        
	            	int offset = (int) (Math.tan(angle) * l);
	            	int verticaloffset = (int) (-Math.tan(verticalangle) * l);
	            	
	                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	                GlStateManager.enableAlpha();
	                this.drawTexturedModalRect(w / 2 - 7 + offset, h / 2 - 7 + verticaloffset, 0, 0, 16, 16);
	                if (angle != 0) {
		                this.drawTexturedModalRect(w / 2 - 7 - offset, h / 2 - 7 + verticaloffset, 0, 0, 16, 16);
	                }
		        }
            	
            	if (custom.rocket && custom.rocket_vertical_angle != 0) {
	            	int verticaloffset = (int) (-Math.tan(Math.toRadians(custom.rocket_vertical_angle)) * l);
	                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	                GlStateManager.enableAlpha();
	                this.drawTexturedModalRect(w / 2 - 7, h / 2 - 7 + verticaloffset, 0, 0, 16, 16);
            	}
			}
		}

		if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
	    	double rocketFuel = ((ClientProxyClass) grapplemod.proxy).rocketFuel;
	
	    	if (rocketFuel < 1) {
 				ScaledResolution resolution = event.getResolution();
				
	            int w = resolution.getScaledWidth();
	            int h = resolution.getScaledHeight();
	            
	    		int totalbarlength = w / 8;
	    		
		        GlStateManager.pushMatrix();
//		        mc.entityRenderer.setupOverlayRendering();
//		        GlStateManager.color(0.5F, 1.0F, 1.0F, 0.5F);
	//	        mc.getTextureManager().bindTexture(Gui.ICONS);
//		        GlStateManager.enableBlend();
//	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//	            GlStateManager.enableAlpha();

		        GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.disableTexture2D();
//				GlStateManager.enableBlend();
//				GlStateManager.enableAlpha();

				//	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				
	            this.drawRect(w / 2 - totalbarlength / 2, h * 3 / 4, totalbarlength, 2, 50, 100);
	            this.drawRect(w / 2 - totalbarlength / 2, h * 3 / 4, (int) (totalbarlength * rocketFuel), 2, 200, 255);
	            
//				GlStateManager.disableBlend();
//				GlStateManager.disableAlpha();
				GlStateManager.enableTexture2D();
//				GlStateManager.enableLighting();
//				GlStateManager.enableDepth();

		        GlStateManager.popMatrix();
//	            GlStateManager.disableAlpha();
//	            GlStateManager.disableBlend();
	    	}
		}
	}
	
    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        tessellator.draw();
    }
    public void drawRect(int x, int y, int width, int height, int g, int a)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).color(g, g, g, a).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).color(g, g, g, a).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).color(g, g, g, a).endVertex();
        vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).color(g, g, g, a).endVertex();
        tessellator.draw();
    }
}
