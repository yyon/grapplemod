package com.yyon.grapplinghook.controllers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.yyon.grapplinghook.vec;

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

public class smartHookControl extends grappleController {
	public smartHookControl(int arrowId, int entityId, World world, vec pos, double maxlen, int id, boolean slow) {
		super(arrowId, entityId, world, pos, maxlen, id, null);
		if (slow) {
			this.acceleration = this.acceleration / 2;
		}
	}

//	public double maxspeed = 4;
	public double acceleration = 0.2;
	public float oldstepheight;
	public final double playermovementmult = 1;
		
	@Override
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null && entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (true) {
//					this.normalGround();
					this.normalCollisions();
					this.applyAirFriction();
					
					vec arrowpos = null;//this.pos;
					vec playerpos = vec.positionvec(player);
					
					vec oldspherevec = playerpos.sub(arrowpos);
					vec spherevec = oldspherevec.changelen(0);
//					Vec3 spherechange = spherevec.subtract(oldspherevec);
//					Vec3 spherepos = spherevec.add(arrowpos);

		        	vec facing = new vec(player.getLookVec()).normalize();

					double dist = oldspherevec.length();
					
					if (this.isjumping()) {
//						this.dojump(player, spherevec);
						return;
					} else {
						applyPlayerMovement();
					}
					
					vec newmotion;
					
					if (dist < 4) {
						if (motion.length() > 0.3) {
							motion.mult_ip(0.6);
						}
						
						if (player.onGround) {
							entity.motionX = 0;
							entity.motionY = 0;
							entity.motionZ = 0;
							this.updateServerPos();
						}
					}
					
					vec gravity = new vec(0, -0.05, 0);
					
					if (!(this.ongroundtimer > 0)) {
						motion.add_ip(gravity);
					}
					
					vec ropevec = arrowpos.sub(playerpos).normalize();
//					if (ropevec.dot(facing) > 0) {
						vec ropexz = new vec(ropevec.x, 0, ropevec.z);
						vec facingxz = new vec(facing.x, 0, facing.z);//.proj(ropexz);
						double facinglmult = (ropevec.length()) / (facing.length());
						if (facinglmult < 0) {
							facinglmult = -facinglmult;
						}
						double pulll = gravity.y / (facing.y * facinglmult - ropevec.y);
						if (pulll > acceleration) {
							pulll = acceleration;
						}
						if (pulll < 0) {
							pulll = acceleration;
						}
						vec pullvec = ropevec.changelen(pulll);
						
						motion.add_ip(pullvec);
//					}
					
					double speed = motion.proj(oldspherevec).length();
					
					newmotion = motion;
					
					entity.motionX = newmotion.x;
					entity.motionY = newmotion.y;
					entity.motionZ = newmotion.z;
					
					player.fallDistance = 0;
					
					this.updateServerPos();
				}
			}
		}
	}
	

}
