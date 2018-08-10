package com.yyon.grapplinghook.controllers;

import java.util.LinkedList;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.network.SegmentMessage;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class SegmentHandler {

	public LinkedList<vec> segments;
	public LinkedList<EnumFacing> segmentbottomsides;
	public LinkedList<EnumFacing> segmenttopsides;
	public World world;
	public grappleArrow arrow;
	
	vec prevhookpos = null;
	vec prevplayerpos = null;;
	
	public SegmentHandler(World w, grappleArrow arrow) {
		segments = new LinkedList<vec>();
		segments.add(new vec(0, 0, 0));
		segments.add(new vec(0, 0, 0));
		segmentbottomsides = new LinkedList<EnumFacing>();
		segmentbottomsides.add(null);
		segmentbottomsides.add(null);
		segmenttopsides = new LinkedList<EnumFacing>();
		segmenttopsides.add(null);
		segmenttopsides.add(null);
		this.world = w;
		this.arrow = arrow;
	}
	
	double ropelen;
	
	public void update(vec hookpos, vec playerpos, double ropelen, boolean movinghook) {
		if (prevhookpos == null) {
	        prevhookpos = hookpos;
	        prevplayerpos = playerpos;
		}
		
		segments.set(0, hookpos);
		segments.set(segments.size() - 1, playerpos);
		this.ropelen = ropelen;
		
		
		vec closest = segments.get(segments.size()-2);
		
		while (true) {
			if (segments.size() == 2) {
				break;
			}
			
			int index = segments.size()-2;
			closest = segments.get(index);
			EnumFacing bottomside = segmentbottomsides.get(index);
			EnumFacing topside = segmenttopsides.get(index);
			vec ropevec = playerpos.sub(closest);
			
			vec beforepoint = segments.get(index-1);
			
			vec edgevec = getnormal(bottomside).cross(getnormal(topside));
			vec planenormal = beforepoint.sub(closest).cross(edgevec);
//			planenormal = getnormal(bottomside).add(getnormal(topside)).proj(planenormal);
			
//			System.out.println(ropevec.dot(planenormal));
			
			if (ropevec.dot(planenormal) > 0) {
				this.removesegment(index);
			} else {
				break;
			}
		}
		
		vec farthest = segments.get(1);
		
		if (movinghook) {
			while (true) {
				if (segments.size() == 2) {
					break;
				}
				
				int index = 1;
				farthest = segments.get(index);
				EnumFacing bottomside = segmentbottomsides.get(index);
				EnumFacing topside = segmenttopsides.get(index);
				vec ropevec = farthest.sub(hookpos);
				
				vec beforepoint = segments.get(index+1);
				
				vec edgevec = getnormal(bottomside).cross(getnormal(topside));
				vec planenormal = beforepoint.sub(farthest).cross(edgevec);
//				planenormal = getnormal(bottomside).add(getnormal(topside)).proj(planenormal);
				
//				System.out.println(ropevec.dot(planenormal));
				
				if (ropevec.dot(planenormal) > 0 || ropevec.length() < 0.1) {
					System.out.println("removed farthest");
					this.removesegment(index);
				} else {
					break;
				}
			}
			
			while (true) {
				if (this.getDistToFarthest() > ropelen) {
					this.removesegment(1);
				} else {
					break;
				}
			}
		}
		
		vec prevclosest = closest;
		if (segments.size() == 2) {
			prevclosest = prevhookpos;
		}
		updatesegment(closest, prevclosest, playerpos, prevplayerpos, segments.size() - 1);
		
		farthest = segments.get(1);
		vec prevfarthest = farthest;
		if (segments.size() == 2) {
			prevfarthest = prevplayerpos;
		}
		updatesegment(hookpos, prevhookpos, farthest, prevfarthest, 1);
		
        prevhookpos = hookpos;
        prevplayerpos = playerpos;
	}
	
	public void removesegment(int index) {
		System.out.println("removed segment");
		segments.remove(index);
		segmentbottomsides.remove(index);
		segmenttopsides.remove(index);

		if (!this.world.isRemote) {
			SegmentMessage addmessage = new SegmentMessage(this.arrow.getEntityId(), false, index, new vec(0, 0, 0), EnumFacing.DOWN, EnumFacing.DOWN);
			vec playerpoint = vec.positionvec(this.arrow.shootingEntity);
			grapplemod.network.sendToAllAround(addmessage, new TargetPoint(this.world.provider.getDimension(), playerpoint.x, playerpoint.y, playerpoint.z, 100));
		}
	}
	
	public void updatesegment(vec top, vec prevtop, vec bottom, vec prevbottom, int index) {
        RayTraceResult bottomraytraceresult = this.world.rayTraceBlocks(bottom.toVec3d(), top.toVec3d());
        
        if (bottomraytraceresult != null)
        {
            vec bottomhitvec = new vec(bottomraytraceresult.hitVec.x, bottomraytraceresult.hitVec.y, bottomraytraceresult.hitVec.z);
            EnumFacing bottomside = bottomraytraceresult.sideHit;
            RayTraceResult topraytraceresult = this.world.rayTraceBlocks(top.toVec3d(), bottom.toVec3d());
            vec tophitvec = new vec(topraytraceresult.hitVec.x, topraytraceresult.hitVec.y, topraytraceresult.hitVec.z);
            EnumFacing topside = topraytraceresult.sideHit;
            
            if (bottomhitvec.sub(top).length() > 0.01 && tophitvec.sub(bottom).length() > 0.01) {
            	if (bottomside == topside) {
            		System.out.println("Warning: bottomside == topside");
            	} else if ((bottomside == EnumFacing.DOWN && topside == EnumFacing.UP) || 
	            		(bottomside == EnumFacing.UP && topside == EnumFacing.DOWN) || 
	            		(bottomside == EnumFacing.EAST && topside == EnumFacing.WEST) || 
	            		(bottomside == EnumFacing.WEST && topside == EnumFacing.EAST) || 
	            		(bottomside == EnumFacing.NORTH && topside == EnumFacing.SOUTH) || 
	            		(bottomside == EnumFacing.SOUTH && topside == EnumFacing.NORTH)) {
	            	System.out.println("two sides");
	            	// binary search to find 3rd side
	            	vec newprevtop = prevtop;
	            	vec newprevbottom = prevbottom;
            		vec center = bottomhitvec.add(tophitvec).mult(0.5);
	            	for (int i = 0; i < 20; i++) {
	            		vec prevcenter = newprevtop.add(newprevbottom).mult(0.5);
	            		RayTraceResult thirdsidetrace = this.world.rayTraceBlocks(prevcenter.toVec3d(), center.toVec3d());
	            		if (thirdsidetrace == null) {
	            			break;
	            		}
	            		EnumFacing thirdside = thirdsidetrace.sideHit;
	            		if (thirdside == bottomside) {
	            			newprevbottom = prevcenter;
	            		} else if (thirdside == topside) {
	            			newprevtop = prevcenter;
	            		} else {
	            			vec collisionpoint = new vec(thirdsidetrace.hitVec.x, thirdsidetrace.hitVec.y, thirdsidetrace.hitVec.z);
	            			this.addsegment(bottomhitvec, collisionpoint, bottomside, thirdside, index, top, prevtop, bottom, prevbottom);
	            			this.addsegment(collisionpoint, tophitvec, thirdside, topside, index, top, prevtop, bottom, prevbottom);
	            			break;
	            		}
	            	}
	            } else {
                    this.addsegment(bottomhitvec, tophitvec, bottomside, topside, index, top, prevtop, bottom, prevbottom);
	            }
            }
        }
	}
	
	public vec getnormal(EnumFacing facing) {
		Vec3i facingvec = facing.getDirectionVec();
		return new vec(facingvec.getX(), facingvec.getY(), facingvec.getZ());
	}
	
	public void addsegment(vec bottomhit, vec tophit, EnumFacing bottomside, EnumFacing topside, int index, vec top, vec prevtop, vec bottom, vec prevbottom) {
		System.out.println("Computing bend point");
		
		vec bottomnormal = getnormal(bottomside);
		vec topnormal = getnormal(topside);
		vec edgevec = bottomnormal.cross(topnormal);
		
		edgevec.print();
		
		double d = (tophit.sub(bottomhit)).dot(topnormal) / topnormal.dot(topnormal);
		vec edgepoint = topnormal.mult(d).add(bottomhit);
		
		edgepoint.print();
		
		vec movement = bottom.sub(prevbottom);
		if (movement.length() == 0) {
			movement = top.sub(prevtop);
		}
		vec planenormal = movement.cross(top.sub(bottom));
		
		movement.print();
		planenormal.print();
		
		if (edgevec.dot(planenormal) == 0) {
			System.out.println("warning: can't compute bend point");
			return;
		}
		
		double d2 = (top.sub(edgepoint)).dot(planenormal) / edgevec.dot(planenormal);
		
		vec intersectionpoint = edgevec.mult(d2).add(edgepoint);
		
		intersectionpoint.print();
		
		vec offset = bottomnormal.add(topnormal).mult(0.1);
		vec bendpoint = intersectionpoint.add(offset);
		
		this.actuallyaddsegment(index, bendpoint, bottomside, topside);

		if(this.getDistToAnchor() + .2 > this.ropelen) {
			System.out.println("not enough length left, removing");
			this.removesegment(index);
			return;
		}
	}
	
	public void actuallyaddsegment(int index, vec bendpoint, EnumFacing bottomside, EnumFacing topside) {
        segments.add(index, bendpoint);
        segmentbottomsides.add(index, bottomside);
        segmenttopsides.add(index, topside);
		System.out.println("added segment");
		this.print();
		
		if (!this.world.isRemote) {
			SegmentMessage addmessage = new SegmentMessage(this.arrow.getEntityId(), true, index, bendpoint, topside, bottomside);
			vec playerpoint = vec.positionvec(this.arrow.shootingEntity);
			grapplemod.network.sendToAllAround(addmessage, new TargetPoint(this.world.provider.getDimension(), playerpoint.x, playerpoint.y, playerpoint.z, 100));
		}
	}
	
	public void print() {
		for (int i = 1; i < segments.size() - 1; i++) {
			System.out.print(i);
			System.out.print(" ");
			System.out.print(segmenttopsides.get(i).toString());
			System.out.print(" ");
			System.out.print(segmentbottomsides.get(i).toString());
			System.out.print(" ");
			segments.get(i).print();
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
	
	public vec getfarthest() {
		return segments.get(1);
	}
	
	public double getDistToFarthest() {
		double dist = 0;
		for (int i = 1; i < segments.size() - 1; i++) {
			dist += segments.get(i).sub(segments.get(i+1)).length();
		}
		
		return dist;
	}
	
	public double getDist(vec hookpos, vec playerpos) {
		segments.set(0, hookpos);
		segments.set(segments.size() - 1, playerpos);
		double dist = 0;
		for (int i = 0; i < segments.size() - 1; i++) {
			dist += segments.get(i).sub(segments.get(i+1)).length();
		}
		
		return dist;
	}
}
