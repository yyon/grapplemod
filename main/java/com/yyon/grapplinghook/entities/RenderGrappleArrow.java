package com.yyon.grapplinghook.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

@SideOnly(Side.CLIENT)
public class RenderGrappleArrow extends Render
{
    protected final Item item;
//    private final RenderItem itemRenderer;
    private final ItemStack itemstack;
    private static final ResourceLocation LEASH_KNOT_TEXTURES = new ResourceLocation("textures/entity/lead_knot.png");
    
    public RenderGrappleArrow(Item itemIn)
    {
        super();
        this.item = itemIn;
        this.itemstack = new ItemStack(this.item);
//        this.itemRenderer = itemRendererIn;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        grappleArrow arrow = (grappleArrow) entity;
        if (arrow == null || arrow.isDead) {
        	return;
        }
        
        EntityLivingBase e = (EntityLivingBase) arrow.shootingEntity;
        
        if (e == null || e.isDead) {
        	return;
        }

        int primaryhand = 1;
        
        
    	Vec3 offset = Vec3.createVectorHelper(0,0,0);
    	if (!arrow.attached) {
	    	if (arrow.righthand) {
	            offset = Vec3.createVectorHelper((double)1 * -0.36D, -0.175D, 0.45D); // hand relative to person
	    	} else {
	            offset = Vec3.createVectorHelper((double)1 * 0.36D, -0.175D, 0.45D); // hand relative to person
	    	}
	        offset.rotateAroundX(-(e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * partialTicks) * 0.017453292F);
	        offset.rotateAroundY(-(e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * partialTicks) * 0.017453292F);
	        
	        double dist = e.getDistanceToEntity(arrow);
	        double mult = 1 - (dist / 10.0);
	        if (mult <= 0) {
	        	offset = Vec3.createVectorHelper(0,0,0);
	        } else {
	        	offset = Vec3.createVectorHelper(offset.xCoord * mult, offset.yCoord * mult, offset.zCoord * mult);
	        	
		        x += offset.xCoord;
		        y += offset.yCoord;
		        z += offset.zCoord;
	        }
    	}
    	
    	GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
//        GL11.glDisable(GL11.GL_LIGHTING);
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        
        GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        this.bindTexture(TextureMap.locationBlocksTexture);
        
        Tessellator tessellator = Tessellator.instance;
        
        IIcon iicon = this.item.getIconFromDamage(0);
        this.func_77026_a(tessellator, iicon);
//        this.itemRenderer.renderIcon(p_94149_1_, p_94149_2_, iicon, p_94149_4_, p_94149_5_)//.renderItem(this.getStackToRender(entity));

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    	
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        this.bindEntityTexture(entity);
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        tessellator.startDrawing(7);
        tessellator.addVertex(-0.5D, -0.5D, 0.0D);//.tex(0.0625D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.addVertex(0.5D, -0.5D, 0.0D);//.tex(0.125D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.addVertex(0.5D, 0.5D, 0.0D);//.tex(0.125D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.addVertex(-0.5D, 0.5D, 0.0D);//.tex(0.0625D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();

        int k = 1;
        float f7 = e.getSwingProgress(partialTicks);
        float f8 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
        float f9 = (e.prevRenderYawOffset + (e.renderYawOffset - e.prevRenderYawOffset) * partialTicks) * 0.017453292F;
        double d0 = (double)MathHelper.sin(f9);
        double d1 = (double)MathHelper.cos(f9);
        double d2 = (double)k * 0.35D;
        double d4;
        double d5;
        double d6;
        double d7;

        if ((this.renderManager.options == null || this.renderManager.options.thirdPersonView <= 0) && e == Minecraft.getMinecraft().thePlayer)
        {
        	Vec3 V;
        	if (arrow.righthand) {
                V = Vec3.createVectorHelper((double)k * -0.36D, -0.175D, 0.45D); // hand relative to person
        	} else {
                V = Vec3.createVectorHelper((double)k * 0.36D, -0.175D, 0.45D); // hand relative to person
        	}
            V.rotateAroundX(-(e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * partialTicks) * 0.017453292F);
            V.rotateAroundY(-(e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * partialTicks) * 0.017453292F);
            V.rotateAroundY(f8 * 0.5F);
            V.rotateAroundX(-f8 * 0.7F);
            d4 = e.prevPosX + (e.posX - e.prevPosX) * (double)partialTicks + V.xCoord;
            d5 = e.prevPosY + (e.posY - e.prevPosY) * (double)partialTicks + V.yCoord;
            d6 = e.prevPosZ + (e.posZ - e.prevPosZ) * (double)partialTicks + V.zCoord;
            d7 = (double)e.getEyeHeight();
        }
        else
        {
            d4 = e.prevPosX + (e.posX - e.prevPosX) * (double)partialTicks - d1 * d2 - d0 * 0.8D;
            d5 = e.prevPosY + (double)e.getEyeHeight() + (e.posY - e.prevPosY) * (double)partialTicks - 0.45D;
            d6 = e.prevPosZ + (e.posZ - e.prevPosZ) * (double)partialTicks - d0 * d2 + d1 * 0.8D;
            d7 = e.isSneaking() ? -0.1875D : 0.0D;
        }

        double d13 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
        double d8 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks;
        double d9 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
        
        // hand position
        double d10 = (double)((float)(d4 - d13)) - offset.xCoord;
        double d11 = (double)((float)(d5 - d8)) + d7 - offset.yCoord;
        double d12 = (double)((float)(d6 - d9)) - offset.zCoord;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        tessellator.startDrawing(5);
        
        double taut = arrow.taut;
        
    	boolean reverse = false;
    	if (arrow.posY < e.posY+1.62) {
    		reverse = true;
    	}
    	
        double X;
        double Y;
        double Z;
        for (int i1 = 0; i1 <= 16; ++i1)
        {
            float R = 0.5F;
            float G = 0.4F;
            float B = 0.3F;

            if (i1 % 2 == 0)
            {
                R *= 0.7F;
                G *= 0.7F;
                B *= 0.7F;
            }
            
            float f10 = (float)i1 / 16.0F;
        	X = x + d10 * (double)f10;
        	Z = z + d12 * (double)f10;
            if (reverse) {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(f10*f10 + f10) * 0.5D);
            } else {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(Math.sqrt(f10)));
            }
            
