package com.yyon.grapplinghook.controllers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.network.GrappleEndMessage;
import com.yyon.grapplinghook.network.PlayerMovementMessage;
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

public class grappleController {
	public int arrowId;
	public int entityId;
	public World world;
	public vec pos;
	
//	public grappleArrow arrow;
	public Entity entity;
	
	public boolean attached = true;
	
	public double r;
	public vec motion;
	
	public double playerforward = 0;
	public double playerstrafe = 0;
	public boolean playerjump = false;
	public vec playermovement = new vec(0,0,0);
	
	public int counter = 0;
	public int ongroundtimer = 0;
	
	public int maxlen;
	
	public grappleController(int arrowId, int entityId, World world, vec pos, int maxlen) {
		System.out.println("GrappleStart " + this.toString());
		
		this.arrowId = arrowId;
		this.entityId = entityId;
		this.world = world;
		this.pos = pos;
		this.maxlen = maxlen;
		
		this.entity = world.getEntityByID(entityId);
		
		this.r = this.pos.sub(vec.positionvec(entity)).length();
		this.motion = vec.motionvec(entity);
		
		this.ongroundtimer = 0;
		
		grapplemod.registerController(entityId, this);
	}
	
	public void unattach() {
		System.out.println("GrappleEnd " + this.toString());
		
		this.attached = false;
		
		grappleArrow arrow = getArrow();
		if (arrow != null) {
			arrow.remove();
		}
		
		grapplemod.unregisterController(this.entityId);
		grapplemod.network.sendToServer(new GrappleEndMessage(this.entityId, this.arrowId));
	}
	
	public grappleArrow getArrow() {
		return (grappleArrow) world.getEntityByID(arrowId);
	}
	
	public void doClientTick() {
		if (this.attached) {
			grapplemod.proxy.getplayermovement(this, this.entityId);
			this.updatePlayerPos();
		}
	}
	
