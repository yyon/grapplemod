package com.yyon.grapplinghook.controllers;

import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.Vec;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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

public class AirfrictionController extends GrappleController {
	public final double playermovementmult = 0.5;
	
	public int ignoregroundcounter = 0;
	public boolean was_sliding = false;
	public boolean was_wallrunning = false;
	public boolean was_rocket = false;
	public boolean first_tick_since_created = true;
	
	public AirfrictionController(int arrowId, int entityId, World world, Vec pos, int id, GrappleCustomization custom) {
		super(arrowId, entityId, world, pos, id, custom);
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
		
		if (GrappleConfig.getconf().other.dont_override_movement_in_air && !entity.isOnGround() && !was_sliding && !was_wallrunning && !was_rocket && !first_tick_since_created) {
			motion = Vec.motionvec(entity);
			this.unattach();
			return;
		}

		if (this.attached) {
			boolean issliding = ClientProxyInterface.proxy.issliding(entity, motion);
			
			if (issliding && !was_sliding) {
				playSlideSound();
			}
			
			if (this.ignoregroundcounter <= 0) {
				this.normalGround(!issliding);					
				this.normalCollisions(!issliding);
			}

			this.applyAirFriction();
			
			if (this.entity.isInWater()) {
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

			boolean wallrun = this.applywallrun();

			if (!issliding && !was_sliding) {
				if (wallrun) {
					motion = motion.removealong(new Vec(0,1,0));
					if (this.walldirection != null) {
						motion = motion.removealong(this.walldirection);
					}

					Vec new_movement = this.playermovement.changelen(GrappleConfig.getconf().enchantments.wallrun.wallrun_speed*1.5);
					if (this.walldirection != null) {
						new_movement = new_movement.removealong(this.walldirection);
					}
					if (new_movement.length() > GrappleConfig.getconf().enchantments.wallrun.wallrun_speed) {
						new_movement.changelen_ip(GrappleConfig.getconf().enchantments.wallrun.wallrun_speed);
					}
					motion.add_ip(new_movement);
					Vec current_motion_along = this.motion.removealong(new Vec(0,1,0));
					if (this.walldirection != null) {
						current_motion_along = current_motion_along.removealong(this.walldirection);
					}
					if (current_motion_along.length() > GrappleConfig.getconf().enchantments.wallrun.wallrun_max_speed) {
						this.motion.changelen_ip(GrappleConfig.getconf().enchantments.wallrun.wallrun_max_speed);
					}
					additionalmotion.add_ip(wallrun_press_against_wall());
				} else {
					double max_motion = GrappleConfig.getconf().other.airstrafe_max_speed;
					double accel = GrappleConfig.getconf().other.airstrafe_acceleration;
					Vec motion_horizontal = motion.removealong(new Vec(0,1,0));
					double prev_motion = motion_horizontal.length();
					Vec new_motion_horizontal = motion_horizontal.add(this.playermovement.changelen(accel));
					double angle = motion_horizontal.angle(new_motion_horizontal);
					if (new_motion_horizontal.length() > max_motion && new_motion_horizontal.length() > prev_motion) {
						double ninety_deg = Math.PI / 2;
						double new_max_motion = max_motion;
						if (angle < ninety_deg && prev_motion > max_motion) {
							new_max_motion = prev_motion + ((max_motion - prev_motion) * (angle / (Math.PI / 2)));
						}
						new_motion_horizontal.changelen_ip(new_max_motion);
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

			newmotion.setmotion(entity);
			
			this.updateServerPos();
			
			if (entity.isOnGround()) {
				if (!issliding) {
					if (!wallrun) {
						if (!doesrocket) {
							if (ignoregroundcounter <= 0) {
								this.unattach();
							}
						} else {
							motion = Vec.motionvec(entity);
						}
					}
				}
			}
			if (ignoregroundcounter > 0) { ignoregroundcounter--; }
			
			was_sliding = issliding;
			was_wallrunning = wallrun;
			was_rocket = doesrocket;
			first_tick_since_created = false;
		}
	}

	public void receiveEnderLaunch(double x, double y, double z) {
		super.receiveEnderLaunch(x, y, z);
		this.ignoregroundcounter = 2;
	}
	
	public void slidingJump() {
		super.slidingJump();
		this.ignoregroundcounter = 2;
	}
	
	public void playSlideSound() {
		ClientProxyInterface.proxy.playSlideSound(this.entity);
	}
}
