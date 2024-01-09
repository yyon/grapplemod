package com.yyon.grapplinghook;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

import com.yyon.grapplinghook.items.multiBow;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class crosshairRenderer {
	public Minecraft mc;
	
	float zLevel = -90.0F;
	
	public crosshairRenderer() {
		FMLCommonHandler.instance().bus().register(this);
	    MinecraftForge.EVENT_BUS.register(this);
	    this.mc = Minecraft.getMinecraft();
	}
	
	@SubscribeEvent
	public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
		if (event.type == ElementType.CROSSHAIRS) {
			EntityPlayerSP player = this.mc.thePlayer;
			if ((player.getHeldItem() != null && player.getHeldItem().getItem() instanceof multiBow)) {
//				float partialticks = event.getPartialTicks();
				ScaledResolution resolution = event.resolution;
				
		        mc.entityRenderer.setupOverlayRendering();
		        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//		        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	//	        mc.getTextureManager().bindTexture(Gui.ICONS);
		        GL11.glEnable(GL11.GL_BLEND);
		        
				GameSettings gamesettings = this.mc.gameSettings;
	
		        if (gamesettings.thirdPersonView == 0)
		        {
//		            if (this.mc.playerController() && this.mc.pointedEntity == null)
//		            {
//		            	return;
//		            }
	
		            int w = resolution.getScaledWidth();
		            int h = resolution.getScaledHeight();
	
		            if (gamesettings.showDebugInfo && !gamesettings.hideGUI)
		            {
		            }
		            else
		            {
		            	double fov = Math.toRadians(gamesettings.fovSetting);
		            	fov *= player.getFOVMultiplier();
		            	
		            	double angle = Math.toRadians(multiBow.getAngle(player));
		            	
		            	double l = ((double) h/2) / Math.tan(fov/2);
		            	
		            	int offset = (int) (Math.tan(angle) * l);
		            	
		                GL11.glEnable(GL11.GL_BLEND);
		                OpenGlHelper.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, 1, 0);
		                this.drawTexturedModalRect(w / 2 - 7 + offset, h / 2 - 7, 0, 0, 16, 16);
		                this.drawTexturedModalRect(w / 2 - 7 - offset, h / 2 - 7, 0, 0, 16, 16);
		                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		                GL11.glDisable(GL11.GL_BLEND);
		            }
		        }
			}
		}
	}
	
    public void drawTexturedModalRect(int p_73729_1_, int p_73729_2_, int p_73729_3_, int p_73729_4_, int p_73729_5_, int p_73729_6_)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(p_73729_1_ + 0), (double)(p_73729_2_ + p_73729_6_), (double)this.zLevel, (double)((float)(p_73729_3_ + 0) * f), (double)((float)(p_73729_4_ + p_73729_6_) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + p_73729_6_), (double)this.zLevel, (double)((float)(p_73729_3_ + p_73729_5_) * f), (double)((float)(p_73729_4_ + p_73729_6_) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + 0), (double)this.zLevel, (double)((float)(p_73729_3_ + p_73729_5_) * f), (double)((float)(p_73729_4_ + 0) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + 0), (double)(p_73729_2_ + 0), (double)this.zLevel, (double)((float)(p_73729_3_ + 0) * f), (double)((float)(p_73729_4_ + 0) * f1));
        tessellator.draw();
    }
}
