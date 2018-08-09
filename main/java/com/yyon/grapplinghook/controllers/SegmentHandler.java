package com.yyon.grapplinghook.controllers;

import java.util.ArrayList;

import com.yyon.grapplinghook.vec;

import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class SegmentHandler {

	public ArrayList<vec> segments;
	public World world;
	
	public SegmentHandler(World w) {
		segments = new ArrayList<vec>();
		segments.add(new vec(0, 0, 0));
		segments.add(new vec(0, 0, 0));
		this.world = w;
	}
	
	public void update(vec hookpos, vec playerpos) {
		segments.set(0, hookpos);
		segments.set(segments.size() - 1, playerpos);
		
		vec closest = segments.get(segments.size()-2);
		
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(playerpos.toVec3d(), closest.toVec3d());
        
        if (raytraceresult != null)
        {
            vec hitvec = new vec(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
            segments.add(playerpos);
            segments.set(segments.size() - 2, hitvec);
        }
	}
	
	public vec getclosest(vec hookpos) {
		segments.set(0, hookpos);
		
		return segments.get(segments.size() - 2);
	}
	
	public double getDistToAnchor() {
		double dist = 0;
		for (int i = 0; i < segments.size() - 2; i++) {
			dist += segments.get(i).sub(segments.get(i+1)).length();
		}
		
		return dist;
	}
}
