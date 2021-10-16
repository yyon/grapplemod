package com.yyon.grapplinghook.controllers;

import com.yyon.grapplinghook.utils.Vec;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ForcefieldController extends GrappleController {
	public ForcefieldController(int arrowId, int entityId, World world, Vec pos, int id) {
		super(arrowId, entityId, world, pos, id, null);
		
		this.playermovementmult = 1;
	}

	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null) {
				if (true) {
					this.normalGround(true);
					this.normalCollisions(true);
//					this.applyAirFriction();
					
					Vec playerpos = Vec.positionvec(entity);
					
//					double dist = oldspherevec.length();
					
					if (playersneak) {
						motion.mult_ip(0.95);
					}
					applyPlayerMovement();
					
					Vec blockpush = check_repel(playerpos, entity.level);
					blockpush.mult_ip(0.5);
					blockpush = new Vec(blockpush.x*0.5, blockpush.y*2, blockpush.z*0.5);
					this.motion.add_ip(blockpush);
					
					if (!entity.isOnGround()) {
						motion.add_ip(0, -0.05, 0);
					}
					
					motion.setmotion(this.entity);
					
					this.updateServerPos();
				}
			}
		}
	}}
