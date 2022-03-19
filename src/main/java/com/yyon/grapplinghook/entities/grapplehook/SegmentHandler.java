package com.yyon.grapplinghook.entities.grapplehook;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.network.SegmentMessage;
import com.yyon.grapplinghook.utils.GrapplemodUtils;
import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;

import java.util.LinkedList;

public class SegmentHandler {

	public LinkedList<Vec> segments;
	public LinkedList<Direction> segmentBottomSides;
	public LinkedList<Direction> segmentTopSides;
	public Level world;
	public GrapplehookEntity hookEntity;
	
	Vec prevHookPos = null;
	Vec prevPlayerPos = null;;
	
	final double bendOffset = 0.05;
	final double intoBlock = 0.05;
	
	public SegmentHandler(Level w, GrapplehookEntity hookEntity, Vec hookpos, Vec playerpos) {
		segments = new LinkedList<Vec>();
		segments.add(hookpos);
		segments.add(playerpos);
		segmentBottomSides = new LinkedList<Direction>();
		segmentBottomSides.add(null);
		segmentBottomSides.add(null);
		segmentTopSides = new LinkedList<Direction>();
		segmentTopSides.add(null);
		segmentTopSides.add(null);
		this.world = w;
		this.hookEntity = hookEntity;
		this.prevHookPos = new Vec(hookpos);
		this.prevPlayerPos = new Vec(playerpos);
	}
	
	public void forceSetPos(Vec hookpos, Vec playerpos) {
		this.prevHookPos = new Vec(hookpos);
		this.prevPlayerPos = new Vec(playerpos);
    	this.segments.set(0, new Vec(hookpos));
    	this.segments.set(this.segments.size() - 1, new Vec(playerpos));
	}
	
	double ropeLen;
	
	public void updatePos(Vec hookpos, Vec playerpos, double ropelen) {
		segments.set(0, hookpos);
		segments.set(segments.size() - 1, playerpos);
		this.ropeLen = ropelen;
	}
	
	public void update(Vec hookpos, Vec playerpos, double ropelen, boolean movinghook) {
		if (prevHookPos == null) {
	        prevHookPos = hookpos;
	        prevPlayerPos = playerpos;
		}
		
		segments.set(0, hookpos);
		segments.set(segments.size() - 1, playerpos);
		this.ropeLen = ropelen;
		
		
		Vec closest = segments.get(segments.size()-2);
		
		while (true) {
			if (segments.size() == 2) {
				break;
			}
			
			int index = segments.size()-2;
			closest = segments.get(index);
			Direction bottomside = segmentBottomSides.get(index);
			Direction topside = segmentTopSides.get(index);
			Vec ropevec = playerpos.sub(closest);
			
			Vec beforepoint = segments.get(index-1);
			
			Vec edgevec = getNormal(bottomside).cross(getNormal(topside));
			Vec planenormal = beforepoint.sub(closest).cross(edgevec);
			
			if (ropevec.dot(planenormal) > 0) {
				this.removeSegment(index);
			} else {
				break;
			}
		}
		
		Vec farthest = segments.get(1);
		
		if (movinghook) {
			while (true) {
				if (segments.size() == 2) {
					break;
				}
				
				int index = 1;
				farthest = segments.get(index);
				Direction bottomside = segmentBottomSides.get(index);
				Direction topside = segmentTopSides.get(index);
				Vec ropevec = farthest.sub(hookpos);
				
				Vec beforepoint = segments.get(index+1);
				
				Vec edgevec = getNormal(bottomside).cross(getNormal(topside));
				Vec planenormal = beforepoint.sub(farthest).cross(edgevec);
				
				if (ropevec.dot(planenormal) > 0 || ropevec.length() < 0.1) {
					this.removeSegment(index);
				} else {
					break;
				}
			}
			
			while (true) {
				if (this.getDistToFarthest() > ropelen) {
					this.removeSegment(1);
				} else {
					break;
				}
			}
		}
		
		if (movinghook) {
			farthest = segments.get(1);
			Vec prevfarthest = farthest;
			if (segments.size() == 2) {
				prevfarthest = prevPlayerPos;
			}
			updateSegment(hookpos, prevHookPos, farthest, prevfarthest, 1, 0);
		}
		
		Vec prevclosest = closest;
		if (segments.size() == 2) {
			prevclosest = prevHookPos;
		}
		updateSegment(closest, prevclosest, playerpos, prevPlayerPos, segments.size() - 1, 0);
		
		
        prevHookPos = hookpos;
        prevPlayerPos = playerpos;
	}
	
