package com.yyon.grapplinghook;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
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

public class grappleController {
	public int arrowId;
	public int entityId;
	public World world;
	public Vec3 pos;
	
//	public grappleArrow arrow;
	public Entity entity;
	
	public boolean attached = true;
	
	public double r;
	public Vec3 motion;
	
	public double playerforward = 0;
	public double playerstrafe = 0;
	public boolean playerjump = false;
	public Vec3 playermovement = Vec3.createVectorHelper(0,0,0);
	
	public int counter = 0;
	public int ongroundtimer = 0;
	
	public int maxlen;
	
	public grappleController(int arrowId, int entityId, World world, Vec3 pos, int maxlen) {
		System.out.println("GrappleStart " + this.toString());
		
		this.arrowId = arrowId;
		this.entityId = entityId;
		this.world = world;
		this.pos = pos;
		this.maxlen = maxlen;
		
		this.entity = world.getEntityByID(entityId);
		
		this.r = subvec(this.pos, Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ)).lengthVector();
		this.motion = Vec3.createVectorHelper(this.entity.motionX, this.entity.motionY, this.entity.motionZ);
		
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
		playermovement = Vec3.createVectorHelper(strafe, 0, forward);
		playermovement.rotateAroundY((float) (this.entity.rotationYaw * (-Math.PI / 180.0)));
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
						if (this.motion.yCoord < 0) {
							this.motion = Vec3.createVectorHelper(this.motion.xCoord, 0, this.motion.zCoord);
						}
					} else {
						if (this.ongroundtimer > 0) {
							ongroundtimer--;
						}
					}
					
					Vec3 arrowpos = this.pos;//this.getPositionVector();
					Vec3 playerpos = Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ);
//					Vec3 playermotion = new Vec3(entity.motionX, entity.motionY, entity.motionZ);
					
					Vec3 oldspherevec = subvec(playerpos, arrowpos);
					Vec3 spherevec = changelen(oldspherevec, r);
					Vec3 spherechange = subvec(spherevec, oldspherevec);
//					Vec3 spherepos = spherevec.add(arrowpos);
					
					spherevec = multvec(spherevec, -1);
					
					Vec3 additionalmotion;
					if (subvec(arrowpos, playerpos).lengthVector() < this.r) {
						additionalmotion = Vec3.createVectorHelper(0,0,0);
					} else {
						additionalmotion = spherechange;//new Vec3(0,0,0);
					}
					
					double dist = oldspherevec.lengthVector();
					
					if (entity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) entity;
						if (playerjump) {
							this.dojump(player, spherevec);
							return;
						} else if (entity.isSneaking()) {
							if (arrowpos.yCoord > playerpos.yCoord) {
	//							motion = multvec(motion, 0.9);
								Vec3 motiontorwards = changelen(spherevec, 0.1);
								motiontorwards = Vec3.createVectorHelper(motiontorwards.xCoord, 0, motiontorwards.zCoord);
								if (motion.dotProduct(motiontorwards) < 0) {
									motion = addvec(motion, motiontorwards);
								}
								
								Vec3 newmotion = dampenmotion(motion, motiontorwards);
								motion = Vec3.createVectorHelper(newmotion.xCoord, motion.yCoord, newmotion.zCoord);
	//							motion = multvec(motion, 0.98);
								
								if (this.playerforward != 0) {
									if (dist < maxlen || this.playerforward > 0 || maxlen == 0) {
										additionalmotion = Vec3.createVectorHelper(0, this.playerforward, 0);
										this.r = dist;
									}
								}
							}
						} else {
							motion = addvec(motion, changelen(this.playermovement, 0.01));
						}
					}
						
					if (!entity.onGround) {
						motion = motion.addVector(0, -0.05, 0);
					}
					
					Vec3 newmotion = addvec(motion, additionalmotion);
					
					if (subvec(arrowpos, addvec(playerpos, motion)).lengthVector() > r) { // moving away
						motion = removealong(motion, spherevec);
					}
					
//					entity.setVelocity(newmotion.xCoord, newmotion.yCoord, newmotion.zCoord);
					entity.motionX = newmotion.xCoord;
					entity.motionY = newmotion.yCoord;
					entity.motionZ = newmotion.zCoord;
					
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
	
	public void dojump(Entity player, Vec3 spherevec) {
		if (ongroundtimer > 0) { // on ground: jump normally
			return;
		}
		
		double maxjump = 1;
		Vec3 jump = Vec3.createVectorHelper(0, maxjump, 0);
		jump = proj(jump, spherevec);
		double jumppower = jump.yCoord;
		System.out.println("JUMP");
		System.out.println(jumppower);
		if (jumppower < 0) {
			jumppower = 0;
		}
		if (spherevec.yCoord > 0) {
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

	public Vec3 dampenmotion(Vec3 motion, Vec3 forward) {
		Vec3 newmotion = proj(motion, forward);
		double dampening = 0.05;
		return Vec3.createVectorHelper(newmotion.xCoord*dampening + motion.xCoord*(1-dampening), newmotion.yCoord*dampening + motion.yCoord*(1-dampening), newmotion.zCoord*dampening + motion.zCoord*(1-dampening));
	}
	
	public void updateServerPos() {
		grapplemod.network.sendToServer(new PlayerMovementMessage(this.entityId, this.entity.posX, this.entity.posY, this.entity.posZ, this.entity.motionX, this.entity.motionY, this.entity.motionZ));
	}
	
	// Vector stuff:
	
	public Vec3 proj (Vec3 a, Vec3 b) {
		b = b.normalize();
		double dot = a.dotProduct(b);
		return changelen(b, dot);
	}
	
	public Vec3 removealong(Vec3 a, Vec3 b) {
		return subvec(a, (proj(a, b)));
	}
	
	public Vec3 multvec(Vec3 a, double changefactor) {
		return Vec3.createVectorHelper(a.xCoord * changefactor, a.yCoord * changefactor, a.zCoord * changefactor);
	}
	
	public Vec3 addvec(Vec3 a, Vec3 b) {
//		return Vec3.createVectorHelper(a.xCoord + b.xCoord, a.yCoord + b.yCoord, a.zCoord + b.zCoord);
		return a.addVector(b.xCoord, b.yCoord, b.zCoord);
	}
	
	public Vec3 changelen(Vec3 a, double l) {
		double oldl = a.lengthVector();
		if (oldl != 0) {
			double changefactor = l / oldl;
			return multvec(a, changefactor);
		} else {
			return a;
		}
	}
	
	public void printvec(Vec3 a) {
		System.out.printf("%f %f %f\n", a.xCoord, a.yCoord, a.zCoord);
	}

	public void receiveGrappleClick(boolean leftclick) {
		if (!leftclick) {
			this.unattach();
		}
	}

	public void receiveEnderLaunch(double x, double y, double z) {
		System.out.println("wrong!");
	}
	public Vec3 subvec(Vec3 a, Vec3 b) {
		return Vec3.createVectorHelper(a.xCoord - b.xCoord, a.yCoord - b.yCoord, a.zCoord - b.zCoord);
	}
}