            tessellator.setColorRGBA_F(R, G, B, 1.0F);
            tessellator.addVertex(X, Y + 0.025D, Z);//.color(R, G, B, 1.0F).endVertex();
            tessellator.addVertex(X - 0.025D, Y, Z - 0.025D);//.color(R, G, B, 1.0F).endVertex();
        }
        
        tessellator.draw();
        tessellator.startDrawing(5);

        for (int i1 = 0; i1 <= 16; ++i1)
        {
            float R = 0.5F;
            float G = 0.4F;
            float B = 0.3F;

            if (i1 % 2 == 0)
            {
                R *= 0.7F;
                G *= 0.7F;
                B *= 0.7F;
            }
            
            float f10 = (float)i1 / 16.0F;
        	X = x + d10 * (double)f10;
        	Z = z + d12 * (double)f10;
            if (reverse) {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(f10*f10 + f10) * 0.5D);
            } else {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(Math.sqrt(f10)));
            }
            tessellator.setColorRGBA_F(R, G, B, 1.0F);
            tessellator.addVertex(X + 0.025D, Y, Z - 0.025D);//.color(R, G, B, 1.0F).endVertex();
            tessellator.addVertex(X, Y + 0.025D, Z);//.color(R, G, B, 1.0F).endVertex();
        }
        
        tessellator.draw();
        tessellator.startDrawing(5);

        for (int i1 = 0; i1 <= 16; ++i1)
        {
            float R = 0.5F;
            float G = 0.4F;
            float B = 0.3F;

            if (i1 % 2 == 0)
            {
                R *= 0.7F;
                G *= 0.7F;
                B *= 0.7F;
            }
            
            float f10 = (float)i1 / 16.0F;
        	X = x + d10 * (double)f10;
        	Z = z + d12 * (double)f10;
            if (reverse) {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(f10*f10 + f10) * 0.5D);
            } else {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(Math.sqrt(f10)));
            }
            tessellator.setColorRGBA_F(R, G, B, 1.0F);
            tessellator.addVertex(X, Y - 0.025D, Z);//.color(R, G, B, 1.0F).endVertex();
            tessellator.addVertex(X + 0.025D, Y, Z - 0.025D);//.color(R, G, B, 1.0F).endVertex();
        }
        
        tessellator.draw();
        tessellator.startDrawing(5);

        for (int i1 = 0; i1 <= 16; ++i1)
        {
            float R = 0.5F;
            float G = 0.4F;
            float B = 0.3F;

            if (i1 % 2 == 0)
            {
                R *= 0.7F;
                G *= 0.7F;
                B *= 0.7F;
            }
            
            float f10 = (float)i1 / 16.0F;
        	X = x + d10 * (double)f10;
        	Z = z + d12 * (double)f10;
            if (reverse) {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(f10*f10 + f10) * 0.5D);
            } else {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(Math.sqrt(f10)));
            }
            tessellator.setColorRGBA_F(R, G, B, 1.0F);
            tessellator.addVertex(X - 0.025D, Y, Z - 0.025D);//.color(R, G, B, 1.0F).endVertex();
            tessellator.addVertex(X, Y - 0.025D, Z);//.color(R, G, B, 1.0F).endVertex();
         }
        
        tessellator.draw();
        tessellator.startDrawing(5);
    	X = x + d10;
    	Y = y + d11;
    	Z = z + d12;
        tessellator.setColorRGBA_F(0.5F * 0.7F, 0.4F * 0.7F, 0.3F * 0.7F, 1.0F);
        tessellator.addVertex(X, Y - 0.025D, Z);//.color(0.5F * 0.7F, 0.4F * 0.7F, 0.3F * 0.7F, 1.0F).endVertex();
        tessellator.addVertex(X - 0.025D, Y, Z - 0.025D);//.color(0.5F * 0.7F, 0.4F * 0.7F, 0.3F * 0.7F, 1.0F).endVertex();
        tessellator.addVertex(X, Y + 0.025D, Z);//.color(0.5F * 0.7F, 0.4F * 0.7F, 0.3F * 0.7F, 1.0F).endVertex();
        tessellator.addVertex(X + 0.025D, Y, Z - 0.025D);//.color(0.5F * 0.7F, 0.4F * 0.7F, 0.3F * 0.7F, 1.0F).endVertex();
        tessellator.addVertex(X, Y - 0.025D, Z);//.color(0.5F * 0.7F, 0.4F * 0.7F, 0.3F * 0.7F, 1.0F).endVertex();
        tessellator.draw();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
