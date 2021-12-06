package com.yyon.grapplinghook.controllers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;

public class repelController extends grappleController {
	public repelController(int arrowId, int entityId, World world, vec pos, int id) {
		super(arrowId, entityId, world, pos, id, null);
		
		this.playermovementmult = 1;
	}

	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null) {
				if (true) {
					this.normalGround(false);
					this.normalCollisions(false);
//					this.applyAirFriction();
					
					vec playerpos = vec.positionvec(entity);
					
//					double dist = oldspherevec.length();
					
					if (entity instanceof EntityPlayer) {
//						EntityPlayer player = (EntityPlayer) entity;
						if (playersneak) {
							motion.mult_ip(0.95);
						}
						applyPlayerMovement();
					}
					
					vec blockpush = check_repel(playerpos, entity.world);
					blockpush.mult_ip(0.5);
					blockpush = new vec(blockpush.x*0.5, blockpush.y*2, blockpush.z*0.5);
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
