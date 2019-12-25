package com.yyon.grapplinghook.controllers;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.vec;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

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

public class airfrictionController extends grappleController {
	public final double playermovementmult = 0.5;
	
	public airfrictionController(int arrowId, int entityId, World world, vec pos, int id, GrappleCustomization custom) {
		super(arrowId, entityId, world, pos, id, custom);
	}
	
	@Override
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			this.normalGround();
			this.normalCollisions();
			this.applyAirFriction();
			
			boolean doesrocket = false;
			if (this.custom != null) {
				if (this.custom.rocket) {
					vec rocket = this.rocket(entity);
					this.motion.add_ip(rocket);
					if (rocket.length() > 0) {
						doesrocket = true;
					}
				}
			}

			motion.add_ip(this.playermovement.changelen(0.01));
			
			boolean wallrun = this.applywallrun();

//			if (prev_wallrun && !wallrun && walldirection != null) {
//				vec facing = new vec(this.entity.getLookVec());
//				facing.y = 0;
//				facing.changelen_ip(1);
//
//				float entitywidth = this.entity.width;
//
//				vec direction_forward = walldirection;
//				
//				RayTraceResult raytraceresult = entity.world.rayTraceBlocks(this.entity.getPositionVector(), vec.positionvec(entity).add(direction_forward.changelen(entitywidth/2 + 0.05)).toVec3d(), false, true, false);
//				if (raytraceresult == null || raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
//					direction_againstwall = dir;
//					break;
//				}
//				
//				vec direction_againstwall = null;
//				
//				for (vec dir : new vec[] {direction_forward.cross(new vec(0,1,0)), direction_forward.cross(new vec(0,-1,0))}) {
//					RayTraceResult raytraceresult = entity.world.rayTraceBlocks(vec.positionvec(entity).add(direction_forward.changelen(entitywidth + 0.05)).toVec3d(), vec.positionvec(entity).add(direction_forward.changelen(entitywidth/2 + 0.05)).add(dir.changelen(entitywidth/2 + 0.5)).toVec3d(), false, true, false);
//					if (raytraceresult == null || raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
//						direction_againstwall = dir;
//						break;
//					}
//				}
//				
//				if (direction_againstwall != null) {
//					System.out.println("continue");
//					direction_forward.print();
//					direction_againstwall.print();
//					
//					motion.add_ip(direction_forward.changelen(0.2));
//					motion.add_ip(direction_againstwall.mult(-1).changelen(0.1));
//				} else {
//					prev_wallrun = false;
//					System.out.println("don't continue");
//				}
////				vec closebywall = getclosebywall();
////				if (closebywall != null) {
////					motion.add_ip(closebywall.changelen(0.3));
////					prev_wallrun = true;
////				} else {
////					prev_wallrun = wallrun;
////				}
//			} else {
//				prev_wallrun = wallrun;
//			}
			
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase entityliving = (EntityLivingBase) entity;
				if (entityliving.isElytraFlying()) {
					this.unattach();
				}
			}
			
			vec gravity = new vec(0, -0.05, 0);

			if (!wallrun) {
				motion.add_ip(gravity);
			}

			vec newmotion;
			
			newmotion = motion;
			
//			if (wallrun) {
//				newmotion.add_ip(this.walldirection);
//			}
			
			entity.motionX = newmotion.x;
			entity.motionY = newmotion.y;
			entity.motionZ = newmotion.z;
			
			this.updateServerPos();
			
			if (entity.onGround) {
				if (!wallrun) {
					if (!doesrocket) {
						this.unattach();
					} else {
						motion = vec.motionvec(entity);
					}
				}
			}
		}
	}
}
