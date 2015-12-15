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
						this.dojump(player, spherevec);
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
						if (motion.lengthVector() > 0.3) {
							motion = multvec(motion, 0.6);
						}
						
//						if (this.playermovement.lengthVector() > 0.05) {
//							this.unattach();
//						}
						if (player.onGround) {
							entity.motionX = 0;
							entity.motionY = 0;
							entity.motionZ = 0;
							this.updateServerPos();
							
//							this.unattach();
						}
					}
					
					motion = motion.add(changelen(arrowpos.subtract(playerpos), acceleration));
					
					double speed = proj(motion, oldspherevec).lengthVector();
					
					if (speed > maxspeed) {
						motion = changelen(motion, maxspeed);
					}
					
					/*
					if (!player.onGround) {
						motion = motion.addVector(0, -0.05, 0);
					} else {
						if (dist > 4) {
							motion = motion.addVector(0, 0.3, 0);
						}
					}
					*/
					
					newmotion = motion;
					
					Vec3 motiontorwards = changelen(spherevec, -1);
					motion = dampenmotion(motion, motiontorwards);
					
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
