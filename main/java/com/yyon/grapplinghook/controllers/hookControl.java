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

public class hookControl extends grappleController {
	public hookControl(int arrowId, int entityId, World world, vec pos, int maxlen) {
		super(arrowId, entityId, world, pos, maxlen);
	}

	public double maxspeed = 4;
	public double acceleration = 0.2;
	public float oldstepheight;
	
	@Override
	public void updatePlayerPos() {
		
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null && entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (true) {
					
					vec arrowpos = this.pos;
					vec playerpos = vec.positionvec(player);
					
					vec oldspherevec = playerpos.sub(arrowpos);
					vec spherevec = oldspherevec.changelen(r);
					
					double dist = oldspherevec.length();
					
					if (playerjump) {
						this.dojump(player, spherevec);
						return;
					} else {
						motion.add_ip(this.playermovement.changelen(0.01));
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
					
					motion.add_ip(arrowpos.sub(playerpos).changelen(acceleration));
					
					double speed = motion.proj(oldspherevec).length();
					
					if (speed > maxspeed) {
						motion.changelen_ip(maxspeed);
					}
					
					
					newmotion = motion;
					
					vec motiontorwards = spherevec.changelen(-1);
					motion = dampenmotion(motion, motiontorwards);
					
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
