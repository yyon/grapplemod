package com.yyon.grapplinghook.controllers;

import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

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

public class AirfrictionController extends GrappleController {
	public double playerMovementMult = 0.5;
	
	public int ignoreGroundCounter = 0;
	public boolean wasSliding = false;
	public boolean wasWallrunning = false;
	public boolean wasRocket = false;
	public boolean firstTickSinceCreated = true;
	
	public AirfrictionController(int grapplehookEntityId, int entityId, Level world, Vec pos, int id, GrappleCustomization custom) {
		super(grapplehookEntityId, entityId, world, pos, id, custom);
	}
	
	@Override
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (entity == null) {return;}
		
		if (entity.getVehicle() != null) {
			this.unattach();
			this.updateServerPos();
			return;
		}

		Vec additionalmotion = new Vec(0,0,0);
		
		if (GrappleConfig.getConf().other.dont_override_movement_in_air && !entity.isOnGround() && !wasSliding && !wasWallrunning && !wasRocket && !firstTickSinceCreated) {
			motion = Vec.motionVec(entity);
			this.unattach();
			return;
		}

		if (this.attached) {
			boolean issliding = ClientProxyInterface.proxy.isSliding(entity, motion);
			
			if (issliding && !wasSliding) {
				playSlideSound();
			}
			
			if (this.ignoreGroundCounter <= 0) {
				this.normalGround(issliding);					
				this.normalCollisions(issliding);
			}

			this.applyAirFriction();
			
			if (this.entity.isInWater() || this.entity.isInLava()) {
				this.unattach();
				return;
			}
			
			boolean doesrocket = false;
			if (this.custom != null) {
				if (this.custom.rocket) {
					Vec rocket = this.rocket(entity);
					this.motion.add_ip(rocket);
					if (rocket.length() > 0) {
						doesrocket = true;
					}
				}
			}

			if (issliding) {
				this.applySlidingFriction();
			}

			boolean wallrun = this.applyWallrun();

			if (!issliding && !wasSliding) {
				if (wallrun) {
					motion = motion.removeAlong(new Vec(0,1,0));
					if (this.wallDirection != null) {
						motion = motion.removeAlong(this.wallDirection);
					}

					Vec new_movement = this.playerMovement.changeLen(GrappleConfig.getConf().enchantments.wallrun.wallrun_speed*1.5);
					if (this.wallDirection != null) {
						new_movement = new_movement.removeAlong(this.wallDirection);
					}
					if (new_movement.length() > GrappleConfig.getConf().enchantments.wallrun.wallrun_speed) {
						new_movement.changeLen_ip(GrappleConfig.getConf().enchantments.wallrun.wallrun_speed);
					}
					Vec current_motion_along = this.motion.removeAlong(new Vec(0,1,0));
					Vec new_motion_along = this.motion.add(new_movement).removeAlong(new Vec(0,1,0));
					if (this.wallDirection != null) {
						current_motion_along = current_motion_along.removeAlong(this.wallDirection);
						new_motion_along = new_motion_along.removeAlong(this.wallDirection);
					}
					if (current_motion_along.length() <= GrappleConfig.getConf().enchantments.wallrun.wallrun_max_speed || current_motion_along.dot(new_movement) < 0) {
						motion.add_ip(new_movement);
						if (new_motion_along.length() > GrappleConfig.getConf().enchantments.wallrun.wallrun_max_speed) {
							this.motion.changeLen_ip(GrappleConfig.getConf().enchantments.wallrun.wallrun_max_speed);
						}
					}
					additionalmotion.add_ip(wallrunPressAgainstWall());
				} else {
					double max_motion = GrappleConfig.getConf().other.airstrafe_max_speed;
					double accel = GrappleConfig.getConf().other.airstrafe_acceleration;
					Vec motion_horizontal = motion.removeAlong(new Vec(0,1,0));
					double prev_motion = motion_horizontal.length();
					Vec new_motion_horizontal = motion_horizontal.add(this.playerMovement.changeLen(accel));
					double angle = motion_horizontal.angle(new_motion_horizontal);
					if (new_motion_horizontal.length() > max_motion && new_motion_horizontal.length() > prev_motion) {
						double ninety_deg = Math.PI / 2;
						double new_max_motion = max_motion;
						if (angle < ninety_deg && prev_motion > max_motion) {
							new_max_motion = prev_motion + ((max_motion - prev_motion) * (angle / (Math.PI / 2)));
						}
						new_motion_horizontal.changeLen_ip(new_max_motion);
					}
					motion.x = new_motion_horizontal.x;
					motion.z = new_motion_horizontal.z;
				}
			}
			
			if (entity instanceof LivingEntity) {
				LivingEntity entityliving = (LivingEntity) entity;
				if (entityliving.isFallFlying()) {
					this.unattach();
				}
			}
			
			Vec gravity = new Vec(0, -0.05, 0);

			if (!wallrun) {
				motion.add_ip(gravity);
			}

			Vec newmotion;
			
			newmotion = motion.add(additionalmotion);
			
//			if (wallrun) {
//				newmotion.add_ip(this.walldirection);
//			}

			newmotion.setMotion(entity);
			
			this.updateServerPos();
			
			if (entity.isOnGround()) {
				if (!issliding) {
					if (!wallrun) {
						if (!doesrocket) {
							if (ignoreGroundCounter <= 0) {
								this.unattach();
							}
						} else {
							motion = Vec.motionVec(entity);
						}
					}
				}
			}
			if (ignoreGroundCounter > 0) { ignoreGroundCounter--; }
			
			wasSliding = issliding;
			wasWallrunning = wallrun;
			wasRocket = doesrocket;
			firstTickSinceCreated = false;
		}
	}

	public void receiveEnderLaunch(double x, double y, double z) {
		super.receiveEnderLaunch(x, y, z);
		this.ignoreGroundCounter = 2;
	}
	
	public void slidingJump() {
		super.slidingJump();
		this.ignoreGroundCounter = 2;
	}
	
	public void playSlideSound() {
		ClientProxyInterface.proxy.playSlideSound(this.entity);
	}
}
