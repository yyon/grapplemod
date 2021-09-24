package com.yyon.grapplinghook.controllers;

import java.util.HashSet;

import com.yyon.grapplinghook.ClientProxyClass;
import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.network.GrappleEndMessage;
import com.yyon.grapplinghook.network.PlayerMovementMessage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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
	public boolean playersneak = false;
//	public boolean waitingonplayerjump = false;
	public vec playermovement_unrotated = new vec(0,0,0);
	public vec playermovement = new vec(0,0,0);
	
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
		
		// undo friction
		double f6 = 0.91F;
        if (entity.onGround)
        {
            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain(entity.posX, entity.getEntityBoundingBox().minY - 1.0D, entity.posZ);
            IBlockState underState = this.world.getBlockState(blockpos$pooledmutableblockpos.setPos(entity.posX, entity.getEntityBoundingBox().minY - 1.0D, entity.posZ));
            f6 = underState.getBlock().getSlipperiness(underState, entity.world, blockpos$pooledmutableblockpos, entity) * 0.91F;
        }
        motion.y /= 0.9800000190734863D;
        motion.x /= (double)f6;
        motion.z /= (double)f6;

		this.ongroundtimer = 0;
		
		grapplemod.registerController(this.entityId, this);
		
//		if (grapplemod.proxy instanceof ClientProxyClass) {
//			this.clientproxy = (ClientProxyClass) grapplemod.proxy;
//		}
		
		if (arrowId != -1) {
			Entity arrowentity = world.getEntityByID(arrowId);
			if (arrowentity != null && !arrowentity.isDead && arrowentity instanceof grappleArrow) {
				this.addArrow((grappleArrow)arrowentity);
			} else {
				this.unattach();
			}
		}
		
		if (custom != null && custom.rocket) {
			grapplemod.proxy.updateRocketRegen(custom.rocket_active_time, custom.rocket_refuel_ratio);
		}
	}
	
	public void unattach() {
		if (grapplemod.controllers.containsValue(this)) {
			this.attached = false;
			
			grapplemod.unregisterController(this.entityId);
			
			if (this.controllerid != grapplemod.AIRID) {
				grapplemod.network.sendToServer(new GrappleEndMessage(this.entityId, this.arrowIds));
				grapplemod.createControl(grapplemod.AIRID, -1, this.entityId, this.entity.world, new vec(0,0,0), null, this.custom);
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
		playermovement_unrotated = new vec(strafe, 0, forward);
		playermovement = playermovement_unrotated.rotate_yaw((float) (this.entity.rotationYaw * (Math.PI / 180.0)));
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
					if (entity.isRiding()) {
						this.unattach();						
						this.updateServerPos();
						return;
					}
					
					this.normalGround(true);
					this.normalCollisions(true);
					this.applyAirFriction();
					
					vec playerpos = vec.positionvec(entity);
					playerpos = playerpos.add(new vec(0, entity.getEyeHeight(), 0));
					

//					Vec3 playermotion = new Vec3(entity.motionX, entity.motionY, entity.motionZ);
					
					vec additionalmotion = new vec(0,0,0);
					
					vec gravity = new vec(0, -0.05, 0);

					
//					if (!(this.ongroundtimer > 0)) {
						motion.add_ip(gravity);
//					}
					
					boolean doJump = false;
					double jumpSpeed = 0;
					boolean isClimbing = false;
					
					// is motor active? (check motorwhencrouching / motorwhennotcrouching)
					boolean motor = false;
					if (this.custom.motor) {
						if (ClientProxyClass.key_motoronoff.isKeyDown() && this.custom.motorwhencrouching) {
							motor = true;
						} else if (!ClientProxyClass.key_motoronoff.isKeyDown() && this.custom.motorwhennotcrouching) {
							motor = true;
						}
					}
					
//					double curspeed = 0;
					boolean close = false;
					
					vec averagemotiontowards = new vec(0, 0, 0);
					
					double min_spherevec_dist = 99999;
					
					for (grappleArrow arrow : this.arrows) {
						vec arrowpos = vec.positionvec(arrow);//this.getPositionVector();
						
						// Update segment handler (handles rope bends)
						if (this.custom.phaserope) {
							arrow.segmenthandler.updatepos(arrowpos, playerpos, arrow.r);
						} else {
							arrow.segmenthandler.update(arrowpos, playerpos, arrow.r, false);
						}
						
						// vectors along rope
						vec anchor = arrow.segmenthandler.getclosest(arrowpos);
						double distToAnchor = arrow.segmenthandler.getDistToAnchor();
						double remaininglength = arrow.r - distToAnchor;
						
						vec oldspherevec = playerpos.sub(anchor);
						vec spherevec = oldspherevec.changelen(remaininglength);
						vec spherechange = spherevec.sub(oldspherevec);
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
								if (oldspherevec.length() - remaininglength > GrappleConfig.getconf().rope_snap_buffer) {
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
						if (entity instanceof EntityPlayer) {
							EntityPlayer player = (EntityPlayer) entity;
							boolean isjumping = ClientProxyClass.key_jumpanddetach.isKeyDown();
							isjumping = isjumping && !playerjump; // only jump once when key is first pressed
							playerjump = ClientProxyClass.key_jumpanddetach.isKeyDown();
							if (isjumping) {
								// jumping
								if (ongroundtimer > 0) { // on ground: jump normally
									
								} else {
									double timer = this.entity.world.getTotalWorldTime() - ClientProxyClass.prev_rope_jump_time;
									if (timer > GrappleConfig.getconf().rope_jump_cooldown_s * 20.0) {
										doJump = true;
										jumpSpeed = this.getJumpPower(player, spherevec, arrow);
									}
								}
							}
							if (ClientProxyClass.key_slow.isKeyDown()) {
								// climbing
	//							motion = multvec(motion, 0.9);
								vec motiontorwards = spherevec.changelen(-0.1);
								motiontorwards = new vec(motiontorwards.x, 0, motiontorwards.z);
								if (motion.dot(motiontorwards) < 0) {
									motion.add_ip(motiontorwards);
								}

								vec newmotion = dampenmotion(motion, motiontorwards);
								motion = new vec(newmotion.x, motion.y, newmotion.z);
	//							motion = multvec(motion, 0.98);

							}
							if ((ClientProxyClass.key_climb.isKeyDown() || !this.custom.climbkey) && !motor) {
								isClimbing = true;
								if (anchor.y > playerpos.y) {
									// when shift is pressed, stop swinging
									
									// climb up/down rope
									float playerforward = 0;
									if (ClientProxyClass.key_climbup.isKeyDown()) { playerforward = 1.0f; }
									else if (ClientProxyClass.key_climbdown.isKeyDown()) { playerforward = -1.0f; }
									if (playerforward != 0) {
											if (dist + distToAnchor < maxlen || this.playerforward > 0 || maxlen == 0) {
//												double motionup = this.playerforward;
//												additionalmotion = new vec(0, playerforward, 0);
//												additionalmotion.add_ip(spherevec.changelen_ip(playerforward));
//												this.r = dist;
												arrow.r = dist + distToAnchor;
												arrow.r -= playerforward*GrappleConfig.getconf().climb_speed;
												if (arrow.r < distToAnchor) {
													arrow.r = dist + distToAnchor;
												}
												
												vec additionalmovementdown = spherevec.changelen(-playerforward * GrappleConfig.getconf().climb_speed).proj(new vec(0,1,0));
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
					
		        	vec facing = new vec(entity.getLookVec()).normalize();
		        	
		        	// Motor
					if (motor) {
						boolean dopull = true;
						
						// if only one rope is pulling and not oneropepull, disable motor
						if (this.custom.doublehook && this.arrows.size() == 1) {
							boolean isdouble = true;
							for (grappleArrow arrow : this.arrows) {
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
						
						vec totalpull = new vec(0, 0, 0);
						
						double accel = this.custom.motoracceleration / this.arrows.size();
						
						double minabssidewayspull = 999;
//						double maxabssidewayspull = 0;
						
						boolean firstpull = true;
						boolean pullispositive = true;
						boolean pullissameway = true;
						
						// set all motors to maximum pull and precalculate some stuff for smart motor / smart double motor
						for (grappleArrow arrow : this.arrows) {
							vec arrowpos = vec.positionvec(arrow);//this.getPositionVector();
							vec anchor = arrow.segmenthandler.getclosest(arrowpos);
							vec spherevec = playerpos.sub(anchor);
							vec pull = spherevec.mult(-1);
							
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
									vec facingxy = new vec(facing.x, 0, facing.z);
									vec facingside = facingxy.cross(new vec(0, 1, 0)).normalize();
//									vec pullxy = new vec(pull.x, 0, pull.z);
									vec sideways = pull.proj(facingside); // pullxy.removealong(facing);
									vec currentsideways = motion.proj(facingside);
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
							totalpull = new vec(0, 0, 0);
							
							for (grappleArrow arrow : this.arrows) {
								vec arrowpos = vec.positionvec(arrow);//this.getPositionVector();
								vec anchor = arrow.segmenthandler.getclosest(arrowpos);
								vec spherevec = playerpos.sub(anchor);
								vec pull = spherevec.mult(-1);
								pull.changelen_ip(arrow.pull);
								
								if (pull.dot(facing) > 0 || this.custom.pullbackwards) {
									vec facingxy = new vec(facing.x, 0, facing.z);
									vec facingside = facingxy.cross(new vec(0, 1, 0)).normalize();
//									vec pullxy = new vec(pull.x, 0, pull.z);
									vec sideways = pull.proj(facingside); // pullxy.removealong(facing);
									vec currentsideways = motion.proj(facingside);
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
						if (this.custom.smartmotor && totalpull.y > 0 && !(this.ongroundtimer > 0 || entity.onGround)) {
							vec pullxzvector = new vec(totalpull.x, 0, totalpull.z);
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
							for (grappleArrow arrow : this.arrows) {
								vec arrowpos = vec.positionvec(arrow);//this.getPositionVector();
								vec anchor = arrow.segmenthandler.getclosest(arrowpos);
								vec spherevec = playerpos.sub(anchor);
								vec pull = spherevec.mult(-1);
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
							if (entity.collided || entity.onGround) {
								motion.mult_ip(0.6);
							}
						}
//						System.out.println();
					}
					
					// forcefield
					if (this.custom.repel) {
						vec blockpush = check_repel(playerpos, entity.world);
						blockpush.mult_ip(this.custom.repelforce * 0.5);
						blockpush = new vec(blockpush.x*0.5, blockpush.y*2, blockpush.z*0.5);
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
						if (jumpSpeed > GrappleConfig.getconf().rope_jump_power) {
							jumpSpeed = GrappleConfig.getconf().rope_jump_power;
						}
						this.doJump(entity, jumpSpeed, averagemotiontowards, min_spherevec_dist);
						ClientProxyClass.prev_rope_jump_time = this.entity.world.getTotalWorldTime();
						return;
					}
					
					// now to actually apply everything to the player
					vec newmotion = motion.add(additionalmotion);
					
					if (Double.isNaN(newmotion.x) || Double.isNaN(newmotion.y) || Double.isNaN(newmotion.z)) {
						newmotion = new vec(0, 0, 0);
						motion = new vec(0, 0, 0);
						System.out.println("error: motion is NaN");
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
	
//	boolean prevcollision = false;
//	vec prevcollisionpos = new vec(0,0,0);

	public void normalCollisions(boolean refreshmotion) {
		// stop if collided with object
		vec pos = vec.positionvec(this.entity);
		if (entity.collidedHorizontally) {
//			if (refreshmotion || prevcollision) {
				if (entity.motionX == 0) {
					if (refreshmotion || this.tryStepUp(new vec(this.motion.x, 0, 0))) {
						this.motion.x = 0;
					}
				}
				if (entity.motionZ == 0) {
					if (refreshmotion || this.tryStepUp(new vec(0, 0, this.motion.z))) {
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
		if (entity.collidedVertically) {
			if (entity.onGround) {
				if (refreshmotion && GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump)) {
					this.motion.y = entity.motionY;
				} else {
					if (this.motion.y < 0) {
						this.motion.y = 0;
					}
				}
			} else {
				if (this.motion.y > 0) {
					if (entity.lastTickPosY == entity.posY) {
						this.motion.y = 0;
					}
				}
			}
		}
	}
	
	public boolean tryStepUp(vec collisionmotion) {
		if (collisionmotion.length() == 0) {return false;}
		vec moveoffset = collisionmotion.changelen(0.05).add(0, entity.stepHeight+0.01, 0);
		if (this.world.getCollisionBoxes(this.entity, this.entity.getEntityBoundingBox().offset(moveoffset.x, moveoffset.y, moveoffset.z)).isEmpty()) {
			if (!this.entity.onGround) {
				entity.posX += moveoffset.x;
				entity.posY += moveoffset.y;
				entity.posZ += moveoffset.z;
				this.entity.setPosition(entity.posX, entity.posY, entity.posZ);
				entity.prevPosX = entity.posX;
				entity.prevPosY = entity.posY;
				entity.prevPosZ = entity.posZ;
			}
			entity.collidedHorizontally = false;
			return false;
		}
		return true;
	}
	
	boolean prevonground = false;

	public void normalGround(boolean refreshmotion) {
		if (entity.onGround) {
			ongroundtimer = maxongroundtimer;
//			if (this.motion.y < 0) {
//				this.motion.y = 0;
//			}
		} else {
			if (this.ongroundtimer > 0) {
				ongroundtimer--;
			}
		}
		if (entity.onGround || ongroundtimer > 0) {
			if (refreshmotion) {
				this.motion = vec.motionvec(entity);
				if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()) {
					this.motion.y += 0.05;
				}
			}
		}
		prevonground = entity.onGround;
	}

	private double getJumpPower(Entity player, double jumppower) {
		double maxjump = GrappleConfig.getconf().rope_jump_power;
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
	
	public void doJump(Entity player, double jumppower, vec averagemotiontowards, double min_spherevec_dist) {
		if (jumppower > 0) {
			if (GrappleConfig.getconf().rope_jump_at_angle && min_spherevec_dist > 1) {
				motion.add_ip(averagemotiontowards.changelen(jumppower));
				player.motionX = motion.x;
				player.motionY = motion.y;
				player.motionZ = motion.z;
			} else {
				if (jumppower > player.motionY + jumppower) {
					player.motionY = jumppower;
				} else {
					player.motionY += jumppower;
				}
			}
		}
		
		this.unattach();
		
		this.updateServerPos();
	}
	
	public double getJumpPower(Entity player, vec spherevec, grappleArrow arrow) {
		double maxjump = GrappleConfig.getconf().rope_jump_power;
		vec jump = new vec(0, maxjump, 0);
		if (spherevec != null && !GrappleConfig.getconf().rope_jump_at_angle) {
			jump = jump.proj(spherevec);
		}
		double jumppower = jump.y;
		
		if (spherevec != null && spherevec.y > 0) {
			jumppower = 0;
		}
		if ((arrow != null) && arrow.r < 1 && (player.posY < arrow.posY)) {
			jumppower = maxjump;
		}

		jumppower = this.getJumpPower(player, jumppower);
		
		double current_speed = GrappleConfig.getconf().rope_jump_at_angle ? -motion.dist_along(spherevec) : motion.y;
		if (current_speed > 0) {
			jumppower = jumppower - current_speed;
		}

		if (jumppower < 0) {jumppower = 0;}

		return jumppower;
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
	
	public void receiveGrappleDetach() {
		this.unattach();
	}

	public void receiveEnderLaunch(double x, double y, double z) {
		this.motion.add_ip(x, y, z);
		this.entity.motionX = this.motion.x;
		this.entity.motionY = this.motion.y;
		this.entity.motionZ = this.motion.z;
	}
	
	public void applyAirFriction() {
		double dragforce = 1 / 200F;
		if (this.entity.isInWater()) {
//			this.applyWaterFriction();
			dragforce = 1 / 4F;
		}
		
		double vel = this.motion.length();
		dragforce = vel * dragforce;
		
		vec airfric = new vec(this.motion.x, this.motion.y, this.motion.z);
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

	public void receiveGrappleDetachHook(int hookid) {
		if (this.arrowIds.contains(hookid)) {
			this.arrowIds.remove(hookid);
		} else {
			System.out.println("Error: controller received hook detach, but hook id not in arrowIds");
		}
		
		grappleArrow arrowToRemove = null;
		for (grappleArrow arrow : this.arrows) {
			if (arrow.getEntityId() == hookid) {
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
	
	public vec rocket(Entity entity) {
		if (ClientProxyClass.key_rocket.isKeyDown()) {
			double rocket_force = this.custom.rocket_force * 0.225 * grapplemod.proxy.getRocketFunctioning();
        	double yaw = entity.rotationYaw;
        	double pitch = -entity.rotationPitch;
        	pitch += this.custom.rocket_vertical_angle;
        	vec force = new vec(0, 0, rocket_force);
        	force = force.rotate_pitch(Math.toRadians(pitch));
        	force = force.rotate_yaw(Math.toRadians(yaw));
        	
        	return force;
		}
		return new vec(0,0,0);
	}
	
	boolean isonwall = false;
	vec walldirection = null;
//	boolean wallonleft = true;
	
	public vec getnearbywall(vec tryfirst, vec trysecond, double extra) {
		float entitywidth = this.entity.width;
		
		for (vec direction : new vec[] {tryfirst, trysecond, tryfirst.mult(-1), trysecond.mult(-1)}) {
			RayTraceResult raytraceresult = this.entity.world.rayTraceBlocks(this.entity.getPositionVector(), vec.positionvec(this.entity).add(direction.changelen(entitywidth/2 + extra)).toVec3d(), false, true, false);
			if (raytraceresult != null && raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
				return direction;
			}
		}
		
		return null;
	}
	
	public vec getwalldirection() {
		vec tryfirst = new vec(0, 0, 0);
		vec trysecond = new vec(0, 0, 0);
		
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
	
	public vec getcorner(int cornernum, vec facing, vec sideways) {
		vec corner = new vec(0,0,0);
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
		
		float entitywidth = this.entity.width;
		vec v1 = new vec(entitywidth/2 + dist, 0, 0);
		vec v2 = new vec(0, 0, entitywidth/2 + dist);
		
		for (int i = 0; i < 4; i++) {
			vec corner1 = getcorner(i, v1, v2);
			vec corner2 = getcorner((i + 1) % 4, v1, v2);
			
			RayTraceResult raytraceresult = this.entity.world.rayTraceBlocks(vec.positionvec(this.entity).add(corner1).toVec3d(), vec.positionvec(this.entity).add(corner2).toVec3d(), false, true, false);
			if (raytraceresult != null && raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
				return true;
			}
		}
		
		return false;
	}
	
	public int tickswallrunning = 0;

	public boolean iswallrunning() {
		double current_speed = Math.sqrt(Math.pow(this.motion.x, 2) + Math.pow(this.motion.z,  2));
		if (current_speed < GrappleConfig.getconf().wallrun_min_speed) {
			isonwall = false;
			return false;
		}
		
		if (isonwall) {
			tickswallrunning += 1;
		}
		
		if (tickswallrunning < GrappleConfig.getconf().max_wallrun_time * 40) {
			if (!(playersneak)) {
				// continue wallrun
				if (isonwall && !this.entity.onGround && this.entity.collidedHorizontally) {
					return true;
				}
				
				// start wallrun
				if (grapplemod.proxy.iswallrunning(this.entity)) {
					isonwall = true;
					return true;
				}
			}
			
			isonwall = false;
		}
		
		if (tickswallrunning > 0 && (this.entity.onGround || (!this.entity.collidedHorizontally && !wallnearby(0.2)))) {
			tickswallrunning = 0;
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
		
		if (wallrun && !ClientProxyClass.key_jumpanddetach.isKeyDown()) {

			vec wallside = this.getwalldirection();
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
			double dragforce = GrappleConfig.getconf().wallrun_drag;
			
			double vel = this.motion.length();
//			dragforce = vel*vel * dragforce;
			
			if (dragforce > vel) {dragforce = vel;}
			
			vec wallfric = new vec(this.motion);
			if (wallside != null) {
				wallfric.removealong(wallside);
			}
			wallfric.changelen_ip(-dragforce);
			this.motion.add_ip(wallfric);
			
			
		}
		
		// jump
		boolean isjumping = ClientProxyClass.key_jumpanddetach.isKeyDown() && isonwall;
		isjumping = isjumping && !playerjump; // only jump once when key is first pressed
		playerjump = ClientProxyClass.key_jumpanddetach.isKeyDown() && isonwall;
		if (isjumping && wallrun) {
			vec jump = new vec(0, GrappleConfig.getconf().wall_jump_up, 0);
			if (walldirection != null) {
				jump.add_ip(walldirection.mult(-GrappleConfig.getconf().wall_jump_side));
			}
			motion.add_ip(jump);
			
			wallrun = false;
		}
		
		return wallrun;
	}
	
	public vec wallrun_press_against_wall() {
		// press against wall
		if (this.walldirection != null) {
			return this.walldirection.changelen(0.05);
		}
		return new vec(0,0,0);
	}

	public void doublejump() {
		if (-this.motion.y > GrappleConfig.getconf().dont_doublejump_if_falling_faster_than) {
			return;
		}
		if (this.motion.y < 0 && !GrappleConfig.getconf().doublejump_relative_to_falling) {
			this.motion.y = 0;
		}
		this.motion.y += GrappleConfig.getconf().doublejumpforce;
		entity.motionX = motion.x;
		entity.motionY = motion.y;
		entity.motionZ = motion.z;
	}
	
	public void applySlidingFriction() {
		double dragforce = GrappleConfig.getconf().sliding_friction;
		
//		double vel = this.motion.length();
//		dragforce = vel*vel * dragforce;
		
		if (dragforce > this.motion.length()) {dragforce = this.motion.length(); };
		
		vec airfric = new vec(this.motion.x, this.motion.y, this.motion.z);
		airfric.changelen_ip(-dragforce);
		this.motion.add_ip(airfric);
	}

	public void slidingJump() {
		this.motion.y = GrappleConfig.getconf().slidingjumpforce;
	}
}
