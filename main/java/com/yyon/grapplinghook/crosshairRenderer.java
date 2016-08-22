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

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class crosshairRenderer {
	public Minecraft mc;
	
	float zLevel = -90.0F;
	
	public crosshairRenderer() {
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
		            }
	
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
		            	
		                OpenGlHelper.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, 1, 0);
//		                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		                GL11.glEnable(GL11.GL_ALPHA_TEST);
//		                GlStateManager.enableAlpha();
		                this.drawTexturedModalRect(w / 2 - 7 + offset, h / 2 - 7, 0, 0, 16, 16);
		                this.drawTexturedModalRect(w / 2 - 7 - offset, h / 2 - 7, 0, 0, 16, 16);
		            }
		        }
			}
		}
	
    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
//        VertexBuffer vertexbuffer = tessellator.getBuffer();
        tessellator.startDrawing(7);
//        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        tessellator.addVertex((double)(x + 0), (double)(y + height), (double)this.zLevel);//.tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).endVertex();
        tessellator.addVertex((double)(x + width), (double)(y + height), (double)this.zLevel);//.tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).endVertex();
        tessellator.addVertex((double)(x + width), (double)(y + 0), (double)this.zLevel);//.tex((double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        tessellator.addVertex((double)(x + 0), (double)(y + 0), (double)this.zLevel);//.tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        tessellator.draw();
    }
}
