package com.yyon.grapplinghook.controllers;

import net.minecraft.entity.Entity;
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

public class airfrictionController extends grappleController {
	public final double playermovementmult = 0.5;
	
	public airfrictionController(int arrowId, int entityId, World world, vec pos, int maxlen, int id) {
		super(arrowId, entityId, world, pos, maxlen, id);
	}
	
	@Override
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			this.normalGround();
			this.normalCollisions();
			this.applyAirFriction();
			
			if (entity.isCollided || entity.onGround) {
				this.unattach();
			}
			
			motion.add_ip(this.playermovement.changelen(0.01));
			
			vec newmotion;
			
			newmotion = motion;
			
			entity.motionX = newmotion.x;
//			entity.motionY = newmotion.y;
			entity.motionZ = newmotion.z;
			
			this.updateServerPos();
		}
	}
}
