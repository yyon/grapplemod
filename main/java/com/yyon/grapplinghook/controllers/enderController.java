package com.yyon.grapplinghook.controllers;

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

public class enderController extends grappleController {
	public enderController(int arrowId, int entityId, World world, Vec3 pos, int maxlen) {
		super(arrowId, entityId, world, pos, maxlen);
	}

	public void receiveGrappleClick(boolean leftclick) {
		super.receiveGrappleClick(leftclick);
		
	}
	
	@Override
	public void receiveEnderLaunch(double x, double y, double z) {
//		System.out.println("now launching");
		this.motion = this.motion.addVector(x, y, z);
	}
}
