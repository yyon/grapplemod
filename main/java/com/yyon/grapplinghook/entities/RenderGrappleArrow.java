package com.yyon.grapplinghook.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


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

@OnlyIn(Dist.CLIENT)
public class RenderGrappleArrow<T extends grappleArrow> extends EntityRenderer<T>
{
    protected final Item item;
    private static final ResourceLocation HOOK_TEXTURES = new ResourceLocation("grapplemod", "textures/items/grapplinghook.png");
    private static final RenderType HOOK_RENDER = RenderType.entityCutout(HOOK_TEXTURES);
    private static final ResourceLocation ROPE_TEXTURES = new ResourceLocation("grapplemod", "textures/entity/rope.png");
    private static final RenderType ROPE_RENDER = RenderType.entitySolid(ROPE_TEXTURES);
    
    public RenderGrappleArrow(EntityRendererManager renderManagerIn, Item itemIn)
    {
        super(renderManagerIn);
        this.item = itemIn;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    @Override
    public void render(T arrow, float p_225623_2_, float partialTicks, MatrixStack matrix, IRenderTypeBuffer rendertype, int p_225623_6_) {
		if (arrow == null || !arrow.isAlive()) {
			return;
		}
		
		SegmentHandler segmenthandler = arrow.segmenthandler;
		
		LivingEntity e = (LivingEntity) arrow.shootingEntity;
		
		if (e == null || !e.isAlive()) {
			return;
		}
		
		LivingEntity playerentity = e;
		
		/** draw hook **/
		
		// transformation so hook texture is facing camera
		matrix.pushPose();
		matrix.scale(0.5F, 0.5F, 0.5F);
		matrix.mulPose(this.entityRenderDispatcher.cameraOrientation());
		matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		MatrixStack.Entry matrixstack$entry = matrix.last();
		Matrix4f matrix4f = matrixstack$entry.pose();
		Matrix3f matrix3f = matrixstack$entry.normal();
		
		// draw hook texture
		IVertexBuilder ivertexbuilder = rendertype.getBuffer(HOOK_RENDER);
		vertex(ivertexbuilder, matrix4f, matrix3f, p_225623_6_, 0.0F, 0, 0, 1);
		vertex(ivertexbuilder, matrix4f, matrix3f, p_225623_6_, 1.0F, 0, 1, 1);
		vertex(ivertexbuilder, matrix4f, matrix3f, p_225623_6_, 1.0F, 1, 1, 0);
		vertex(ivertexbuilder, matrix4f, matrix3f, p_225623_6_, 0.0F, 1, 0, 0);
		
		// revert transformation
		matrix.popPose();
		
		/** get player hand position **/
		
		// is right hand?
		int hand_right = (playerentity.getMainArm() == HandSide.RIGHT ? 1 : -1) * (arrow.righthand ? 1 : -1);
		
		// attack/swing progress
		float f = playerentity.getAttackAnim(partialTicks);
		float f1 = MathHelper.sin(MathHelper.sqrt(f) * (float)Math.PI);
		
		// get the offset from the center of the head to the hand
		vec hand_offset;
		if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && playerentity == Minecraft.getInstance().player) {
			// if first person
			
			// base hand offset (no swing, when facing +Z)
			double d7 = this.entityRenderDispatcher.options.fov;
			d7 = d7 / 100.0D;
			hand_offset = new vec((double) hand_right * -0.38D * d7, -0.1D * d7, 0.38D);
			// apply swing
			hand_offset = hand_offset.rotate_pitch(-f1 * 0.7F);
			hand_offset = hand_offset.rotate_yaw(-f1 * 0.5F);
			// apply looking direction
			hand_offset = hand_offset.rotate_pitch(-vec.lerp(partialTicks, playerentity.xRotO, playerentity.xRot) * ((float)Math.PI / 180F));
			hand_offset = hand_offset.rotate_yaw(vec.lerp(partialTicks, playerentity.yRotO, playerentity.yRot) * ((float)Math.PI / 180F));
		} else {
			// if third person
			
			// base hand offset (no swing, when facing +Z)
			hand_offset = new vec((double) hand_right * -0.36D, -0.65D + (playerentity.isCrouching() ? -0.1875F : 0.0F), 0.6D);
			// apply swing
			hand_offset = hand_offset.rotate_pitch(f1 * 0.7F);
			// apply body rotation
			hand_offset = hand_offset.rotate_yaw(vec.lerp(partialTicks, playerentity.yBodyRotO, playerentity.yBodyRot) * ((float)Math.PI / 180F));
		}
		
		// get the hand position
		hand_offset.y += playerentity.getEyeHeight();
		vec hand_position = hand_offset.add(vec.partialpositionvec(playerentity, partialTicks));
        
		
		/** draw rope **/
		
		// transformation (no tranformation)
        matrix.pushPose();
        matrixstack$entry = matrix.last();
        Matrix4f matrix4f1 = matrixstack$entry.pose();
        Matrix3f matrix3f1 = matrixstack$entry.normal();

        // initialize vertexbuffer (used for drawing)
        IVertexBuilder vertexbuffer = rendertype.getBuffer(ROPE_RENDER);
        
        // draw rope
        if (segmenthandler == null) {
        	// if no segmenthandler, straight line from hand to hook
    		drawSegment(new vec(0,0,0), getRelativeToEntity(arrow, new vec(hand_position), partialTicks), 1.0F, vertexbuffer, matrix4f1, matrix3f1, p_225623_6_);
        } else {
        	for (int i = 0; i < segmenthandler.segments.size() - 1; i++) {
        		vec from = segmenthandler.segments.get(i);
        		vec to = segmenthandler.segments.get(i+1);
        		
        		if (i == 0) {
        			from = vec.partialpositionvec(arrow, partialTicks);
        		}
        		if (i + 2 == segmenthandler.segments.size()) {
        			to = hand_position;
        		}
        		
        		from = getRelativeToEntity(arrow, from, partialTicks);
        		to = getRelativeToEntity(arrow, to, partialTicks);
        		
        		double taut = 1;
        		if (i == segmenthandler.segments.size() - 2) {
//        			taut = arrow.taut;
        		}
        		
        		drawSegment(from, to, taut, vertexbuffer, matrix4f1, matrix3f1, p_225623_6_);
        	}
        }

		matrix.popPose();
        
		
         
		super.render(arrow, p_225623_2_, partialTicks, matrix, rendertype, p_225623_6_);
    }
    
