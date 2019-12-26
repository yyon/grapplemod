package com.yyon.grapplinghook.controllers;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
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
			
			if (this.entity.isInWater()) {
				this.unattach();
				return;
			}
			
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

			boolean wallrun = this.applywallrun();

			if (wallrun) {
				if (this.walldirection != null) {
					this.playermovement = this.playermovement.removealong(this.walldirection);
				}
				this.playermovement.changelen_ip(GrappleConfig.getconf().wallrunspeed);
				motion.add_ip(this.playermovement);
			} else {
				motion.add_ip(this.playermovement.changelen(0.01));
			}
			
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase entityliving = (EntityLivingBase) entity;
				if (entityliving.isElytraFlying()) {
					this.unattach();
				}
			}
			
			boolean issliding = grapplemod.proxy.issliding(this.entity);
			
			if (issliding) {
				this.applySlidingFriction();
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
				if (!issliding) {
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
}
