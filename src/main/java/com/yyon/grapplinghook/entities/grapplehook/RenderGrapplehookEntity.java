package com.yyon.grapplinghook.entities.grapplehook;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;


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
public class RenderGrapplehookEntity<T extends GrapplehookEntity> extends EntityRenderer<T> {

	public static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	public static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	public static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);

    private static final ResourceLocation HOOK_TEXTURES = new ResourceLocation("grapplemod", "textures/entity/hook.png");
    private static final ResourceLocation ROPE_TEXTURES = new ResourceLocation("grapplemod", "textures/entity/rope.png");
    private static final RenderType ROPE_RENDER = RenderType.entitySolid(ROPE_TEXTURES);

	protected final Item item;
	protected final EntityRendererProvider.Context context;

	public RenderGrapplehookEntity(EntityRendererProvider.Context context, Item itemIn) {
		super(context);
		this.item = itemIn;
		this.context = context;
	}

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    @Override
    public void render(T hookEntity, float entityYaw, float partialTicks, PoseStack matrix, MultiBufferSource renderType, int packedLight) {
		if (!hookEntity.isAlive()) return;
		if (!(hookEntity.shootingEntity instanceof LivingEntity e && e.isAlive())) return;
		
		SegmentHandler segmentHandler = hookEntity.segmentHandler;

		// 0 is center point  |  1 is right.
		int handDirection = (e.getMainArm() == HumanoidArm.RIGHT ? 1 : -1) * (hookEntity.rightHand ? 1 : -1);

		// attack/swing progress
		float completion = e.getAttackAnim(partialTicks);
		float swingPos = Mth.sin(Mth.sqrt(completion) * (float)Math.PI);

		// get the offset from the center of the head to the hand
		Vec handOffset = this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && e == Minecraft.getInstance().player
				? this.getFirstPersonHandOffset(e, handDirection, swingPos, partialTicks)
				: this.getThirdPersonHandOffset(e, handDirection, swingPos, partialTicks);

		// get the hand position
		handOffset.y += e.getEyeHeight();
		Vec handPosition = handOffset.add(Vec.partialPositionVec(e, partialTicks));

		this.renderHook(matrix, hookEntity, segmentHandler, handPosition, handDirection, renderType, packedLight, partialTicks);
		this.renderRope(matrix, hookEntity, segmentHandler, handPosition, renderType, packedLight, partialTicks);
		super.render(hookEntity, entityYaw, partialTicks, matrix, renderType, packedLight);
    }

	protected Vec getFirstPersonHandOffset(LivingEntity grappleHookHolder, int handDirection, float swingPos, float partialTicks) {
		// base hand offset (no swing, when facing +Z)
		double d7 = this.entityRenderDispatcher.options.fov().get();
		d7 = d7 / 100.0D;

		Vec handOffset = new Vec(
				(double) handDirection * -0.46D * d7,
				-0.18D * d7,
				0.38D
		);

		// apply swing
		handOffset = handOffset.rotatePitch(-swingPos * 0.7F);
		handOffset = handOffset.rotateYaw(-swingPos * 0.5F);

		// apply looking direction
		handOffset = handOffset.rotatePitch(-Vec.lerp(partialTicks, grappleHookHolder.xRotO, grappleHookHolder.getXRot()) * ((float)Math.PI / 180F));
		return handOffset.rotateYaw(Vec.lerp(partialTicks, grappleHookHolder.yRotO, grappleHookHolder.getYRot()) * ((float)Math.PI / 180F));
	}

	protected Vec getThirdPersonHandOffset(LivingEntity grappleHookHolder, int handDirection, float swingPos, float partialTicks) {
		// base hand offset (no swing, when facing +Z)
		Vec handOffset = new Vec(
				(double) handDirection * -0.36D,
				-0.65D + (grappleHookHolder.isCrouching() ? -0.1875F : 0.0F),
				0.6D
		);

		// apply swing
		handOffset = handOffset.rotatePitch(swingPos * 0.7F);

		// apply body rotation
		return handOffset.rotateYaw(Vec.lerp(partialTicks, grappleHookHolder.yBodyRotO, grappleHookHolder.yBodyRot) * ((float)Math.PI / 180F));
	}

    private Vec getRelativeToEntity(GrapplehookEntity hookEntity, Vec inVec, float partialTicks) {
    	return inVec.sub(Vec.partialPositionVec(hookEntity, partialTicks));
    }

    // draw a segment of the rope
    public void drawSegment(Vec start, Vec finish, double taut, VertexConsumer vertexBuffer, Matrix4f matrix, Matrix3f matrix3, int packedLight) {
    	if (start.sub(finish).length() < 0.05) return;

        int squareCount = 16;
        if (taut == 1.0F) {
        	squareCount = 1;
        }

    	Vec diff = finish.sub(start);
        
        Vec forward = diff.changeLen(1);
        Vec up = forward.cross(new Vec(1, 0, 0));

        if (up.length() == 0) {
        	up = forward.cross(new Vec(0, 0, 1));
        }

        up.changeLen_ip(0.025);
        Vec side = forward.cross(up);
        side.changeLen_ip(0.025);
        
        Vec[] corners = new Vec[] {
				up.mult(-1).add(side.mult(-1)),
				up.add(side.mult(-1)),
				up.add(side),
				up.mult(-1).add(side)
		};

        for (int size = 0; size < 4; size++) {
            Vec corner1 = corners[size];
            Vec corner2 = corners[(size + 1) % 4];

        	Vec normal1 = corner1.normalize();
        	Vec normal2 = corner2.normalize();
            
            for (int squareNum = 0; squareNum < squareCount; squareNum++) {
                float squareFrac1 = (float) squareNum / (float) squareCount;
                Vec pos1 = start.add(diff.mult(squareFrac1));
                pos1.y += - (1 - taut) * (0.25 - Math.pow((squareFrac1 - 0.5), 2)) * 1.5;

                float squareFrac2 = ((float) squareNum + 1) / (float) squareCount;
                Vec pos2 = start.add(diff.mult(squareFrac2));
                pos2.y += - (1 - taut) * (0.25 - Math.pow((squareFrac2 - 0.5), 2)) * 1.5;
                
                Vec corner1pos1 = pos1.add(corner1);
                Vec corner2pos1 = pos1.add(corner2);
                Vec corner1pos2 = pos2.add(corner1);
                Vec corner2pos2 = pos2.add(corner2);
            	
                vertexBuffer.vertex(matrix, (float) corner1pos1.x, (float) corner1pos1.y, (float) corner1pos1.z).color(255, 255, 255, 255).uv(0, squareFrac1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3, (float) normal1.x, (float) normal1.y, (float) normal1.z).endVertex();
                vertexBuffer.vertex(matrix, (float) corner2pos1.x, (float) corner2pos1.y, (float) corner2pos1.z).color(255, 255, 255, 255).uv(1, squareFrac1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3, (float) normal2.x, (float) normal2.y, (float) normal2.z).endVertex();
                vertexBuffer.vertex(matrix, (float) corner2pos2.x, (float) corner2pos2.y, (float) corner2pos2.z).color(255, 255, 255, 255).uv(1, squareFrac2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3, (float) normal2.x, (float) normal2.y, (float) normal2.z).endVertex();
                vertexBuffer.vertex(matrix, (float) corner1pos2.x, (float) corner1pos2.y, (float) corner1pos2.z).color(255, 255, 255, 255).uv(0, squareFrac2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3, (float) normal1.x, (float) normal1.y, (float) normal1.z).endVertex();
            }
        }
    }

	protected void renderHook(PoseStack matrix, GrapplehookEntity hookEntity, SegmentHandler segmentHandler, Vec handPosition, int handDirection, MultiBufferSource renderType, int packedLight, float partialTicks) {
		// get direction of rope where hook is attached
		Vec attachDir = Vec.motionVec(hookEntity).mult(-1);
		if (attachDir.length() == 0) {
			if (hookEntity.attach_dir != null) {
				attachDir = hookEntity.attach_dir;

			} else {
				if (segmentHandler == null || segmentHandler.segments.size() <= 2) {
					attachDir = getRelativeToEntity(hookEntity, new Vec(handPosition), partialTicks);
				} else {
					Vec from = segmentHandler.segments.get(1);
					Vec to = Vec.partialPositionVec(hookEntity, partialTicks);
					attachDir = from.sub(to);
				}
			}
		}

		attachDir.normalize_ip();

		if (hookEntity.attached && hookEntity.attach_dir != null) {
			attachDir = hookEntity.attach_dir;
		}

		hookEntity.attach_dir = attachDir;

		// transformation so hook texture is facing the correct way
		matrix.pushPose();
		matrix.scale(0.5F, 0.5F, 0.5F);

		Quaternionf base = new Quaternionf();
		matrix.mulPose(base.rotateAxis((float) (-attachDir.getYaw()), Y_AXIS));
		matrix.mulPose(base.rotateAxis((float) (attachDir.getPitch() - 90), X_AXIS));
		matrix.mulPose(base.rotateAxis((float) (45 * handDirection), Y_AXIS));
		matrix.mulPose(base.rotateAxis((float) (-45), Z_AXIS));

		// draw hook
		ItemStack stack = this.getStackToRender();
		BakedModel bakedModel = this.context.getItemRenderer().getModel(stack, hookEntity.level, null, hookEntity.getId());
		this.context.getItemRenderer().render(stack, ItemTransforms.TransformType.NONE, false, matrix, renderType, packedLight, OverlayTexture.NO_OVERLAY, bakedModel);

		// revert transformation
		matrix.popPose();
	}


	protected void renderRope(PoseStack matrix, GrapplehookEntity hookEntity, SegmentHandler segmentHandler, Vec handPosition, MultiBufferSource renderType, int packedLight, float partialTicks) {
		// transformation (no transformation)
		matrix.pushPose();
		PoseStack.Pose pose = matrix.last();
		Matrix4f poseMatrix = pose.pose();
		Matrix3f normalMatrix = pose.normal();

		// initialize vertexbuffer (used for drawing)
		VertexConsumer vertexBuffer = renderType.getBuffer(ROPE_RENDER);

		// draw rope
		if (segmentHandler == null) {
			// if no segmenthandler, straight line from hand to hook
			this.drawSegment(new Vec(0,0,0), getRelativeToEntity(hookEntity, new Vec(handPosition), partialTicks), 1.0F, vertexBuffer, poseMatrix, normalMatrix, packedLight);

		} else {
			for (int i = 0; i < segmentHandler.segments.size() - 1; i++) {
				Vec from = segmentHandler.segments.get(i);
				Vec to = segmentHandler.segments.get(i+1);

				if (i == 0) {
					from = Vec.partialPositionVec(hookEntity, partialTicks);
				}

				if (i + 2 == segmentHandler.segments.size()) {
					to = handPosition;
				}

				from = this.getRelativeToEntity(hookEntity, from, partialTicks);
				to = this.getRelativeToEntity(hookEntity, to, partialTicks);

				double taut = 1;
				if (i == segmentHandler.segments.size() - 2) {
					taut = hookEntity.taut;
				}

				this.drawSegment(from, to, taut, vertexBuffer, poseMatrix, normalMatrix, packedLight);
			}
		}

		// draw tip of rope closest to hand
		Vec hookPos = Vec.partialPositionVec(hookEntity, partialTicks);
		Vec handClosest = segmentHandler == null || segmentHandler.segments.size() <= 2
				? hookPos
				: segmentHandler.segments.get(segmentHandler.segments.size() - 2);

		Vec diff = handClosest.sub(handPosition);
		Vec forward = diff.changeLen(1);
		Vec up = forward.cross(new Vec(1, 0, 0));

		if (up.length() == 0) {
			up = forward.cross(new Vec(0, 0, 1));
		}

		up.changeLen_ip(0.025);
		Vec side = forward.cross(up);
		side.changeLen_ip(0.025);

		Vec[] corners = new Vec[] {
				up.mult(-1).add(side.mult(-1)),
				up.mult(-1).add(side),
				up.add(side),
				up.add(side.mult(-1))
		};

		float[][] uvs = new float[][] {
				{ 0.00f, 0.99f },
				{ 0.00f, 1.00f },
				{ 1.00f, 1.00f },
				{ 1.00f, 0.99f }
		};

		for (int size = 0; size < 4; size++) {
			Vec corner = corners[size];
			Vec normal = corner.normalize();
			Vec cornerPos = getRelativeToEntity(hookEntity, handPosition, partialTicks).add(corner);
			vertexBuffer.vertex(poseMatrix, (float) cornerPos.x, (float) cornerPos.y, (float) cornerPos.z).color(255, 255, 255, 255).uv(uvs[size][0], uvs[size][1]).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normalMatrix, (float) normal.x, (float) normal.y, (float) normal.z).endVertex();
		}

		matrix.popPose();
	}

    @Override
    public boolean shouldRender(T entity, Frustum cam, double camPosX, double camPosY, double camPosZ) {
		return true;
	}

	public ItemStack getStackToRender() {
		ItemStack stack = new ItemStack(this.item);
		CompoundTag tag = stack.getOrCreateTag();
		tag.putBoolean("hook", true);
		stack.setTag(tag);
        return stack;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
	@Override
	public ResourceLocation getTextureLocation(T entity) {
        return HOOK_TEXTURES;
	}
}
