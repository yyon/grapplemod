package com.yyon.grapplinghook.controllers;

import java.util.HashSet;
import java.util.stream.Stream;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.entities.grapplearrow.GrapplehookEntity;
import com.yyon.grapplinghook.network.GrappleEndMessage;
import com.yyon.grapplinghook.network.PlayerMovementMessage;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.GrapplemodUtils;
import com.yyon.grapplinghook.utils.Vec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
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

public class GrappleController {
//	public int arrowId;
	public int entityId;
	public World world;
//	public vec pos;
	
//	public grappleArrow arrow;
	public Entity entity;
	
	public HashSet<GrapplehookEntity> arrows = new HashSet<GrapplehookEntity>();
	public HashSet<Integer> arrowIds = new HashSet<Integer>();
	
	public boolean attached = true;
	
//	public double r;
	public Vec motion;
	
	public double playerforward = 0;
	public double playerstrafe = 0;
	public boolean playerjump = false;
	public boolean playersneak = false;
//	public boolean waitingonplayerjump = false;
	public Vec playermovement_unrotated = new Vec(0,0,0);
	public Vec playermovement = new Vec(0,0,0);
	
//	public int counter = 0;
	public int ongroundtimer = 0;
	public int maxongroundtimer = 3;
	
	public double maxlen;
	
	public double playermovementmult = 0;
	
	public int controllerid;
	
//	public ClientProxyClass clientproxy = null;
	
//	public final double playermovementmult = 0.5;
	
//	public SegmentHandler segmenthandler;
	
	public GrappleCustomization custom = null;
	
	public GrappleController(int arrowId, int entityId, World world, Vec pos, int controllerid, GrappleCustomization custom) {
		this.entityId = entityId;
		this.world = world;
//		this.pos = pos;
		this.custom = custom;
		
		if (this.custom != null) {
			this.playermovementmult = this.custom.playermovementmult;
			this.maxlen = custom.maxlen;
		}
		
		this.controllerid = controllerid;
		
		this.entity = world.getEntity(entityId);
//		grappleArrow arrow = (grappleArrow) world.getEntity(arrowId);
//		if (arrow != null) {
//			((grappleArrow) arrow).segmenthandler = this.segmenthandler;
//			this.segmenthandler = ((grappleArrow) arrow).segmenthandler;
//			arrow.r = ((grappleArrow) arrow).segmenthandler.getDist(this.pos, vec.positionvec(entity).add(new vec(0, entity.getEyeHeight(), 0)));
//		}
		
//		this.r = this.pos.sub(vec.positionvec(entity)).length();
		this.motion = Vec.motionvec(entity);
		
		// undo friction
		Vec newmotion = new Vec(entity.position().x - entity.xOld, entity.position().y - entity.yOld, entity.position().z - entity.zOld);
		if (newmotion.x/motion.x < 2 && motion.x/newmotion.x < 2 && newmotion.y/motion.y < 2 && motion.y/newmotion.y < 2 && newmotion.z/motion.z < 2 && motion.z/newmotion.z < 2) {
			this.motion = newmotion;
		}
//		double f6 = 0.91F;
//        if (entity.isOnGround())
//        {
//            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain(entity.posX, entity.getEntityBoundingBox().minY - 1.0D, entity.posZ);
//            BlockState underState = this.level.getBlockState(blockpos$pooledmutableblockpos.setPos(entity.posX, entity.getEntityBoundingBox().minY - 1.0D, entity.posZ));
//            f6 = underState.getBlock().getSlipperiness(underState, entity.world, blockpos$pooledmutableblockpos, entity);// * 0.91F;
//        }
//        motion.y /= 0.9800000190734863D;
//        motion.x /= (double)f6;
//        motion.z /= (double)f6;
//        this.applyAirFriction();

		this.ongroundtimer = 0;
				
//		if (grapplemod.proxy instanceof ClientProxyClass) {
//			this.clientproxy = (ClientProxyClass) grapplemod.proxy;
//		}
		
		if (arrowId != -1) {
			Entity arrowentity = world.getEntity(arrowId);
			if (arrowentity != null && arrowentity.isAlive() && arrowentity instanceof GrapplehookEntity) {
				this.addArrow((GrapplehookEntity)arrowentity);
			} else {
				grapplemod.LOGGER.warn("no arrow");
				this.unattach();
			}
		}
		
		if (custom != null && custom.rocket) {
			ClientProxyInterface.proxy.updateRocketRegen(custom.rocket_active_time, custom.rocket_refuel_ratio);
		}
	}
	
	public void unattach() {
		if (ClientProxyInterface.proxy.unregisterController(this.entityId) != null) {
			this.attached = false;
			
			if (this.controllerid != GrapplemodUtils.AIRID) {
				CommonSetup.network.sendToServer(new GrappleEndMessage(this.entityId, this.arrowIds));
				ClientProxyInterface.proxy.createControl(GrapplemodUtils.AIRID, -1, this.entityId, this.entity.level, new Vec(0,0,0), null, this.custom);
			}
		}
	}
	
/*	public grappleArrow getArrow() {
		return (grappleArrow) world.getEntity(arrowId);
	}*/
	
	public void doClientTick() {
		if (this.attached) {
			if (this.entity == null || !this.entity.isAlive()) {
				this.unattach();
			} else {
//				grapplemod.proxy.getplayermovement(this, this.entityId);
				this.updatePlayerPos();
			}
		}
	}
		
