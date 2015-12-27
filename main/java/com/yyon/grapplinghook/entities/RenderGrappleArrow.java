package com.yyon.grapplinghook.entities;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
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
public class RenderGrappleArrow extends Render
{
    protected final Item field_177084_a;
    private final RenderItem field_177083_e;
    public RenderGrappleArrow(RenderManager p_i46137_1_, Item p_i46137_2_, RenderItem p_i46137_3_)
    {
        super(p_i46137_1_);
        this.field_177084_a = p_i46137_2_;
        this.field_177083_e = p_i46137_3_;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    @Override
    public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        this.bindTexture(TextureMap.locationBlocksTexture);
        this.field_177083_e.renderItemModel(this.func_177082_d(entity));
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, p_76986_8_, partialTicks);
        
        grappleArrow ga = ((grappleArrow) entity);
        
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
        if (ga.shootingEntity != null)
        {
//        	System.out.println(p_76986_8_);
        	p_76986_8_ = 0;
            float f9 = 0;//((EntityLivingBase) ga.shootingEntity).getSwingProgress(p_76986_8_);
            float f10 = MathHelper.sin(MathHelper.sqrt_float(f9) * (float)Math.PI);
            Vec3 vec3 = new Vec3(-0.36D, 0.03D, 0.35D);
            vec3 = vec3.rotatePitch(-(ga.shootingEntity.prevRotationPitch + (ga.shootingEntity.rotationPitch - ga.shootingEntity.prevRotationPitch) * p_76986_8_) * (float)Math.PI / 180.0F);
            vec3 = vec3.rotateYaw(-(ga.shootingEntity.prevRotationYaw + (ga.shootingEntity.rotationYaw - ga.shootingEntity.prevRotationYaw) * p_76986_8_) * (float)Math.PI / 180.0F);
            vec3 = vec3.rotateYaw(f10 * 0.5F);
            vec3 = vec3.rotatePitch(-f10 * 0.7F);
            double d3 = ga.shootingEntity.prevPosX + (ga.shootingEntity.posX - ga.shootingEntity.prevPosX) * (double)p_76986_8_ + vec3.xCoord;
            double d4 = ga.shootingEntity.prevPosY + (ga.shootingEntity.posY - ga.shootingEntity.prevPosY) * (double)p_76986_8_ + vec3.yCoord;
            double d5 = ga.shootingEntity.prevPosZ + (ga.shootingEntity.posZ - ga.shootingEntity.prevPosZ) * (double)p_76986_8_ + vec3.zCoord;
            double d6 = (double)ga.shootingEntity.getEyeHeight();

            if (this.renderManager.options != null && this.renderManager.options.thirdPersonView > 0 || ga.shootingEntity != Minecraft.getMinecraft().thePlayer)
            {
                float f11 = (((EntityLivingBase) ga.shootingEntity).prevRenderYawOffset + (((EntityLivingBase) ga.shootingEntity).renderYawOffset - ((EntityLivingBase) ga.shootingEntity).prevRenderYawOffset) * p_76986_8_) * (float)Math.PI / 180.0F;
                double d7 = (double)MathHelper.sin(f11);
                double d9 = (double)MathHelper.cos(f11);
//                double d11 = 0.35D;
//                double d13 = 0.8D;
                d3 = ga.shootingEntity.prevPosX + (ga.shootingEntity.posX - ga.shootingEntity.prevPosX) * (double)p_76986_8_ - d9 * 0.35D - d7 * 0.8D;
                d4 = ga.shootingEntity.prevPosY + d6 + (ga.shootingEntity.posY - ga.shootingEntity.prevPosY) * (double)p_76986_8_ - 0.45D;
                d5 = ga.shootingEntity.prevPosZ + (ga.shootingEntity.posZ - ga.shootingEntity.prevPosZ) * (double)p_76986_8_ - d7 * 0.35D + d9 * 0.8D;
                d6 = ga.shootingEntity.isSneaking() ? -0.1875D : 0.0D;
            }

            double d16 = ga.prevPosX + (ga.posX - ga.prevPosX) * (double)p_76986_8_;
            double d8 = ga.prevPosY + (ga.posY - ga.prevPosY) * (double)p_76986_8_ + 0.25D;
            double d10 = ga.prevPosZ + (ga.posZ - ga.prevPosZ) * (double)p_76986_8_;
            double d12 = (double)((float)(d3 - d16));
            double d14 = (double)((float)(d4 - d8)) + d6;
            double d15 = (double)((float)(d5 - d10));
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            worldrenderer.startDrawing(3);
            worldrenderer.setColorOpaque_I(0);
            byte b2 = 16;

            for (int i = 0; i <= b2; ++i)
            {
                float f12 = (float)i / (float)b2;
                worldrenderer.addVertex(x + d12 * (double)f12, y + d14 * (double)(f12 * f12 + f12) * 0.5D + 0.25D, z + d15 * (double)f12);
            }
            
            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }
    }

    public ItemStack func_177082_d(Entity p_177082_1_)
    {
        return new ItemStack(this.field_177084_a, 1, 0);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return TextureMap.locationBlocksTexture;
    }
}