    vec getRelativeToEntity(grappleArrow arrow, vec inVec, float partialTicks) {
    	return inVec.sub(vec.partialpositionvec(arrow, partialTicks));
    }
    
    // vertex for the hook
    private static void vertex(IVertexBuilder p_229106_0_, Matrix4f p_229106_1_, Matrix3f p_229106_2_, int p_229106_3_, float p_229106_4_, int p_229106_5_, int p_229106_6_, int p_229106_7_) {
        p_229106_0_.vertex(p_229106_1_, p_229106_4_ - 0.5F, (float)p_229106_5_ - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)p_229106_6_, (float)p_229106_7_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_229106_3_).normal(p_229106_2_, 0.0F, 1.0F, 0.0F).endVertex();
     }

    // draw a segment of the rope
    public void drawSegment(vec start, vec finish, double taut, IVertexBuilder vertexbuffer, Matrix4f matrix, Matrix3f matrix3, int p_225623_6_) {
    	if (start.sub(finish).length() < 0.05) {
    		return;
    	}

        vec diff = finish.sub(start);
        
        vec forward = diff.changelen(1);
        vec up = forward.cross(new vec(1, 0, 0));
        if (up.length() == 0) {
        	up = forward.cross(new vec(0, 0, 1));
        }
        up.changelen_ip(0.025);
        vec side = forward.cross(up);
        side.changelen_ip(0.025);
        
        vec[] corners = new vec[] {up.mult(-1).add(side.mult(-1)), up.add(side.mult(-1)), up.add(side), up.mult(-1).add(side)};

        for (int size = 0; size < 4; size++) {
            vec corner1 = corners[size];
            vec corner2 = corners[(size + 1) % 4];

        	vec normal = corner1.add(corner2).normalize();
            
            int number_squares = 16;
            for (int square_num = 0; square_num < number_squares; square_num++)
            {
                float squarefrac1 = (float)square_num / (float) number_squares;
                vec pos1 = start.add(diff.mult(squarefrac1));
                pos1.y += - (1 - taut) * (0.25 - Math.pow((squarefrac1 - 0.5), 2)) * 1.5;
                float squarefrac2 = ((float) square_num+1) / (float) number_squares;
                vec pos2 = start.add(diff.mult(squarefrac2));
                pos2.y += - (1 - taut) * (0.25 - Math.pow((squarefrac2 - 0.5), 2)) * 1.5;
                
                vec corner1pos1 = pos1.add(corner1);
                vec corner2pos1 = pos1.add(corner2);
                vec corner1pos2 = pos2.add(corner1);
                vec corner2pos2 = pos2.add(corner2);
            	
                vertexbuffer.vertex(matrix, (float) corner1pos1.x, (float) corner1pos1.y, (float) corner1pos1.z).color(255, 255, 255, 255).uv(0, squarefrac1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_225623_6_).normal(matrix3, (float) normal.x, (float) normal.y, (float) normal.z).endVertex();
                vertexbuffer.vertex(matrix, (float) corner2pos1.x, (float) corner2pos1.y, (float) corner2pos1.z).color(255, 255, 255, 255).uv(1, squarefrac1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_225623_6_).normal(matrix3, (float) normal.x, (float) normal.y, (float) normal.z).endVertex();
                vertexbuffer.vertex(matrix, (float) corner2pos2.x, (float) corner2pos2.y, (float) corner2pos2.z).color(255, 255, 255, 255).uv(1, squarefrac2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_225623_6_).normal(matrix3, (float) normal.x, (float) normal.y, (float) normal.z).endVertex();
                vertexbuffer.vertex(matrix, (float) corner1pos2.x, (float) corner1pos2.y, (float) corner1pos2.z).color(255, 255, 255, 255).uv(0, squarefrac2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_225623_6_).normal(matrix3, (float) normal.x, (float) normal.y, (float) normal.z).endVertex();
            }
        }
        
    }

    @Override
    public boolean shouldRender(T p_225626_1_, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
		return true;
	}

	public ItemStack getStackToRender(T entityIn)
    {
        return new ItemStack(this.item);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
	@Override
	public ResourceLocation getTextureLocation(T entity) {
        return HOOK_TEXTURES;
	}
}