	public void removeSegment(int index) {
		segments.remove(index);
		segmentBottomSides.remove(index);
		segmentTopSides.remove(index);

		if (!this.world.isClientSide) {
			SegmentMessage addmessage = new SegmentMessage(this.hookEntity.getId(), false, index, new Vec(0, 0, 0), Direction.DOWN, Direction.DOWN);
			Vec playerpoint = Vec.positionVec(this.hookEntity.shootingEntity);
			CommonSetup.network.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(new BlockPos(playerpoint.x, playerpoint.y, playerpoint.z))), addmessage);
		}
	}
	
	public void updateSegment(Vec top, Vec prevtop, Vec bottom, Vec prevbottom, int index, int numberrecursions) {		
		BlockHitResult bottomraytraceresult = GrapplemodUtils.rayTraceBlocks(this.world, bottom, top);
        
        // if rope hit block
        if (bottomraytraceresult != null) {
        	if (GrapplemodUtils.rayTraceBlocks(this.world, prevbottom, prevtop) != null) {
        		return;
        	}
        	
            Vec bottomhitvec = new Vec(bottomraytraceresult.getLocation());

            Direction bottomside = bottomraytraceresult.getDirection();
            Vec bottomnormal = this.getNormal(bottomside);
            
            // calculate where bottomhitvec was along the rope in the previous tick
            double prevropelen = prevtop.sub(prevbottom).length();
            
            Vec cornerbound1 = bottomhitvec.add(bottomnormal.changeLen(-intoBlock));
            
            Vec bound_option1 = linePlaneIntersection(prevtop, prevbottom, cornerbound1, bottomnormal);
            Vec bound_option2 = linePlaneIntersection(top, prevtop, cornerbound1, bottomnormal);
            Vec bound_option3 = linePlaneIntersection(prevbottom, bottom, cornerbound1, bottomnormal);
            
            for (Vec cornerbound2 : new Vec[] {bound_option1, bound_option2, bound_option3}) {
            	if (cornerbound2 == null) {
            		continue;
            	}
            	
            	// the corner must be in the line (cornerbound2, cornerbound1)
            	BlockHitResult cornerraytraceresult = GrapplemodUtils.rayTraceBlocks(this.world, cornerbound2, cornerbound1);
                if (cornerraytraceresult != null) {
                	Vec cornerhitpos = new Vec(cornerraytraceresult.getLocation());
                	Direction cornerside = cornerraytraceresult.getDirection();
                	
                	if (cornerside == bottomside || 
                			cornerside.getOpposite() == bottomside) {
                		// this should not happen
//                		System.out.println("Warning: corner is same or opposite of bottomside");
                		continue;
                	} else {
                		// add a bend around the corner
                		Vec actualcorner = cornerhitpos.add(bottomnormal.changeLen(intoBlock));
                		Vec bend = actualcorner.add(bottomnormal.changeLen(bendOffset)).add(getNormal(cornerside).changeLen(bendOffset));
                		Vec topropevec = bend.sub(top);
                		Vec bottomropevec = bend.sub(bottom);
                		
                		// ignore bends that are too close to another bend
                		if (topropevec.length() < 0.05) {
                			if (this.segmentBottomSides.get(index - 1) == bottomside && this.segmentTopSides.get(index - 1) == cornerside) {
//                    			System.out.println("Warning: top bend is too close");
                    			continue;
                			}
                		}
                		if (bottomropevec.length() < 0.05) {
                			if (this.segmentBottomSides.get(index) == bottomside && this.segmentTopSides.get(index) == cornerside) {
//                    			System.out.println("Warning: bottom bend is too close");
                    			continue;
                			}
                		}
                		
                		this.actuallyAddSegment(index, bend, bottomside, cornerside);
                		
                		// if not enough rope length left, undo
                		if(this.getDistToAnchor() + .2 > this.ropeLen) {
//                			System.out.println("Warning: not enough length left, removing");
                			this.removeSegment(index);
                			continue;
                		}
                		
                		// now to recurse on top section of rope
                		double newropelen = topropevec.length() + bottomropevec.length();
                		
                		double prevtoptobend = topropevec.length() * prevropelen / newropelen;
                		Vec prevbend = prevtop.add(prevbottom.sub(prevtop).changeLen(prevtoptobend));
                		
                		if (numberrecursions < 10) {
                    		updateSegment(top, prevtop, bend, prevbend, index, numberrecursions+1);
                		} else {
                			System.out.println("Warning: number recursions exceeded");
                		}
                		break;
                	}
                }
            }
        }
	}
	
	public Vec linePlaneIntersection(Vec linepoint1, Vec linepoint2, Vec planepoint, Vec planenormal) {
		// calculate the intersection of a line and a plane
		// formula: https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection#Algebraic_form
		
		Vec linevec = linepoint2.sub(linepoint1);
		
		if (linevec.dot(planenormal) == 0) {
			return null;
		}
		
		double d = planepoint.sub(linepoint1).dot(planenormal) / linevec.dot(planenormal);
		return linepoint1.add(linevec.mult(d));
	}
	
	public Vec getNormal(Direction facing) {
		Vec3i facingvec = facing.getNormal();
		return new Vec(facingvec.getX(), facingvec.getY(), facingvec.getZ());
	}
	
	public boolean hookPastBend(double ropelen) {
		return (this.getDistToFarthest() > ropelen);
	}
	
	public BlockPos getBendBlock(int index) {
		Vec bendpos = this.segments.get(index);
		bendpos.add_ip(this.getNormal(this.segmentBottomSides.get(index)).changeLen(-this.intoBlock * 2));
		bendpos.add_ip(this.getNormal(this.segmentTopSides.get(index)).changeLen(-this.intoBlock * 2));
		return new BlockPos(bendpos.x, bendpos.y, bendpos.z);
	}
	
	public void actuallyAddSegment(int index, Vec bendpoint, Direction bottomside, Direction topside) {
        segments.add(index, bendpoint);
        segmentBottomSides.add(index, bottomside);
        segmentTopSides.add(index, topside);
        
		if (!this.world.isClientSide) {
			SegmentMessage addmessage = new SegmentMessage(this.hookEntity.getId(), true, index, bendpoint, topside, bottomside);
			Vec playerpoint = Vec.positionVec(this.hookEntity.shootingEntity);
			CommonSetup.network.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(new BlockPos(playerpoint.x, playerpoint.y, playerpoint.z))), addmessage);
		}
	}
	
	public void print() {
		for (int i = 1; i < segments.size() - 1; i++) {
			System.out.print(i);
			System.out.print(" ");
			System.out.print(segmentTopSides.get(i).toString());
			System.out.print(" ");
			System.out.print(segmentBottomSides.get(i).toString());
			System.out.print(" ");
			segments.get(i).print();
		}
	}
	
	public Vec getClosest(Vec hookpos) {
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
	
	public Vec getFarthest() {
		return segments.get(1);
	}
	
	public double getDistToFarthest() {
		double dist = 0;
		for (int i = 1; i < segments.size() - 1; i++) {
			dist += segments.get(i).sub(segments.get(i+1)).length();
		}
		
		return dist;
	}
	
	public double getDist(Vec hookpos, Vec playerpos) {
		segments.set(0, hookpos);
		segments.set(segments.size() - 1, playerpos);
		double dist = 0;
		for (int i = 0; i < segments.size() - 1; i++) {
			dist += segments.get(i).sub(segments.get(i+1)).length();
		}
		
		return dist;
	}
	
	public AABB getBoundingBox(Vec hookpos, Vec playerpos) {
		this.updatePos(hookpos, playerpos, this.ropeLen);
		Vec minvec = new Vec(hookpos);
		Vec maxvec = new Vec(hookpos);
		for (int i = 1; i < segments.size(); i++) {
			Vec segpos = segments.get(i);
			if (segpos.x < minvec.x) {
				minvec.x = segpos.x;
			} else if (segpos.x > maxvec.x) {
				maxvec.x = segpos.x;
			}
			if (segpos.y < minvec.y) {
				minvec.y = segpos.y;
			} else if (segpos.y > maxvec.y) {
				maxvec.y = segpos.y;
			}
			if (segpos.z < minvec.z) {
				minvec.z = segpos.z;
			} else if (segpos.z > maxvec.z) {
				maxvec.z = segpos.z;
			}
		}
		AABB bb = new AABB(minvec.x, minvec.y, minvec.z, maxvec.x, maxvec.y, maxvec.z);
		return bb;
	}
}