	public void receivePlayerMovementMessage(float strafe,
			float forward, boolean jump) {
		playerforward = forward;
		playerstrafe = strafe;
		playerjump = jump;
		playermovement = new vec(strafe, 0, forward);
		playermovement = playermovement.rotate_yaw((float) (this.entity.rotationYaw * (-Math.PI / 180.0)));
	}
		
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null) {
				if (true) {
					counter++;
					if (counter > 1000) {
						counter = 0;
						System.out.println("pulling " + this.toString());
					}
					
					if (entity.onGround) {
						ongroundtimer = 20;
						if (this.motion.y < 0) {
							this.motion.y = 0;
						}
						
						if (!grapplemod.proxy.isSneaking(entity)) {
							this.motion = vec.motionvec(entity);
						}
					} else {
						if (this.ongroundtimer > 0) {
							ongroundtimer--;
						}
					}
					
					// stop if collided with object
					if (entity.isCollidedHorizontally) {
						if (entity.motionX == 0) {
							this.motion.x = 0;
						}
						if (entity.motionZ == 0) {
							this.motion.z = 0;
						}
					}
					if (entity.isCollidedVertically) {
						if (entity.motionY == 0) {
							this.motion.y = 0;
						}
					}
					
					vec arrowpos = this.pos;//this.getPositionVector();
					vec playerpos = vec.positionvec(entity);
//					Vec3 playermotion = new Vec3(entity.motionX, entity.motionY, entity.motionZ);
					
					vec oldspherevec = playerpos.sub(arrowpos);
					vec spherevec = oldspherevec.changelen(r);
					vec spherechange = spherevec.sub(oldspherevec);
//					Vec3 spherepos = spherevec.add(arrowpos);
					
					vec additionalmotion;
					if (arrowpos.sub(playerpos).length() < this.r) {
						additionalmotion = new vec(0,0,0);
					} else {
						additionalmotion = spherechange;//new Vec3(0,0,0);
					}
					
					double dist = oldspherevec.length();
					
					if (entity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) entity;
						if (playerjump) {
							this.dojump(player, spherevec);
							return;
						} else if (grapplemod.proxy.isSneaking(entity)) {
							if (arrowpos.y > playerpos.y) {
	//							motion = multvec(motion, 0.9);
								vec motiontorwards = spherevec.changelen(-0.1);
								motiontorwards = new vec(motiontorwards.x, 0, motiontorwards.z);
								if (motion.dot(motiontorwards) < 0) {
									motion.add_ip(motiontorwards);
								}
								
								vec newmotion = dampenmotion(motion, motiontorwards);
								motion = new vec(newmotion.x, motion.y, newmotion.z);
	//							motion = multvec(motion, 0.98);
								
								if (this.playerforward != 0) {
										if (dist < maxlen || this.playerforward > 0 || maxlen == 0) {
//											double motionup = this.playerforward;
											additionalmotion = new vec(0, this.playerforward, 0);
//											this.r = dist;
											this.r = dist;
											this.r -= this.playerforward*0.3;
											if (this.r < 0) {
												this.r = dist;
											}
										}
								}
							}
						} else {
							motion.add_ip(this.playermovement.changelen(0.01));
						}
					}
						
					if (!entity.onGround) {
						motion.add_ip(0, -0.05, 0);
					}
					
					vec newmotion = motion.add(additionalmotion);
					
					if (arrowpos.sub(playerpos.add(motion)).length() > r) { // moving away
						motion = motion.removealong(spherevec);
					}
					
//					entity.setVelocity(newmotion.xCoord, newmotion.yCoord, newmotion.zCoord);
					entity.motionX = newmotion.x;
					entity.motionY = newmotion.y;
					entity.motionZ = newmotion.z;
					
//					if (entity instanceof EntityPlayerMP) {
						
//						((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
						
						/*
						counter++;
						if (counter > 100) {
							counter = 0;
							grapplemod.network.sendTo(new PlayerPosMessage(entity.getEntityId(), entity.posX, entity.posY, entity.posZ), (EntityPlayerMP) entity);
						}
						*/
//					}
					
//					entity.fallDistance = 0;
					
					this.updateServerPos();
				}
			}
		}
	}
	
	public void dojump(Entity player, vec spherevec) {
		if (ongroundtimer > 0) { // on ground: jump normally
			return;
		}
		
		double maxjump = 1;
		vec jump = new vec(0, maxjump, 0);
		jump = jump.proj(spherevec);
		double jumppower = jump.y;
		System.out.println("JUMP");
		System.out.println(jumppower);
		if (jumppower < 0) {
			jumppower = 0;
		}
		if (spherevec.y > 0) {
			jumppower = 0;
		}
		if (player.isCollided) {
			jumppower = maxjump;
		}
		if (r < 5) {
			jumppower = maxjump;
		}
		if (player.onGround) {
			jumppower = 0;
		}
		System.out.println(jumppower);
		
		this.unattach();
		
//		player.ocity(player.motionX, player.motionY + jumppower, player.motionZ);
		if (jumppower > 0) {
			if (jumppower > player.motionY) {
				player.motionY = jumppower;
			} else {
				player.motionY += jumppower;
			}
		}
//		if (entity instanceof EntityPlayerMP) {
//			((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
//		}
		
		this.updateServerPos();
		
		return;
	}

	public vec dampenmotion(vec motion, vec forward) {
		vec newmotion = motion.proj(forward);
		double dampening = 0.05;
		return new vec(newmotion.x*dampening + motion.x*(1-dampening), newmotion.y*dampening + motion.y*(1-dampening), newmotion.z*dampening + motion.z*(1-dampening));
	}
	
	public void updateServerPos() {
		grapplemod.network.sendToServer(new PlayerMovementMessage(this.entityId, this.entity.posX, this.entity.posY, this.entity.posZ, this.entity.motionX, this.entity.motionY, this.entity.motionZ));
	}
	
	// Vector stuff:
	
	public void receiveGrappleClick(boolean leftclick) {
		if (!leftclick) {
			this.unattach();
		}
	}

	public void receiveEnderLaunch(double x, double y, double z) {
		this.motion.add_ip(x, y, z);
		this.entity.motionX = this.motion.x;
		this.entity.motionY = this.motion.y;
		this.entity.motionZ = this.motion.z;
		
	}
}
