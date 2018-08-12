package com.yyon.grapplinghook;

import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.items.multiBow;

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
		if (event.getType() == ElementType.CROSSHAIRS) {
			EntityPlayerSP player = this.mc.player;
			ItemStack bow = null;
			if ((player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) != null && player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() instanceof grappleBow)) {
				bow = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
			} else if ((player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND) != null && player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem() instanceof grappleBow)) {
				bow = player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
			}
			
			if (bow != null) {
//				float partialticks = event.getPartialTicks();
				ScaledResolution resolution = event.getResolution();
				
		        mc.entityRenderer.setupOverlayRendering();
		        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	//	        mc.getTextureManager().bindTexture(Gui.ICONS);
		        GlStateManager.enableBlend();
		        
				GameSettings gamesettings = this.mc.gameSettings;
	
		        if (gamesettings.thirdPersonView == 0)
		        {
		            if (this.mc.playerController.isSpectator() && this.mc.pointedEntity == null)
		            {
		            	return;
		            }
	
		            int w = resolution.getScaledWidth();
		            int h = resolution.getScaledHeight();
	
		            if (gamesettings.showDebugInfo && !gamesettings.hideGUI && !this.mc.player.hasReducedDebug() && !gamesettings.reducedDebugInfo)
		            {
		            }
		            else
		            {
		            	double fov = Math.toRadians(gamesettings.fovSetting);
		            	fov *= player.getFovModifier();
		            	
		            	double angle = Math.toRadians(((grappleBow) grapplemod.grapplebowitem).getAngle(player, bow));
		            	
		            	double l = ((double) h/2) / Math.tan(fov/2);
		            	
		            	int offset = (int) (Math.tan(angle) * l);
		            	
		                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		                GlStateManager.enableAlpha();
		                this.drawTexturedModalRect(w / 2 - 7 + offset, h / 2 - 7, 0, 0, 16, 16);
		                this.drawTexturedModalRect(w / 2 - 7 - offset, h / 2 - 7, 0, 0, 16, 16);
		            }
		        }
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
}
