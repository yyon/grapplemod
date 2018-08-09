package com.yyon.grapplinghook.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


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
public class RenderGrappleArrow<T extends Entity> extends Render<T>
{
    protected final Item item;
    private final RenderItem itemRenderer;
    private static final ResourceLocation LEASH_KNOT_TEXTURES = new ResourceLocation("textures/entity/lead_knot.png");
    
    public RenderGrappleArrow(RenderManager renderManagerIn, Item itemIn, RenderItem itemRendererIn)
    {
        super(renderManagerIn);
        this.item = itemIn;
        this.itemRenderer = itemRendererIn;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        grappleArrow arrow = (grappleArrow) entity;
        if (arrow == null || arrow.isDead) {
        	return;
        }
        
        EntityLivingBase e = (EntityLivingBase) arrow.shootingEntity;
        
        if (e == null || e.isDead) {
        	return;
        }

        int primaryhand = e.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;
        
        
    	Vec3d offset = new Vec3d(0,0,0);
    	if (!arrow.attached) {
	    	if (arrow.righthand) {
	            offset = new Vec3d((double)primaryhand * -0.36D, -0.175D, 0.45D); // hand relative to person
	    	} else {
	            offset = new Vec3d((double)primaryhand * 0.36D, -0.175D, 0.45D); // hand relative to person
	    	}
	        offset = offset.rotatePitch(-(e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * partialTicks) * 0.017453292F);
	        offset = offset.rotateYaw(-(e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * partialTicks) * 0.017453292F);
	        
	        double dist = e.getDistance(arrow);
	        double mult = 1 - (dist / 10.0);
	        if (mult <= 0) {
	        	offset = new Vec3d(0,0,0);
	        } else {
	        	offset = new Vec3d(offset.x * mult, offset.y * mult, offset.z * mult);
	        	
		        x += offset.x;
		        y += offset.y;
		        z += offset.z;
	        }
    	}
    	
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        
//        GL11.glDisable(GL11.GL_LIGHTING);
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        this.itemRenderer.renderItem(this.getStackToRender(entity), ItemCameraTransforms.TransformType.GROUND);

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    	
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        this.bindEntityTexture(entity);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        vertexbuffer.pos(-0.5D, -0.5D, 0.0D).tex(0.0625D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(0.5D, -0.5D, 0.0D).tex(0.125D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(0.5D, 0.5D, 0.0D).tex(0.125D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(-0.5D, 0.5D, 0.0D).tex(0.0625D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        int k = e.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;
        float f7 = e.getSwingProgress(partialTicks);
        float f8 = MathHelper.sin(MathHelper.sqrt(f7) * (float)Math.PI);
        float f9 = (e.prevRenderYawOffset + (e.renderYawOffset - e.prevRenderYawOffset) * partialTicks) * 0.017453292F;
        double d0 = (double)MathHelper.sin(f9);
        double d1 = (double)MathHelper.cos(f9);
        double d2 = (double)k * 0.35D;
        double d4;
        double d5;
        double d6;
        double d7;

        if ((this.renderManager.options == null || this.renderManager.options.thirdPersonView <= 0) && e == Minecraft.getMinecraft().player)
        {
        	Vec3d vec3d;
        	if (arrow.righthand) {
                vec3d = new Vec3d((double)k * -0.36D, -0.175D, 0.45D); // hand relative to person
        	} else {
                vec3d = new Vec3d((double)k * 0.36D, -0.175D, 0.45D); // hand relative to person
        	}
            vec3d = vec3d.rotatePitch(-(e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * partialTicks) * 0.017453292F);
            vec3d = vec3d.rotateYaw(-(e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * partialTicks) * 0.017453292F);
            vec3d = vec3d.rotateYaw(f8 * 0.5F);
            vec3d = vec3d.rotatePitch(-f8 * 0.7F);
            d4 = e.prevPosX + (e.posX - e.prevPosX) * (double)partialTicks + vec3d.x;
            d5 = e.prevPosY + (e.posY - e.prevPosY) * (double)partialTicks + vec3d.y;
            d6 = e.prevPosZ + (e.posZ - e.prevPosZ) * (double)partialTicks + vec3d.z;
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
        double d10 = (double)((float)(d4 - d13)) - offset.x;
        double d11 = (double)((float)(d5 - d8)) + d7 - offset.y;
        double d12 = (double)((float)(d6 - d9)) - offset.z;
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);
        
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
/*            if (reverse) {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(f10*f10 + f10) * 0.5D);
            } else {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(Math.sqrt(f10)));
            }*/
        	Y = y + d11 * (double)f10 - (1 - taut) * (0.25 - Math.pow((f10 - 0.5), 2)) * 1.5;
            
            vertexbuffer.pos(X, Y + 0.025D, Z).color(R, G, B, 1.0F).endVertex();
            vertexbuffer.pos(X - 0.025D, Y, Z - 0.025D).color(R, G, B, 1.0F).endVertex();
        }
        
        tessellator.draw();
        vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);

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
/*            if (reverse) {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(f10*f10 + f10) * 0.5D);
            } else {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(Math.sqrt(f10)));
            }*/
        	Y = y + d11 * (double)f10 - (1 - taut) * (0.25 - Math.pow((f10 - 0.5), 2)) * 1.5;
            vertexbuffer.pos(X + 0.025D, Y, Z - 0.025D).color(R, G, B, 1.0F).endVertex();
            vertexbuffer.pos(X, Y + 0.025D, Z).color(R, G, B, 1.0F).endVertex();
        }
        
        tessellator.draw();
        vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);

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
/*            if (reverse) {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(f10*f10 + f10) * 0.5D);
            } else {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(Math.sqrt(f10)));
            }*/
        	Y = y + d11 * (double)f10 - (1 - taut) * (0.25 - Math.pow((f10 - 0.5), 2)) * 1.5;
            vertexbuffer.pos(X, Y - 0.025D, Z).color(R, G, B, 1.0F).endVertex();
            vertexbuffer.pos(X + 0.025D, Y, Z - 0.025D).color(R, G, B, 1.0F).endVertex();
        }
        
