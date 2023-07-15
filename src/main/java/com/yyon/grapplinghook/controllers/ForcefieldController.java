package com.yyon.grapplinghook.controllers;

import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ForcefieldController extends GrappleController {
	public ForcefieldController(int grapplehookEntityId, int entityId, Level world, Vec pos, int id) {
		super(grapplehookEntityId, entityId, world, pos, id, null);
		
		this.playerMovementMult = 1;
	}

	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null) {
				if (true) {
					this.normalGround(false);
					this.normalCollisions(false);
//					this.applyAirFriction();
					
					Vec playerpos = Vec.positionVec(entity);
					
//					double dist = oldspherevec.length();
					
					if (playerSneak) {
						motion.mult_ip(0.95);
					}
					applyPlayerMovement();
					
					Vec blockpush = checkRepel(playerpos, entity.level());
					blockpush.mult_ip(0.5);
					blockpush = new Vec(blockpush.x*0.5, blockpush.y*2, blockpush.z*0.5);
					this.motion.add_ip(blockpush);
					
					if (!entity.onGround()) {
						motion.add_ip(0, -0.05, 0);
					}
					
					motion.setMotion(this.entity);
					
					this.updateServerPos();
				}
			}
		}
	}}