//        GL11.enableLighting();
//        GL11.enableTexture2D();
//        GL11.enableCull();
//            GL11.glEnable(GL11.GL_LIGHTING);
            
//        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        
        
    }

//    @Override
//	public boolean shouldRender(Entity livingEntity, ICamera camera, double camX,
//			double camY, double camZ) {
//		return true;
//	}

	public ItemStack getStackToRender(Entity entityIn)
    {
        return this.itemstack;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return LEASH_KNOT_TEXTURES;
    }
    
    private void func_77026_a(Tessellator p_77026_1_, IIcon p_77026_2_)
    {
        float f = p_77026_2_.getMinU();
        float f1 = p_77026_2_.getMaxU();
        float f2 = p_77026_2_.getMinV();
        float f3 = p_77026_2_.getMaxV();
        float f4 = 1.0F;
        float f5 = 0.5F;
        float f6 = 0.25F;
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        p_77026_1_.startDrawingQuads();
        p_77026_1_.setNormal(0.0F, 1.0F, 0.0F);
        p_77026_1_.addVertexWithUV((double)(0.0F - f5), (double)(0.0F - f6), 0.0D, (double)f, (double)f3);
        p_77026_1_.addVertexWithUV((double)(f4 - f5), (double)(0.0F - f6), 0.0D, (double)f1, (double)f3);
        p_77026_1_.addVertexWithUV((double)(f4 - f5), (double)(f4 - f6), 0.0D, (double)f1, (double)f2);
        p_77026_1_.addVertexWithUV((double)(0.0F - f5), (double)(f4 - f6), 0.0D, (double)f, (double)f2);
        p_77026_1_.draw();
    }
}