        tessellator.draw();
        vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);

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
/*            if (reverse) {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(f10*f10 + f10) * 0.5D);
            } else {
            	Y = y + (d11 * (double) f10) * taut + (1-taut) * (d11 * (double)(Math.sqrt(f10)));
            }*/
        	Y = y + d11 * (double)f10 - (1 - taut) * (0.25 - Math.pow((f10 - 0.5), 2)) * 1.5;
            vertexbuffer.pos(X - 0.025D, Y, Z - 0.025D).color(R, G, B, 1.0F).endVertex();
            vertexbuffer.pos(X, Y - 0.025D, Z).color(R, G, B, 1.0F).endVertex();
         }
        
        tessellator.draw();
        vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);
    	X = x + d10;
    	Y = y + d11;
    	Z = z + d12;
        vertexbuffer.pos(X, Y - 0.025D, Z).color(0.5F * 0.7F, 0.4F * 0.7F, 0.3F * 0.7F, 1.0F).endVertex();
        vertexbuffer.pos(X - 0.025D, Y, Z - 0.025D).color(0.5F * 0.7F, 0.4F * 0.7F, 0.3F * 0.7F, 1.0F).endVertex();
        vertexbuffer.pos(X, Y + 0.025D, Z).color(0.5F * 0.7F, 0.4F * 0.7F, 0.3F * 0.7F, 1.0F).endVertex();
        vertexbuffer.pos(X + 0.025D, Y, Z - 0.025D).color(0.5F * 0.7F, 0.4F * 0.7F, 0.3F * 0.7F, 1.0F).endVertex();
        vertexbuffer.pos(X, Y - 0.025D, Z).color(0.5F * 0.7F, 0.4F * 0.7F, 0.3F * 0.7F, 1.0F).endVertex();
        tessellator.draw();

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableCull();
//            GL11.glEnable(GL11.GL_LIGHTING);
            
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        
        
    }

    @Override
	public boolean shouldRender(T livingEntity, ICamera camera, double camX,
			double camY, double camZ) {
		return true;
	}

	public ItemStack getStackToRender(T entityIn)
    {
        return new ItemStack(this.item);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return LEASH_KNOT_TEXTURES;
    }
}
