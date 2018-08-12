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
	public hookControl(int arrowId, int entityId, World world, vec pos, double maxlen, int id) {
		super(arrowId, entityId, world, pos, maxlen, id, null);
	}

	public double maxspeed = 4;
	public double acceleration = 0.2;
	public float oldstepheight;
	public final double playermovementmult = 1;
		
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
//					this.normalGround();
					this.normalCollisions();
//					this.applyAirFriction();
					
//					vec arrowpos = this.pos;
					vec playerpos = vec.positionvec(player);
					
					vec oldspherevec = playerpos.sub(null);
					vec spherevec = oldspherevec.changelen(0);
//					Vec3 spherechange = spherevec.subtract(oldspherevec);
//					Vec3 spherepos = spherevec.add(arrowpos);
					
					double dist = oldspherevec.length();
					
					if (this.isjumping()) {
//						this.dojump(player, spherevec);
						return;

					} else {
						applyPlayerMovement();
					}
					
					vec newmotion;
					
					if (dist < 4) {
						if (motion.length() > 0.3) {
							motion.mult_ip(0.6);
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
					
//					motion.add_ip(arrowpos.sub(playerpos).changelen(acceleration));
					
					double speed = motion.proj(oldspherevec).length();
					
					if (speed > maxspeed) {
						motion.changelen_ip(maxspeed);
					}
					

					
					newmotion = motion;
					
					vec motiontorwards = spherevec.changelen(-1);
					motion = dampenmotion(motion, motiontorwards);
					
//					entity.setVelocity(newmotion.xCoord, newmotion.yCoord, newmotion.zCoord);
					entity.motionX = newmotion.x;
					entity.motionY = newmotion.y;
					entity.motionZ = newmotion.z;
					
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
