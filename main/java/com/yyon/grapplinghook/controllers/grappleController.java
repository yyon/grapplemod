package com.yyon.grapplinghook.controllers;

import java.util.HashSet;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.network.GrappleEndMessage;
import com.yyon.grapplinghook.network.PlayerMovementMessage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
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
//	public int arrowId;
	public int entityId;
	public World world;
//	public vec pos;
	
//	public grappleArrow arrow;
	public Entity entity;
	
	public HashSet<grappleArrow> arrows = new HashSet<grappleArrow>();
	public HashSet<Integer> arrowIds = new HashSet<Integer>();
	
	public boolean attached = true;
	
//	public double r;
	public vec motion;
	
	public double playerforward = 0;
	public double playerstrafe = 0;
	public boolean playerjump = false;
	public boolean waitingonplayerjump = false;
	public vec playermovement_unrotated = new vec(0,0,0);
	public vec playermovement = new vec(0,0,0);
	
//	public int counter = 0;
	public int ongroundtimer = 0;
	public int maxongroundtimer = 3;
	
	public double maxlen;
	
	public double playermovementmult = 0;
	
	public int controllerid;
	
//	public final double playermovementmult = 0.5;
	
//	public SegmentHandler segmenthandler;
	
	public GrappleCustomization custom = null;
	
	public grappleController(int arrowId, int entityId, World world, vec pos, int controllerid, GrappleCustomization custom) {
		this.entityId = entityId;
		this.world = world;
//		this.pos = pos;
		this.custom = custom;
		
		if (this.custom != null) {
			this.playermovementmult = this.custom.playermovementmult;
			this.maxlen = custom.maxlen;
		}
		
		this.controllerid = controllerid;
		
		this.entity = world.getEntityByID(entityId);
//		grappleArrow arrow = (grappleArrow) world.getEntityByID(arrowId);
//		if (arrow != null) {
//			((grappleArrow) arrow).segmenthandler = this.segmenthandler;
//			this.segmenthandler = ((grappleArrow) arrow).segmenthandler;
//			arrow.r = ((grappleArrow) arrow).segmenthandler.getDist(this.pos, vec.positionvec(entity).add(new vec(0, entity.getEyeHeight(), 0)));
//		}
		
//		this.r = this.pos.sub(vec.positionvec(entity)).length();
		this.motion = vec.motionvec(entity);
		
		this.ongroundtimer = 0;
		
		grapplemod.registerController(this.entityId, this);
		
		if (arrowId != -1) {
			Entity arrowentity = world.getEntityByID(arrowId);
			if (arrowentity != null && !arrowentity.isDead && arrowentity instanceof grappleArrow) {
				this.addArrow((grappleArrow)arrowentity);
			} else {
				this.unattach();
			}
		}
	}
	
	public void unattach() {
		if (grapplemod.controllers.containsValue(this)) {
			this.attached = false;
			
			grapplemod.unregisterController(this.entityId);
			
			if (this.controllerid != grapplemod.AIRID) {
				grapplemod.network.sendToServer(new GrappleEndMessage(this.entityId, this.arrowIds));
				grapplemod.createControl(grapplemod.AIRID, -1, this.entityId, this.entity.world, new vec(0,0,0), null, null);
			}
		}
	}
	
