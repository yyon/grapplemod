package com.yyon.grapplinghook.controllers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;

public class repelController extends magnetController {

	public repelController(int arrowId, int entityId, World world, vec pos,
			int maxlen, int id) {
		super(arrowId, entityId, world, pos, maxlen, id, 0);
	}

	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null) {
				if (true) {
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
					
					vec playerpos = vec.positionvec(entity);
					
//					double dist = oldspherevec.length();
					
					boolean domagnet = true;
					
					if (entity instanceof EntityPlayer) {
//						EntityPlayer player = (EntityPlayer) entity;
						if (grapplemod.proxy.isSneaking(entity)) {
							domagnet = false;
							motion.mult_ip(0.95);
						}
						motion.add_ip(this.playermovement.changelen(0.03));//0.02));
					}
					
					vec blockpush = check(playerpos, entity.worldObj);
					blockpush.mult_ip(0.5);
					if (domagnet) {
						blockpush = new vec(blockpush.x*0.5, blockpush.y*2, blockpush.z*0.5);
					}
					this.motion.add_ip(blockpush);
					
					if (!entity.onGround) {
						motion.add_ip(0, -0.05, 0);
					}
					
					entity.motionX = motion.x;
					entity.motionY = motion.y;
					entity.motionZ = motion.z;
					
					this.updateServerPos();
				}
			}
		}
	}}
