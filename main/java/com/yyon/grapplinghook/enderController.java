package com.yyon.grapplinghook;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class enderController extends grappleController {
	public enderController(int arrowId, int entityId, World world, Vec3 pos) {
		super(arrowId, entityId, world, pos);
	}

	public void receiveGrappleClick(boolean leftclick) {
		super.receiveGrappleClick(leftclick);
		
	}
	
	@Override
	public void receiveEnderLaunch(double x, double y, double z) {
		System.out.println("now launching");
		this.motion = this.motion.addVector(x, y, z);
	}
}
