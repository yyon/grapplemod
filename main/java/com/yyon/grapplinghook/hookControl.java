package com.yyon.grapplinghook;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class hookControl extends grappleController {
	public hookControl(int arrowId, int entityId, World world, Vec3 pos) {
		super(arrowId, entityId, world, pos);
	}

	public double maxspeed = 4;
	public double acceleration = 0.2;
	public float oldstepheight;
	
	@Override
	public void updatePlayerPos() {
		
		/*
		super.updatePlayerPos(theplayer);
		if (r > 1) {
			r -= 1;
		}
		*/
		
		Entity entity = this.entity;
		
//		System.out.println(entity == theplayer);
//		System.out.println(entity.worldObj.isRemote);
		
		if (this.attached) {
			if(entity != null && entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
//				EntityPlayer player = ((EntityPlayer)this.riddenByEntity);
//				double l = this.getDistanceToEntity(entity);
				if (true) {
					
					Vec3 arrowpos = this.pos;
					Vec3 playerpos = player.getPositionVector();
					
					Vec3 oldspherevec = playerpos.subtract(arrowpos);
					Vec3 spherevec = changelen(oldspherevec, r);
//					Vec3 spherechange = spherevec.subtract(oldspherevec);
//					Vec3 spherepos = spherevec.add(arrowpos);
					
					double dist = oldspherevec.lengthVector();
					
					if (playerjump) {
						Vec3 jump = new Vec3(0, 0.5, 0);
						jump = proj(jump, spherevec);
						double jumppower = jump.yCoord;
						if (jumppower < 0) {
							jumppower = 0;
						}
						if (dist < 2 || player.onGround || player.isCollided) {
							jumppower = 0.5;
						}
//						player.setVelocity(player.motionX, player.motionY + jumppower, player.motionZ);
						player.motionY = player.motionY + jumppower;
						
						this.unattach();
						return;
/*					} else if (this.shootingEntity.isSneaking()) {
						motion = multvec(motion, 0.9);
						if (this.playerforward != 0) {
							if (this.r > this.playerforward * 0.5) {
								this.r -= this.playerforward * 0.5;
							}
							System.out.println(this.r);
						}*/
					} else {
						motion = motion.add(changelen(this.playermovement, 0.01));
					}
					
					Vec3 newmotion;
					
					if (dist < 4) {
						motion = motion.add(changelen(arrowpos.subtract(playerpos), 3 - dist/2));
						if (dist < 1) {
							this.unattach();
							return;
						}
					} else {
						motion = motion.add(changelen(arrowpos.subtract(playerpos), acceleration));
					}
					
					double speed = proj(motion, oldspherevec).lengthVector();
					
					if (speed > maxspeed) {
						motion = changelen(motion, maxspeed);
					}
					
					if (!player.onGround) {
						motion = motion.addVector(0, -0.05, 0);
					} else {
						if (dist > 4) {
							motion = motion.addVector(0, 0.3, 0);
						}
					}
					
					newmotion = motion;
						
//					entity.setVelocity(newmotion.xCoord, newmotion.yCoord, newmotion.zCoord);
					entity.motionX = newmotion.xCoord;
					entity.motionY = newmotion.yCoord;
					entity.motionZ = newmotion.zCoord;
					
//					if (player instanceof EntityPlayerMP) {
						
//						((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
//					}
					
					player.fallDistance = 0;
					
					this.updateServerPos();
				}
			}
		}
	}
	

}