	public void receivePlayerMovementMessage(float strafe,
			float forward, boolean jump, boolean sneak) {
		playerforward = forward;
		playerstrafe = strafe;
		playersneak = sneak;
//		if (!jump) {
//			playerjump = false;
//		} else if (jump && !playerjump) {
//			playerjump = true;
//			waitingonplayerjump = true;
//		}
		playermovement_unrotated = new Vec(strafe, 0, forward);
		playermovement = playermovement_unrotated.rotate_yaw((float) (this.entity.yRot * (Math.PI / 180.0)));
	}
	
//	public boolean isjumping() {
//		if (playerjump && waitingonplayerjump) {
//			waitingonplayerjump = false;
//			return true;
//		}
//		return false;
//	}
		
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null) {
				if (true) {
					if (entity.getVehicle() != null) {
						this.unattach();						
						this.updateServerPos();
						return;
					}
					
					this.normalGround(true);
					this.normalCollisions(true);
					this.applyAirFriction();
					
					Vec playerpos = Vec.positionvec(entity);
					playerpos = playerpos.add(new Vec(0, entity.getEyeHeight(), 0));
					

//					Vec3 playermotion = new Vec3(entity.motionX, entity.motionY, entity.motionZ);
					
					Vec additionalmotion = new Vec(0,0,0);
					
					Vec gravity = new Vec(0, -0.05, 0);

					
//					if (!(this.isOnGround()timer > 0)) {
						motion.add_ip(gravity);
//					}
					
					boolean doJump = false;
					double jumpSpeed = 0;
					boolean isClimbing = false;
					
					// is motor active? (check motorwhencrouching / motorwhennotcrouching)
					boolean motor = false;
					if (this.custom.motor) {
						if (ClientProxyInterface.proxy.isKeyDown(ClientProxyInterface.grapplekeys.key_motoronoff) && this.custom.motorwhencrouching) {
							motor = true;
						} else if (!ClientProxyInterface.proxy.isKeyDown(ClientProxyInterface.grapplekeys.key_motoronoff) && this.custom.motorwhennotcrouching) {
							motor = true;
						}
					}
					
//					double curspeed = 0;
					boolean close = false;
					
					Vec averagemotiontowards = new Vec(0, 0, 0);
					
					double min_spherevec_dist = 99999;
					
					for (GrapplehookEntity arrow : this.arrows) {
						Vec arrowpos = Vec.positionvec(arrow);//this.getPositionVector();
						
						// Update segment handler (handles rope bends)
						if (this.custom.phaserope) {
							arrow.segmenthandler.updatepos(arrowpos, playerpos, arrow.r);
						} else {
							arrow.segmenthandler.update(arrowpos, playerpos, arrow.r, false);
						}
						
						// vectors along rope
						Vec anchor = arrow.segmenthandler.getclosest(arrowpos);
						double distToAnchor = arrow.segmenthandler.getDistToAnchor();
						double remaininglength = arrow.r - distToAnchor;
						
						Vec oldspherevec = playerpos.sub(anchor);
						Vec spherevec = oldspherevec.changelen(remaininglength);
						Vec spherechange = spherevec.sub(oldspherevec);
//						Vec3 spherepos = spherevec.add(arrowpos);
						
						if (spherevec.length() < min_spherevec_dist) {min_spherevec_dist = spherevec.length();}
						
						averagemotiontowards.add_ip(spherevec.changelen(-1));
						
						if (motor) {
							arrow.r = distToAnchor + oldspherevec.length();
						}
						
//						averagemotiontowards.add_ip(spherevec.changelen(-1));
						
//						curspeed += motion.proj(oldspherevec).length();
						
						// snap to rope length
						if (oldspherevec.length() < remaininglength) {
						} else {
							if (!motor) {
								if (oldspherevec.length() - remaininglength > GrappleConfig.getconf().grapplinghook.other.rope_snap_buffer) {
									// if rope is too long, the rope snaps
									
									this.unattach();
									
									this.updateServerPos();
									return;
								} else {
									additionalmotion = spherechange;
								}
							}
						}
						
						double dist = oldspherevec.length();
						
						this.calctaut(dist, arrow);

						// handle keyboard input (jumping and climbing)
						if (entity instanceof PlayerEntity) {
							PlayerEntity player = (PlayerEntity) entity;
							boolean isjumping = ClientProxyInterface.proxy.isKeyDown(ClientProxyInterface.grapplekeys.key_jumpanddetach);
							isjumping = isjumping && !playerjump; // only jump once when key is first pressed
							playerjump = ClientProxyInterface.proxy.isKeyDown(ClientProxyInterface.grapplekeys.key_jumpanddetach);
							if (isjumping) {
								// jumping
								if (ongroundtimer > 0) { // on ground: jump normally
									
								} else {
									double timer = ClientProxyInterface.proxy.getTimeSinceLastRopeJump(this.entity.level);
									if (timer > GrappleConfig.getconf().grapplinghook.other.rope_jump_cooldown_s * 20.0) {
										doJump = true;
										jumpSpeed = this.getJumpPower(player, spherevec, arrow);
									}
								}
							}
							if (ClientProxyInterface.proxy.isKeyDown(ClientProxyInterface.grapplekeys.key_slow)) {
								// climbing
	//							motion = multvec(motion, 0.9);
								Vec motiontorwards = spherevec.changelen(-0.1);
								motiontorwards = new Vec(motiontorwards.x, 0, motiontorwards.z);
								if (motion.dot(motiontorwards) < 0) {
									motion.add_ip(motiontorwards);
								}

								Vec newmotion = dampenmotion(motion, motiontorwards);
								motion = new Vec(newmotion.x, motion.y, newmotion.z);
	//							motion = multvec(motion, 0.98);

							}
							if ((ClientProxyInterface.proxy.isKeyDown(ClientProxyInterface.grapplekeys.key_climb) || !this.custom.climbkey) && !motor) {
								isClimbing = true;
								if (anchor.y > playerpos.y) {
									// when shift is pressed, stop swinging
									
									// climb up/down rope
									float playerforward = 0;
									if (ClientProxyInterface.proxy.isKeyDown(ClientProxyInterface.grapplekeys.key_climbup)) { playerforward = 1.0f; }
									else if (ClientProxyInterface.proxy.isKeyDown(ClientProxyInterface.grapplekeys.key_climbdown)) { playerforward = -1.0f; }
									if (playerforward != 0) {
											if (dist + distToAnchor < maxlen || this.playerforward > 0 || maxlen == 0) {
//												double motionup = this.playerforward;
//												additionalmotion = new vec(0, playerforward, 0);
//												additionalmotion.add_ip(spherevec.changelen_ip(playerforward));
//												this.r = dist;
												arrow.r = dist + distToAnchor;
												arrow.r -= playerforward*GrappleConfig.getconf().grapplinghook.other.climb_speed;
												if (arrow.r < distToAnchor) {
													arrow.r = dist + distToAnchor;
												}
												
												Vec additionalmovementdown = spherevec.changelen(-playerforward * GrappleConfig.getconf().grapplinghook.other.climb_speed).proj(new Vec(0,1,0));
												if (additionalmovementdown.y < 0) {
													additionalmotion.add_ip(additionalmovementdown);
												}
											}
									}
								}
							}
						}
						if (dist + distToAnchor < 2) {
							close = true;
						}
						
						// swing along max rope length
						if (anchor.sub(playerpos.add(motion)).length() > remaininglength && !motor) { // moving away
							motion = motion.removealong(spherevec);
						}
					}
					averagemotiontowards.changelen_ip(1);
					
//					curspeed = curspeed / this.arrows.size();
//					averagemotiontowards.mult_ip(1 / this.arrows.size());
					
		        	Vec facing = new Vec(entity.getLookAngle()).normalize();
		        	
		        	// Motor
					if (motor) {
						boolean dopull = true;
						
						// if only one rope is pulling and not oneropepull, disable motor
						if (this.custom.doublehook && this.arrows.size() == 1) {
							boolean isdouble = true;
							for (GrapplehookEntity arrow : this.arrows) {
								if (!arrow.isdouble) {
									isdouble = false;
								}
							}
							if (isdouble && !this.custom.oneropepull) {
								dopull = false;
							}
						}
						
/*						if (curspeed > this.custom.motormaxspeed) {
							motion.changelen_ip(this.custom.motormaxspeed);
						}*/
						
						Vec totalpull = new Vec(0, 0, 0);
						
						double accel = this.custom.motoracceleration / this.arrows.size();
						
						double minabssidewayspull = 999;
//						double maxabssidewayspull = 0;
						
						boolean firstpull = true;
						boolean pullispositive = true;
						boolean pullissameway = true;
						
						// set all motors to maximum pull and precalculate some stuff for smart motor / smart double motor
						for (GrapplehookEntity arrow : this.arrows) {
							Vec arrowpos = Vec.positionvec(arrow);//this.getPositionVector();
							Vec anchor = arrow.segmenthandler.getclosest(arrowpos);
							Vec spherevec = playerpos.sub(anchor);
							Vec pull = spherevec.mult(-1);
							
							arrow.pull = accel;
							
							totalpull.add_ip(pull.changelen(accel));
							
							pull.changelen_ip(arrow.pull);

							// precalculate some stuff for smart double motor
							// For smart double motor: the motors should pull left and right equally
							// one side will be less able to pull to its side due to the angle
							// therefore the other side should slow down in order to match and have both sides pull left/right equally
							// the amount each should pull (the lesser of the two) is minabssidewayspull
							if (pull.dot(facing) > 0 || this.custom.pullbackwards) {
								if (this.custom.smartdoublemotor && this.arrows.size() > 1) {
									Vec facingxy = new Vec(facing.x, 0, facing.z);
									Vec facingside = facingxy.cross(new Vec(0, 1, 0)).normalize();
//									vec pullxy = new vec(pull.x, 0, pull.z);
									Vec sideways = pull.proj(facingside); // pullxy.removealong(facing);
									Vec currentsideways = motion.proj(facingside);
									sideways.add_ip(currentsideways);
									double sidewayspull = sideways.dot(facingside); // facingxy.cross(sideways).y;
									
									if (Math.abs(sidewayspull) < minabssidewayspull) {
										minabssidewayspull = Math.abs(sidewayspull);
									}
/*									if (Math.abs(sidewayspull) > maxabssidewayspull) {
										maxabssidewayspull = Math.abs(sidewayspull);
									}*/
									
									if (firstpull) {
										firstpull = false;
										pullispositive = (sidewayspull >= 0);
									} else {
										if (pullispositive != (sidewayspull >= 0)) {
											pullissameway = false;
										}
									}
								}
								
							}
						}
						
						// Smart double motor - calculate the speed each motor should pull at
						if (this.custom.smartdoublemotor && this.arrows.size() > 1) {
							totalpull = new Vec(0, 0, 0);
							
							for (GrapplehookEntity arrow : this.arrows) {
								Vec arrowpos = Vec.positionvec(arrow);//this.getPositionVector();
								Vec anchor = arrow.segmenthandler.getclosest(arrowpos);
								Vec spherevec = playerpos.sub(anchor);
								Vec pull = spherevec.mult(-1);
								pull.changelen_ip(arrow.pull);
								
								if (pull.dot(facing) > 0 || this.custom.pullbackwards) {
									Vec facingxy = new Vec(facing.x, 0, facing.z);
									Vec facingside = facingxy.cross(new Vec(0, 1, 0)).normalize();
//									vec pullxy = new vec(pull.x, 0, pull.z);
									Vec sideways = pull.proj(facingside); // pullxy.removealong(facing);
									Vec currentsideways = motion.proj(facingside);
									sideways.add_ip(currentsideways);
									double sidewayspull = sideways.dot(facingside); // facingxy.cross(sideways).y;
									
									if (pullissameway) {
										// only 1 rope pulls
										if (Math.abs(sidewayspull) > minabssidewayspull+0.05) {
											arrow.pull = 0;
										}
									} else {
										arrow.pull = arrow.pull * minabssidewayspull / Math.abs(sidewayspull);
									}
									totalpull.add_ip(pull.changelen(arrow.pull));
								} else {
									if (arrow.isdouble) {
										if (!this.custom.oneropepull) {
											dopull = false;
										}
									}
								}
							}
						}
						
						// smart motor - angle of motion = angle facing
						// match angle (the ratio of pulling upwards to pulling sideways)
						// between the motion (after pulling and gravity) vector and the facing vector
						// if double hooks, all hooks are scaled by the same amount (to prevent pulling to the left/right)
						double pullmult = 1;
						if (this.custom.smartmotor && totalpull.y > 0 && !(this.ongroundtimer > 0 || entity.isOnGround())) {
							Vec pullxzvector = new Vec(totalpull.x, 0, totalpull.z);
							double pullxz = pullxzvector.length();
							double motionxz = motion.proj(pullxzvector).dot(pullxzvector.normalize());
//							double facingxz = new vec(facing.x, 0, facing.z).length();//.proj(ropexz);
							double facingxz = facing.proj(pullxzvector).dot(pullxzvector.normalize());
//							double facinglmult = (totalpull.length()) / (facing.length());
//							double pulll = gravity.y / (facing.y * facinglmult - totalpull.y);
							
							
							
							// (newpully + gravityy) / newpullx = facingy / facingxz
							// newmotiony = totalpully * pullmult + motion.y
							// newpullxz = pullxz * pullmult + motionxz
							// (totalpully * pullmult + motion.y + gravityy) / (pullxz * pullmult + motionxz) = facingy / facingxz
							// (y          * m        + a        + g       ) / (x      * m        + b       ) = f       / w          (1-letter vars for wolframalpha)
							// m = (w (a + g) - b f)/(f x - w y)
							// pullmult = (facingxz * (motion.y + gravity.y) - motionxz * facing.y)/(facing.y * pullxz - facingxz * totalpull.y)
							
							pullmult = (facingxz * (motion.y + gravity.y) - motionxz * facing.y)/(facing.y * pullxz - facingxz * totalpull.y); // (gravity.y * facingxz) / (facing.y * pullxz - facingxz * totalpull.y);
							
							if ((facing.y * pullxz - facingxz * totalpull.y) == 0) {
								// division by zero
								pullmult = 9999;
							}
									
							double pulll = pullmult * totalpull.length();
							
							if (pulll > this.custom.motoracceleration) {
								pulll = this.custom.motoracceleration;
							}
							if (pulll < 0) {
								pulll = 0;
							}
							
							pullmult = pulll / totalpull.length();
						}
						
						// Prevent motor from moving too fast (motormaxspeed)
						if (this.motion.dot(totalpull) > 0) {
							if (this.motion.proj(totalpull).length() + totalpull.mult(pullmult).length() > this.custom.motormaxspeed) {
								pullmult = (this.custom.motormaxspeed - this.motion.proj(totalpull).length()) / totalpull.length();
								if (pullmult < 0) {
									pullmult = 0;
								}
							}
						}
						
						// sideways dampener
						if (this.custom.motordampener && totalpull.length() != 0) {
							motion = dampenmotion(motion, totalpull);
						}
						
						// actually pull with the motor
						if (dopull) {
							for (GrapplehookEntity arrow : this.arrows) {
								Vec arrowpos = Vec.positionvec(arrow);//this.getPositionVector();
								Vec anchor = arrow.segmenthandler.getclosest(arrowpos);
								Vec spherevec = playerpos.sub(anchor);
								Vec pull = spherevec.mult(-1);
								pull.changelen_ip(arrow.pull * pullmult);
								
//								System.out.print(arrow.pull * pullmult);
//								System.out.print(" ");
								
								if (pull.dot(facing) > 0 || this.custom.pullbackwards) {
									if (arrow.pull > 0) {
										motion.add_ip(pull);
									}
								}
							}
						}
						
						// if player is at the destination, slow down
						if (close && !(this.arrows.size() > 1)) {
							if (entity.horizontalCollision || entity.verticalCollision || entity.isOnGround()) {
								motion.mult_ip(0.6);
							}
						}
//						System.out.println();
					}
					
					// forcefield
					if (this.custom.repel) {
						Vec blockpush = check_repel(playerpos, entity.level);
						blockpush.mult_ip(this.custom.repelforce * 0.5);
						blockpush = new Vec(blockpush.x*0.5, blockpush.y*2, blockpush.z*0.5);
						this.motion.add_ip(blockpush);
					}
					
					// rocket
					if (this.custom.rocket) {
						this.motion.add_ip(this.rocket(entity));
					}
					
					// WASD movement
					if (!doJump && !isClimbing) {
						applyPlayerMovement();
					}
					
					// jump
					if (doJump) {
						if (jumpSpeed <= 0) {
							jumpSpeed = 0;
						}
						if (jumpSpeed > GrappleConfig.getconf().grapplinghook.other.rope_jump_power) {
							jumpSpeed = GrappleConfig.getconf().grapplinghook.other.rope_jump_power;
						}
						this.doJump(entity, jumpSpeed, averagemotiontowards, min_spherevec_dist);
						ClientProxyInterface.proxy.resetRopeJumpTime(this.entity.level);
						return;
					}
					
					// now to actually apply everything to the player
					Vec newmotion = motion.add(additionalmotion);
					
					if (Double.isNaN(newmotion.x) || Double.isNaN(newmotion.y) || Double.isNaN(newmotion.z)) {
						newmotion = new Vec(0, 0, 0);
						motion = new Vec(0, 0, 0);
						System.out.println("error: motion is NaN");
					}
					
//					entity.setVelocity(newmotion.xCoord, newmotion.yCoord, newmotion.zCoord);
					entity.setDeltaMovement(newmotion.x, newmotion.y, newmotion.z);
					
//					if (entity instanceof PlayerEntityMP) {
						
//						((PlayerEntityMP) entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
						
						/*
						counter++;
						if (counter > 100) {
							counter = 0;
							grapplemod.network.sendTo(new PlayerPosMessage(entity.getEntityId(), entity.posX, entity.posY, entity.posZ), (PlayerEntityMP) entity);
						}
						*/
//					}
					
//					entity.fallDistance = 0;
					
					this.updateServerPos();
				}
			}
		}
	}
	
	public void calctaut(double dist, GrapplehookEntity arrow) {
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
	
//	boolean prevcollision = false;
//	vec prevcollisionpos = new vec(0,0,0);

	public void normalCollisions(boolean refreshmotion) {
		// stop if collided with object
		Vec pos = Vec.positionvec(this.entity);
		if (entity.horizontalCollision) {
//			if (refreshmotion || prevcollision) {
				if (entity.getDeltaMovement().x == 0) {
					if (refreshmotion || this.tryStepUp(new Vec(this.motion.x, 0, 0))) {
						this.motion.x = 0;
					}
				}
				if (entity.getDeltaMovement().z == 0) {
					if (refreshmotion || this.tryStepUp(new Vec(0, 0, this.motion.z))) {
						this.motion.z = 0;
					}
				}
//			}
		}
//		prevcollision = entity.collidedHorizontally;
//		if (prevcollision) {
//			if (entity.motionX == 0 || entity.motionZ == 0) {
//				prevcollisionpos = pos;
//			}
//		}
		if (entity.verticalCollision) {
			if (entity.isOnGround()) {
				if (refreshmotion && Minecraft.getInstance().options.keyJump.isDown()) {
					this.motion.y = entity.getDeltaMovement().y;
				} else {
					if (this.motion.y < 0) {
						this.motion.y = 0;
					}
				}
			} else {
				if (this.motion.y > 0) {
					if (entity.yOld == entity.position().y) {
						this.motion.y = 0;
					}
				}
			}
		}
	}
	
	public boolean tryStepUp(Vec collisionmotion) {
		if (collisionmotion.length() == 0) {return false;}
		Vec moveoffset = collisionmotion.changelen(0.05).add(0, entity.maxUpStep+0.01, 0);
		Stream<VoxelShape> collisions = this.entity.level.getCollisions(this.entity, this.entity.getBoundingBox().move(moveoffset.x, moveoffset.y, moveoffset.z), (e) -> false);
		if (collisions.count() == 0) {
			if (!this.entity.isOnGround()) {
				Vec pos = Vec.positionvec(entity);
				pos.add_ip(moveoffset);
				pos.setpos(entity);
				entity.xOld = pos.x;
				entity.yOld = pos.y;
				entity.zOld = pos.z;
			}
			entity.horizontalCollision = false;
			return false;
		}
		return true;
	}
	
	boolean prevonground = false;

	public void normalGround(boolean refreshmotion) {
		if (entity.isOnGround()) {
			ongroundtimer = maxongroundtimer;
//			if (this.motion.y < 0) {
//				this.motion.y = 0;
//			}
		} else {
			if (this.ongroundtimer > 0) {
				ongroundtimer--;
			}
		}
		if (entity.isOnGround() || ongroundtimer > 0) {
			if (refreshmotion) {
				this.motion = Vec.motionvec(entity);
				if (Minecraft.getInstance().options.keyJump.isDown()) {
					this.motion.y += 0.05;
				}
			}
		}
		prevonground = entity.isOnGround();
	}

	private double getJumpPower(Entity player, double jumppower) {
		double maxjump = GrappleConfig.getconf().grapplinghook.other.rope_jump_power;
		if (ongroundtimer > 0) { // on ground: jump normally
			ongroundtimer = 20;
			return 0;
		}
		if (player.isOnGround()) {
			jumppower = 0;
		}
		if (player.horizontalCollision || player.verticalCollision) {
			jumppower = maxjump;
		}
		if (jumppower < 0) {
			jumppower = 0;
		}
		
		return jumppower;
	}
	
	public void doJump(Entity player, double jumppower, Vec averagemotiontowards, double min_spherevec_dist) {
		if (jumppower > 0) {
			if (GrappleConfig.getconf().grapplinghook.other.rope_jump_at_angle && min_spherevec_dist > 1) {
				motion.add_ip(averagemotiontowards.changelen(jumppower));
			} else {
				if (jumppower > player.getDeltaMovement().y + jumppower) {
					motion.y = jumppower;
				} else {
					motion.y += jumppower;
				}
			}
			motion.setmotion(player);
		}
		
		this.unattach();
		
		this.updateServerPos();
	}
	
	public double getJumpPower(Entity player, Vec spherevec, GrapplehookEntity arrow) {
		double maxjump = GrappleConfig.getconf().grapplinghook.other.rope_jump_power;
		Vec jump = new Vec(0, maxjump, 0);
		if (spherevec != null && !GrappleConfig.getconf().grapplinghook.other.rope_jump_at_angle) {
			jump = jump.proj(spherevec);
		}
		double jumppower = jump.y;
		
		if (spherevec != null && spherevec.y > 0) {
			jumppower = 0;
		}
		if ((arrow != null) && arrow.r < 1 && (player.position().y < arrow.position().y)) {
			jumppower = maxjump;
		}

		jumppower = this.getJumpPower(player, jumppower);
		
		double current_speed = GrappleConfig.getconf().grapplinghook.other.rope_jump_at_angle ? -motion.dist_along(spherevec) : motion.y;
		if (current_speed > 0) {
			jumppower = jumppower - current_speed;
		}

		if (jumppower < 0) {jumppower = 0;}

		return jumppower;
	}

	public Vec dampenmotion(Vec motion, Vec forward) {
		Vec newmotion = motion.proj(forward);
		double dampening = 0.05;
		return newmotion.mult(dampening).add(motion.mult(1-dampening));
	}
	
	public void updateServerPos() {
		CommonSetup.network.sendToServer(new PlayerMovementMessage(this.entityId, this.entity.position().x, this.entity.position().y, this.entity.position().z, this.entity.getDeltaMovement().x, this.entity.getDeltaMovement().y, this.entity.getDeltaMovement().z));
	}
	
	// Vector stuff:
	
	public void receiveGrappleDetach() {
		this.unattach();
	}

	public void receiveEnderLaunch(double x, double y, double z) {
		this.motion.add_ip(x, y, z);
		this.motion.setmotion(this.entity);
	}
	
	public void applyAirFriction() {
		double dragforce = 1 / 200F;
		if (this.entity.isInWater()) {
//			this.applyWaterFriction();
			dragforce = 1 / 4F;
		}
		
		double vel = this.motion.length();
		dragforce = vel * dragforce;
		
		Vec airfric = new Vec(this.motion.x, this.motion.y, this.motion.z);
		airfric.changelen_ip(-dragforce);
		this.motion.add_ip(airfric);
	}
	
/*	public void applyWaterFriction() {
		double vel = this.motion.length();
		double dragforce = vel*vel / 4;
		
		vec airfric = new vec(this.motion.x, this.motion.y, this.motion.z);
		airfric.changelen_ip(-dragforce);
		this.motion.add_ip(airfric);
	}*/
	
	public void applyPlayerMovement() {
		motion.add_ip(this.playermovement.changelen(0.015 + motion.length() * 0.01).mult(this.playermovementmult));//0.02 * playermovementmult));
	}

	public void addArrow(GrapplehookEntity arrow) {
		this.arrows.add(arrow);
		arrow.r = ((GrapplehookEntity) arrow).segmenthandler.getDist(Vec.positionvec(arrow), Vec.positionvec(entity).add(new Vec(0, entity.getEyeHeight(), 0)));
		this.arrowIds.add(arrow.getId());
	}
	
    public double repelmaxpush = 0.3;//0.25;
	
    // repel stuff
    public Vec check_repel(Vec p, World w) {
//    	long startTime = System.nanoTime();
    	
    	p = p.add(0.0, 0.75, 0.0);
    	Vec v = new Vec(0, 0, 0);
    	
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
	    	v.add_ip(vecdist(p, new Vec(-1,  t,  0), w));
	    	v.add_ip(vecdist(p, new Vec( 1,  t,  0), w));
	    	v.add_ip(vecdist(p, new Vec(-1, -t,  0), w));
	    	v.add_ip(vecdist(p, new Vec( 1, -t,  0), w));
	    	v.add_ip(vecdist(p, new Vec( 0, -1,  t), w));
	    	v.add_ip(vecdist(p, new Vec( 0,  1,  t), w));
	    	v.add_ip(vecdist(p, new Vec( 0, -1, -t), w));
	    	v.add_ip(vecdist(p, new Vec( 0,  1, -t), w));
	    	v.add_ip(vecdist(p, new Vec( t,  0, -1), w));
	    	v.add_ip(vecdist(p, new Vec( t,  0,  1), w));
	    	v.add_ip(vecdist(p, new Vec(-t,  0, -1), w));
	    	v.add_ip(vecdist(p, new Vec(-t,  0,  1), w));
		}
    	
    	if (v.length() > repelmaxpush) {
    		v.changelen_ip(repelmaxpush);
    	}
    	
//    	long endTime = System.nanoTime();

//    	long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
//    	System.out.println(duration);
    	
		return v;
	}
    
    public Vec vecdist(Vec p, Vec v, World w) {
    	for (double i = 0.5; i < 10; i += 0.5) {
    		Vec v2 = v.changelen(i);
    		BlockPos pos = new BlockPos(Math.floor(p.x + v2.x), Math.floor(p.y + v2.y), Math.floor(p.z + v2.z));
    		if (hasblock(pos, w)) {
    			Vec v3 = new Vec(pos.getX() + 0.5 - p.x, pos.getY() + 0.5 - p.y, pos.getZ() + 0.5 - p.z);
    			v3.changelen_ip(-1 / Math.pow(v3.length(), 2));
    			return v3;
    		}
    	}
    	
    	return new Vec(0, 0, 0);
    }
    
	public boolean hasblock(BlockPos pos, World w) {
//    	if (!blockcache.containsKey(pos)) {
    		boolean isblock = false;
	    	BlockState blockstate = w.getBlockState(pos);
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

	public void receiveGrappleDetachHook(int hookid) {
		if (this.arrowIds.contains(hookid)) {
			this.arrowIds.remove(hookid);
		} else {
			System.out.println("Error: controller received hook detach, but hook id not in arrowIds");
		}
		
		GrapplehookEntity arrowToRemove = null;
		for (GrapplehookEntity arrow : this.arrows) {
			if (arrow.getId() == hookid) {
				arrowToRemove = arrow;
				break;
			}
		}
		
		if (arrowToRemove != null) {
			this.arrows.remove(arrowToRemove);
		} else {
			System.out.println("Error: controller received hook detach, but arrow not in arrows");
		}
	}
	
	public Vec rocket(Entity entity) {
		if (ClientProxyInterface.proxy.isKeyDown(ClientProxyInterface.grapplekeys.key_rocket)) {
			double rocket_force = this.custom.rocket_force * 0.225 * ClientProxyInterface.proxy.getRocketFunctioning();
        	double yaw = entity.yRot;
        	double pitch = -entity.xRot;
        	pitch += this.custom.rocket_vertical_angle;
        	Vec force = new Vec(0, 0, rocket_force);
        	force = force.rotate_pitch(Math.toRadians(pitch));
        	force = force.rotate_yaw(Math.toRadians(yaw));
        	
        	return force;
		}
		return new Vec(0,0,0);
	}
	
	boolean isonwall = false;
	Vec walldirection = null;
	BlockRayTraceResult wallrun_raytrace_result = null;
//	boolean wallonleft = true;
	
	public Vec getnearbywall(Vec tryfirst, Vec trysecond, double extra) {
		float entitywidth = this.entity.getBbWidth();
		
		for (Vec direction : new Vec[] {tryfirst, trysecond, tryfirst.mult(-1), trysecond.mult(-1)}) {
			BlockRayTraceResult raytraceresult = GrapplemodUtils.rayTraceBlocks(this.entity.level, Vec.positionvec(this.entity), Vec.positionvec(this.entity).add(direction.changelen(entitywidth/2 + extra)));
			if (raytraceresult != null) {
				wallrun_raytrace_result = raytraceresult;
				return direction;
			}
		}
		
		return null;
	}
	
	public Vec getwalldirection() {
		Vec tryfirst = new Vec(0, 0, 0);
		Vec trysecond = new Vec(0, 0, 0);
		
		if (Math.abs(this.motion.x) > Math.abs(this.motion.z)) {
			tryfirst.x = (this.motion.x > 0) ? 1 : -1;
			trysecond.z = (this.motion.z > 0) ? 1 : -1;
		} else {
			tryfirst.z = (this.motion.z > 0) ? 1 : -1;
			trysecond.x = (this.motion.x > 0) ? 1 : -1;
		}
		
		return getnearbywall(tryfirst, trysecond, 0.05);
	}
//	
//	public vec getclosebywall() {
//		if (this.walldirection != null) {
//			vec tryfirst = this.walldirection;
//			vec trysecond = new vec(0,0,0);
//			if (tryfirst.x == 0) {
//				trysecond.x = 1;
//			} else {
//				trysecond.z = 1;
//			}
//			
//			vec walldir = getnearbywall(tryfirst, trysecond, 1.05);
//			if (walldir != null && walldir.dot(this.motion) >= 0) {
//				return walldir;
//			}
//		}
//		
//		return null;
//	}
	
	public Vec getcorner(int cornernum, Vec facing, Vec sideways) {
		Vec corner = new Vec(0,0,0);
		if (cornernum / 2 == 0) {
			corner.add_ip(facing);
		} else {
			corner.add_ip(facing.mult(-1));
		}
		if (cornernum % 2 == 0) {
			corner.add_ip(sideways);
		} else {
			corner.add_ip(sideways.mult(-1));
		}
		return corner;
	}
	
	public boolean wallnearby(double dist) {
//		double boxsize = 2;
		
//		// facing 2d
//		vec facing = new vec(this.entity.getLookVec());
//		facing.y = 0;
//		if (facing.length() <= 0.01) {
//			facing = new vec(1, 0, 0);
//		}
//		facing.changelen_ip(boxsize);
//		
//		vec sideways = facing.cross(new vec(0,1,0));
//		sideways.changelen_ip(boxsize);
		
		float entitywidth = this.entity.getBbWidth();
		Vec v1 = new Vec(entitywidth/2 + dist, 0, 0);
		Vec v2 = new Vec(0, 0, entitywidth/2 + dist);
		
		for (int i = 0; i < 4; i++) {
			Vec corner1 = getcorner(i, v1, v2);
			Vec corner2 = getcorner((i + 1) % 4, v1, v2);
			
			BlockRayTraceResult raytraceresult = GrapplemodUtils.rayTraceBlocks(this.entity.level, Vec.positionvec(this.entity).add(corner1), Vec.positionvec(this.entity).add(corner2));
			if (raytraceresult != null) {
				return true;
			}
		}
		
		return false;
	}
	
	public int tickswallrunning = 0;
	int ticks_since_last_wallrun_sound_effect = 0;
	
	public boolean iswallrunning() {
		double current_speed = Math.sqrt(Math.pow(this.motion.x, 2) + Math.pow(this.motion.z,  2));
		if (current_speed < GrappleConfig.getconf().enchantments.wallrun.wallrun_min_speed) {
			isonwall = false;
			return false;
		}
		
		if (isonwall) {
			tickswallrunning += 1;
		}
		
		if (tickswallrunning < GrappleConfig.getconf().enchantments.wallrun.max_wallrun_time * 40) {
			if (!(playersneak)) {
				// continue wallrun
				if (isonwall && !this.entity.isOnGround() && this.entity.horizontalCollision) {
					return true;
				}
				
				// start wallrun
				if (ClientProxyInterface.proxy.iswallrunning(this.entity, this.motion)) {
					isonwall = true;
					return true;
				}
			}
			
			isonwall = false;
		}
		
		if (tickswallrunning > 0 && (this.entity.isOnGround() || (!this.entity.horizontalCollision && !wallnearby(0.2)))) {
			tickswallrunning = 0;
			ticks_since_last_wallrun_sound_effect = 0;
		}
		
		return false;
	}
	
	public boolean applywallrun() {
		boolean wallrun = this.iswallrunning();
		
		if (playerjump) {
			if (wallrun) {
				return false;
			} else {
				playerjump = false;
			}
		}
		
		if (wallrun && !ClientProxyInterface.proxy.isKeyDown(ClientProxyInterface.grapplekeys.key_jumpanddetach)) {

			Vec wallside = this.getwalldirection();
			if (wallside != null) {
				this.walldirection = wallside;
			}
			
			if (this.walldirection == null) {
				return false;
			}

			if (!playerjump) {
				motion.y = 0;
			}

			// drag
			double dragforce = GrappleConfig.getconf().enchantments.wallrun.wallrun_drag;
			
			double vel = this.motion.length();
//			dragforce = vel*vel * dragforce;
			
			if (dragforce > vel) {dragforce = vel;}
			
			Vec wallfric = new Vec(this.motion);
			if (wallside != null) {
				wallfric.removealong(wallside);
			}
			wallfric.changelen_ip(-dragforce);
			this.motion.add_ip(wallfric);

			ticks_since_last_wallrun_sound_effect++;
			if (ticks_since_last_wallrun_sound_effect > GrappleConfig.getclientconf().sounds.wallrun_sound_effect_time_s * 20 * GrappleConfig.getconf().enchantments.wallrun.wallrun_max_speed / (vel + 0.00000001)) {
				if (wallrun_raytrace_result != null) {
					BlockPos blockpos = wallrun_raytrace_result.getBlockPos();
					
					BlockState blockState = this.entity.level.getBlockState(blockpos);
					Block blockIn = blockState.getBlock();
					
			        SoundType soundtype = blockIn.getSoundType(blockState, world, blockpos, this.entity);

		            this.entity.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.30F * GrappleConfig.getclientconf().sounds.wallrun_sound_volume, soundtype.getPitch());
					ticks_since_last_wallrun_sound_effect = 0;
				}
			}
		}
		
		// jump
		boolean isjumping = ClientProxyInterface.proxy.isKeyDown(ClientProxyInterface.grapplekeys.key_jumpanddetach) && isonwall;
		isjumping = isjumping && !playerjump; // only jump once when key is first pressed
		playerjump = ClientProxyInterface.proxy.isKeyDown(ClientProxyInterface.grapplekeys.key_jumpanddetach) && isonwall;
		if (isjumping && wallrun) {
			Vec jump = new Vec(0, GrappleConfig.getconf().enchantments.wallrun.wall_jump_up, 0);
			if (walldirection != null) {
				jump.add_ip(walldirection.mult(-GrappleConfig.getconf().enchantments.wallrun.wall_jump_side));
			}
			motion.add_ip(jump);
			
			wallrun = false;

			ClientProxyInterface.proxy.playWallrunJumpSound(entity);
		}
		
		return wallrun;
	}
	
	public Vec wallrun_press_against_wall() {
		// press against wall
		if (this.walldirection != null) {
			return this.walldirection.changelen(0.05);
		}
		return new Vec(0,0,0);
	}

	public void doublejump() {
		if (-this.motion.y > GrappleConfig.getconf().enchantments.doublejump.dont_doublejump_if_falling_faster_than) {
			return;
		}
		if (this.motion.y < 0 && !GrappleConfig.getconf().enchantments.doublejump.doublejump_relative_to_falling) {
			this.motion.y = 0;
		}
		this.motion.y += GrappleConfig.getconf().enchantments.doublejump.doublejumpforce;
		motion.setmotion(this.entity);
	}
	
	public void applySlidingFriction() {
		double dragforce = GrappleConfig.getconf().enchantments.slide.sliding_friction;
		
//		double vel = this.motion.length();
//		dragforce = vel*vel * dragforce;
		
		if (dragforce > this.motion.length()) {dragforce = this.motion.length(); };
		
		Vec airfric = new Vec(this.motion.x, this.motion.y, this.motion.z);
		airfric.changelen_ip(-dragforce);
		this.motion.add_ip(airfric);
	}

	public void slidingJump() {
		this.motion.y = GrappleConfig.getconf().enchantments.slide.slidingjumpforce;
	}
}