/*	public grappleArrow getArrow() {
		return (grappleArrow) world.getEntityByID(arrowId);
	}*/
	
	public void doClientTick() {
		if (this.attached) {
			if (this.entity == null || this.entity.isDead) {
				this.unattach();
			} else {
				grapplemod.proxy.getplayermovement(this, this.entityId);
				this.updatePlayerPos();
			}
		}
	}
		
	public void receivePlayerMovementMessage(float strafe,
			float forward, boolean jump) {
		playerforward = forward;
		playerstrafe = strafe;
		if (!jump) {
			playerjump = false;
		} else if (jump && !playerjump) {
			playerjump = true;
			waitingonplayerjump = true;
		}
		playermovement_unrotated = new vec(strafe, 0, forward);
		playermovement = playermovement_unrotated.rotate_yaw((float) (this.entity.rotationYaw * (Math.PI / 180.0)));
	}
	
	public boolean isjumping() {
		if (playerjump && waitingonplayerjump) {
			waitingonplayerjump = false;
			return true;
		}
		return false;
	}
		
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null) {
				if (true) {
					this.normalGround();
					this.normalCollisions();
//					this.applyAirFriction();
					
					vec playerpos = vec.positionvec(entity);
					playerpos = playerpos.add(new vec(0, entity.getEyeHeight(), 0));
					

//					Vec3 playermotion = new Vec3(entity.motionX, entity.motionY, entity.motionZ);
					
					vec additionalmotion = new vec(0,0,0);;
					
					vec gravity = new vec(0, -0.05, 0);

					
					if (!(this.ongroundtimer > 0)) {
						motion.add_ip(gravity);
					}
					
					boolean doJump = false;
					double jumpSpeed = 0;
					boolean isClimbing = false;
					
					boolean motor = false;
					if (this.custom.motor) {
						if (grapplemod.proxy.isSneaking(entity) && this.custom.motorwhencrouching) {
							motor = true;
						} else if (!grapplemod.proxy.isSneaking(entity) && this.custom.motorwhennotcrouching) {
							motor = true;
						}
					}
					
					double curspeed = 0;
					boolean close = false;
					vec averagemotiontowards = new vec(0, 0, 0);
					
					for (grappleArrow arrow : this.arrows) {
						vec arrowpos = vec.positionvec(arrow);//this.getPositionVector();
						
						vec anchor = arrow.segmenthandler.getclosest(arrowpos);
						double distToAnchor = arrow.segmenthandler.getDistToAnchor();
						double remaininglength = arrow.r - distToAnchor;
						
						vec oldspherevec = playerpos.sub(anchor);
						vec spherevec = oldspherevec.changelen(remaininglength);
						vec spherechange = spherevec.sub(oldspherevec);
//						Vec3 spherepos = spherevec.add(arrowpos);
						
						if (motor) {
							arrow.r = distToAnchor + oldspherevec.length();
						}
						
						averagemotiontowards.add_ip(spherevec.changelen(-1));
						
						curspeed += motion.proj(oldspherevec).length();
						
						if (oldspherevec.length() < remaininglength) {
						} else {
							// snap to rope length
							if (!motor) {
								additionalmotion = spherechange;
							}
						}
						
						double dist = oldspherevec.length();
						
						this.calctaut(dist, arrow);

						if (entity instanceof EntityPlayer) {
							EntityPlayer player = (EntityPlayer) entity;
							if (this.isjumping()) {
								if (ongroundtimer > 0) { // on ground: jump normally
									
								} else {
									doJump = true;
									jumpSpeed = this.getJumpPower(player, spherevec, arrow);
								}
							} else if (grapplemod.proxy.isSneaking(entity) && !motor) {
								isClimbing = true;
								if (anchor.y > playerpos.y) {
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
//												double motionup = this.playerforward;
												additionalmotion = new vec(0, this.playerforward, 0);
//												this.r = dist;
												arrow.r = dist + distToAnchor;
												arrow.r -= this.playerforward*0.3;
												if (arrow.r < distToAnchor) {
													arrow.r = dist + distToAnchor;
												}
											}
									}
								}
							}
						}
						if (dist < 4) {
							close = true;
						}
						
						if (anchor.sub(playerpos.add(motion)).length() > remaininglength && !motor) { // moving away
							motion = motion.removealong(spherevec);
						}
						
						if (this.custom.phaserope) {
							arrow.segmenthandler.updatepos(arrowpos, playerpos, arrow.r);
						} else {
							arrow.segmenthandler.update(arrowpos, playerpos, arrow.r, false);
						}
					}
					
					curspeed = curspeed / this.arrows.size();
					averagemotiontowards.mult_ip(1 / this.arrows.size());
					
		        	vec facing = new vec(entity.getLookVec()).normalize();

					if (motor) {
						if (close) {
							if (motion.length() > 0.3) {
								motion.mult_ip(0.6);
							}
							
							if (entity.onGround) {
								entity.motionX = 0;
//								entity.motionY = 0;
								entity.motionZ = 0;
								this.updateServerPos();
							}
						}
						
						if (curspeed > this.custom.motormaxspeed) {
							motion.changelen_ip(this.custom.motormaxspeed);
						}
						
						if (this.custom.motordampener && arrows.size() == 1) {
							motion = dampenmotion(motion, averagemotiontowards);
						}
						
						vec totalpull = new vec(0, 0, 0);
						
						double accel = this.custom.motoracceleration / this.arrows.size();
						
						double minabssidewayspull = 999;
						
						boolean firstpull = true;
						boolean pullispositive = true;
						boolean pullissameway = true;
						
						for (grappleArrow arrow : this.arrows) {
							vec arrowpos = vec.positionvec(arrow);//this.getPositionVector();
							vec anchor = arrow.segmenthandler.getclosest(arrowpos);
							vec spherevec = playerpos.sub(anchor);
							vec pull = spherevec.mult(-1);
							
							if (pull.dot(facing) > 0 || this.custom.pullbackwards) {
								if (this.custom.smartdoublemotor && this.custom.doublehook) {
									vec facingxy = new vec(facing.x, 0, facing.z);
									vec pullxy = new vec(pull.x, 0, pull.z);
									vec sideways = pullxy.removealong(facing);
									double sidewayspull = facingxy.cross(sideways).y;
									if (Math.abs(sidewayspull) < minabssidewayspull) {
										minabssidewayspull = Math.abs(sidewayspull);
									}
									
									if (firstpull) {
										firstpull = false;
										pullispositive = (sidewayspull >= 0);
									} else {
										if (pullispositive != (sidewayspull >= 0)) {
											pullissameway = false;
										}
									}
								}
								
								arrow.pull = accel;
								
								totalpull.add_ip(pull.changelen(accel));
							}
						}
						
						if (this.custom.smartdoublemotor && this.custom.doublehook) {
							totalpull = new vec(0, 0, 0);
							
							for (grappleArrow arrow : this.arrows) {
								vec arrowpos = vec.positionvec(arrow);//this.getPositionVector();
								vec anchor = arrow.segmenthandler.getclosest(arrowpos);
								vec spherevec = playerpos.sub(anchor);
								vec pull = spherevec.mult(-1);
								
								if (pull.dot(facing) > 0 || this.custom.pullbackwards) {
									vec facingxy = new vec(facing.x, 0, facing.z);
									vec pullxy = new vec(pull.x, 0, pull.z);
									vec sideways = pullxy.removealong(facing);
									double sidewayspull = facingxy.cross(sideways).y;
									
									if (pullissameway) {
										// only 1 rope pulls
										if (Math.abs(sidewayspull) > minabssidewayspull+0.05) {
											arrow.pull = 0;
										}
									} else {
										arrow.pull = arrow.pull * minabssidewayspull / Math.abs(sidewayspull);
									}
									totalpull.add_ip(pull.changelen(arrow.pull));
								}
							}
						}
						
						double pullmult = 1;
						if (this.custom.smartmotor && totalpull.y > 0) {
							double pullxz = new vec(totalpull.x, 0, totalpull.z).length();
							double facingxz = new vec(facing.x, 0, facing.z).length();//.proj(ropexz);
//							double facinglmult = (totalpull.length()) / (facing.length());
//							double pulll = gravity.y / (facing.y * facinglmult - totalpull.y);
							
							
							
							// (newpully + gravityy) / newpullx = facingy / facingxz
							// newmotiony = totalpully * pullmult
							// newpullxz = pullxz * pullmult
							// (totalpully * pullmult + gravityy) / (pullxz * pullmult) = facingy / facingxz
							// (y          * m        + g       ) / (x      * m       ) = f       / w
							// pullmult = (gravityy * facingxz) / (facingy * pullxz - facingxz * totalpully)
							
							pullmult = (gravity.y * facingxz) / (facing.y * pullxz - facingxz * totalpull.y);
									
							double pulll = pullmult * totalpull.length();
							
							if (pulll > this.custom.motoracceleration) {
								pulll = this.custom.motoracceleration;
							}
							if (pulll < 0) {
								pulll = 0;
							}
							
							pullmult = pulll / totalpull.length();
						}
						
						for (grappleArrow arrow : this.arrows) {
							vec arrowpos = vec.positionvec(arrow);//this.getPositionVector();
							vec anchor = arrow.segmenthandler.getclosest(arrowpos);
							vec spherevec = playerpos.sub(anchor);
							vec pull = spherevec.mult(-1);
							
//							System.out.println(arrow.pull);
							
							if (pull.dot(facing) > 0 || this.custom.pullbackwards) {
								if (arrow.pull > 0) {
									pull.changelen_ip(arrow.pull * pullmult);
									
									motion.add_ip(pull);
								}
							}
						}

					}
					
					if (this.custom.repel) {
						vec blockpush = check_repel(playerpos, entity.world);
						blockpush.mult_ip(this.custom.repelforce * 0.5);
						blockpush = new vec(blockpush.x*0.5, blockpush.y*2, blockpush.z*0.5);
						this.motion.add_ip(blockpush);
					}
					
					if (!doJump && !isClimbing) {
						applyPlayerMovement();
					}

					if (doJump) {
						if (jumpSpeed <= 0) {
							jumpSpeed = 0;
						}
						if (jumpSpeed > 1) {
							jumpSpeed = 1;
						}
						this.doJump(entity, jumpSpeed);
						return;
					}
					
					
						
					vec newmotion = motion.add(additionalmotion);
					
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
	
	public void calctaut(double dist, grappleArrow arrow) {
		if (arrow != null) {
    		if (dist < arrow.r) {
    			double taut = 1 - ((arrow.r - dist) / 5);
    			if (taut < 0) {
    				taut = 0;
    			}
    			arrow.taut = taut;
    		} else {
    			arrow.taut = 1;
    		}
    	}
	}

	public void normalCollisions() {
		// stop if collided with object
		if (entity.collidedHorizontally) {
			if (entity.motionX == 0) {
				this.motion.x = 0;
			}
			if (entity.motionZ == 0) {
				this.motion.z = 0;
			}
		}
		if (entity.collidedVertically) {
			if (entity.motionY == 0) {
				this.motion.y = 0;
			}
		}
	}

	public void normalGround() {
		if (entity.onGround) {
			ongroundtimer = maxongroundtimer;
			if (this.motion.y < 0) {
				this.motion.y = 0;
			}
		} else {
			if (this.ongroundtimer > 0) {
				ongroundtimer--;
			}
		}
		if (this.ongroundtimer > 0) {
			if (!grapplemod.proxy.isSneaking(entity)) {
				this.motion = vec.motionvec(entity);
			}
		}
	}

	public double getJumpPower(Entity player, double jumppower) {
		double maxjump = 1;
		if (ongroundtimer > 0) { // on ground: jump normally
			ongroundtimer = 20;
			return 0;
		}
		if (player.onGround) {
			jumppower = 0;
		}
		if (player.collided) {
			jumppower = maxjump;
		}
		if (jumppower < 0) {
			jumppower = 0;
		}
		
		return jumppower;
	}
	
	public void doJump(Entity player, double jumppower) {
		if (jumppower > 0) {
			if (jumppower > player.motionY + jumppower) {
				player.motionY = jumppower;
			} else {
				player.motionY += jumppower;
			}
		}
		
		this.unattach();
		
		this.updateServerPos();
	}
	
	public double getJumpPower(Entity player, vec spherevec, grappleArrow arrow) {
		double maxjump = 1;
		vec jump = new vec(0, maxjump, 0);
		if (spherevec != null) {
			jump = jump.proj(spherevec);
		}
		double jumppower = jump.y;
		
		if (spherevec != null && spherevec.y > 0) {
			jumppower = 0;
		}
		if ((arrow != null) && arrow.r < 1 && (player.posY < arrow.posY)) {
			jumppower = maxjump;
		}
		
		return this.getJumpPower(player, jumppower);
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
	
	public void applyAirFriction() {
		double vel = this.motion.length();
		double dragforce = vel*vel / 200;
		
		vec airfric = new vec(this.motion.x, this.motion.y, this.motion.z);
		airfric.changelen_ip(-dragforce);
		this.motion.add_ip(airfric);
	}
	
	public void applyPlayerMovement() {
		motion.add_ip(this.playermovement.changelen(0.015 + motion.length() * 0.01).mult(this.playermovementmult));//0.02 * playermovementmult));
	}

	public void addArrow(grappleArrow arrow) {
		this.arrows.add(arrow);
		arrow.r = ((grappleArrow) arrow).segmenthandler.getDist(vec.positionvec(arrow), vec.positionvec(entity).add(new vec(0, entity.getEyeHeight(), 0)));
		this.arrowIds.add(arrow.getEntityId());
	}
	
    public double repelmaxpush = 0.3;//0.25;
	
    // repel stuff
    public vec check_repel(vec p, World w) {
//    	long startTime = System.nanoTime();
    	
    	p = p.add(0.0, 0.75, 0.0);
    	vec v = new vec(0, 0, 0);
    	
    	/*
    	for (int x = (int)p.x - radius; x <= (int)p.x + radius; x++) {
        	for (int y = (int)p.y - radius; y <= (int)p.y + radius; y++) {
            	for (int z = (int)p.z - radius; z <= (int)p.z + radius; z++) {
			    	BlockPos pos = new BlockPos(x, y, z);
			    	if (pos != null) {
				    	if (hasblock(pos, w)) {
				    		vec blockvec = new vec(((double) x)+0.5, ((double) y)+0.5, ((double) z)+0.5);
				    		blockvec.sub_ip(p);
				    		blockvec.changelen_ip(-1 / Math.pow(blockvec.length(), 2));
				    		v.add_ip(blockvec);
				    	}
			    	}
            	}
	    	}
    	}
    	*/
    	
    	double t = (1.0 + Math.sqrt(5.0)) / 2.0;
    	
		BlockPos pos = new BlockPos(Math.floor(p.x), Math.floor(p.y), Math.floor(p.z));
		if (hasblock(pos, w)) {
			v.add_ip(0, 1, 0);
		} else {
	    	v.add_ip(vecdist(p, new vec(-1,  t,  0), w));
	    	v.add_ip(vecdist(p, new vec( 1,  t,  0), w));
	    	v.add_ip(vecdist(p, new vec(-1, -t,  0), w));
	    	v.add_ip(vecdist(p, new vec( 1, -t,  0), w));
	    	v.add_ip(vecdist(p, new vec( 0, -1,  t), w));
	    	v.add_ip(vecdist(p, new vec( 0,  1,  t), w));
	    	v.add_ip(vecdist(p, new vec( 0, -1, -t), w));
	    	v.add_ip(vecdist(p, new vec( 0,  1, -t), w));
	    	v.add_ip(vecdist(p, new vec( t,  0, -1), w));
	    	v.add_ip(vecdist(p, new vec( t,  0,  1), w));
	    	v.add_ip(vecdist(p, new vec(-t,  0, -1), w));
	    	v.add_ip(vecdist(p, new vec(-t,  0,  1), w));
		}
    	
    	if (v.length() > repelmaxpush) {
    		v.changelen_ip(repelmaxpush);
    	}
    	
//    	long endTime = System.nanoTime();

//    	long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
//    	System.out.println(duration);
    	
		return v;
	}
    
    public vec vecdist(vec p, vec v, World w) {
    	for (double i = 0.5; i < 10; i += 0.5) {
    		vec v2 = v.changelen(i);
    		BlockPos pos = new BlockPos(Math.floor(p.x + v2.x), Math.floor(p.y + v2.y), Math.floor(p.z + v2.z));
    		if (hasblock(pos, w)) {
    			vec v3 = new vec(pos.getX() + 0.5 - p.x, pos.getY() + 0.5 - p.y, pos.getZ() + 0.5 - p.z);
    			v3.changelen_ip(-1 / Math.pow(v3.length(), 2));
    			return v3;
    		}
    	}
    	
    	return new vec(0, 0, 0);
    }
    
	public boolean hasblock(BlockPos pos, World w) {
//    	if (!blockcache.containsKey(pos)) {
    		boolean isblock = false;
	    	IBlockState blockstate = w.getBlockState(pos);
	    	Block b = blockstate.getBlock();
	    	if (!(b.isAir(blockstate, w, pos))) {
	    		isblock = true;
	    	}
			
//			blockcache.put(pos, (Boolean) isblock);
	    	return isblock;
//    	} else {
//    		return blockcache.get(pos);
//    	}
	}

}
