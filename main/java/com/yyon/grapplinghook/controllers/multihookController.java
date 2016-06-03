package com.yyon.grapplinghook.controllers;

import java.util.HashSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.entities.multihookArrow;

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

public class multihookController extends grappleController {
	public multihookController(int arrowId, int entityId, World world, vec pos, int maxlen, int id) {
		super(arrowId, entityId, world, pos, maxlen, id);
	}

	public double maxspeed = 2;//4;
	public double acceleration = 0.1;//0.2;
	public float oldstepheight;
	public HashSet<multihookArrow> arrows = new HashSet<multihookArrow>();
	
	@Override
	public void updatePlayerPos() {
		Entity entity = this.entity;
		if (this.attached) {
			if(entity != null && entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (true) {
					
					vec playerpos = vec.positionvec(player);
					
		        	vec facing = new vec(player.getLookVec());
					
					if (playerjump) {
						vec jumpvec = new vec(0,0,0);
						for (multihookArrow arrow : this.arrows) {
							vec arrowpos = vec.positionvec(arrow);
							vec spherevec = arrowpos.sub(playerpos);
							if (spherevec.y > 0) {
								jumpvec.add_ip(spherevec);
							}
						}
						if (jumpvec.y > 0) {
							jumpvec = null;
						}
						
						this.dojump(player, jumpvec);
						return;
					} else {
						motion.add_ip(this.playermovement.changelen(0.01));
					}
					
					vec newmotion;
					
					vec pulldirection = facing.changelen(acceleration);
					vec pullforce = new vec(0, 0, 0);
					
//					vec cantpullalong = new vec(pulldirection.x, pulldirection.y, pulldirection.z);
					
					for (multihookArrow arrow : this.arrows) {
						vec arrowpos = vec.positionvec(arrow);
						vec spherevec = arrowpos.sub(playerpos);
						
						vec pull = pulldirection.proj(spherevec);
						
						if (spherevec.dot(pulldirection) < 0) {
							pull.mult_ip(-1);
						}
						
						pullforce.add_ip(pull);
						
//						cantpullalong = cantpullalong.removealong(spherevec);
					}
					
//					cantpullalong.print();
					
//					pulldirection.sub_ip(cantpullalong);
					
					/*
					for (multihookArrow arrow : this.arrows) {
						vec arrowpos = vec.positionvec(arrow);
						vec spherevec = arrowpos.sub(playerpos);
						
						pullforce.add_ip(spherevec.changelen(acceleration).proj(pulldirection));
					}
					*/
					
					motion.add_ip(pullforce);
					
//					facing.normalize().print();
//					pullforce.normalize().print();
//					pulldirection.normalize().print();
					
					
					if (!entity.onGround) {
						motion.add_ip(0, -0.05, 0);
					}
					
					motion = dampenmotion(motion, facing);
					
					this.applyAirFriction();
					
					if (entity.onGround) {
						motion.x *= 0.9;
						motion.z *= 0.9;
					}
					
					double speed = motion.length();
					
					if (speed > maxspeed) {
						motion.changelen_ip(maxspeed);
					}
					
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

	public void addArrow(multihookArrow arrow, vec pos) {
		this.arrows.add(arrow);
	}
	
	public void unattach() {
		super.unattach();
	}
}
