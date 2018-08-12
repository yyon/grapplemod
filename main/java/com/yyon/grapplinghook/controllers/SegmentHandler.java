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
	
	final double bendoffset = 0.05;
	final double intoblock = 0.05;
	
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
	
	public void updatepos(vec hookpos, vec playerpos, double ropelen) {
		segments.set(0, hookpos);
		segments.set(segments.size() - 1, playerpos);
		this.ropelen = ropelen;
	}
	
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
//					System.out.println("removed farthest");
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
		updatesegment(closest, prevclosest, playerpos, prevplayerpos, segments.size() - 1, 0);
		
		if (movinghook) {
			farthest = segments.get(1);
			vec prevfarthest = farthest;
			if (segments.size() == 2) {
				prevfarthest = prevplayerpos;
			}
			updatesegment(hookpos, prevhookpos, farthest, prevfarthest, 1, 0);
		}
		
        prevhookpos = hookpos;
        prevplayerpos = playerpos;
	}
	
	public void removesegment(int index) {
/*		System.out.println("removed segment");*/
		
		segments.remove(index);
		segmentbottomsides.remove(index);
		segmenttopsides.remove(index);

		if (!this.world.isRemote) {
			SegmentMessage addmessage = new SegmentMessage(this.arrow.getEntityId(), false, index, new vec(0, 0, 0), EnumFacing.DOWN, EnumFacing.DOWN);
			vec playerpoint = vec.positionvec(this.arrow.shootingEntity);
			grapplemod.network.sendToAllAround(addmessage, new TargetPoint(this.world.provider.getDimension(), playerpoint.x, playerpoint.y, playerpoint.z, 100));
		}
	}
	
	public void updatesegment(vec top, vec prevtop, vec bottom, vec prevbottom, int index, int numberrecursions) {
        RayTraceResult bottomraytraceresult = this.world.rayTraceBlocks(bottom.toVec3d(), top.toVec3d());
        
        // if rope hit block
        if (bottomraytraceresult != null)
        {
//        	System.out.println(bottomraytraceresult.typeOfHit);
            vec bottomhitvec = new vec(bottomraytraceresult.hitVec.x, bottomraytraceresult.hitVec.y, bottomraytraceresult.hitVec.z);
/*            this.arrow.debugpos = bottomhitvec;
            this.arrow.debugpos2 = bottom;
            this.arrow.debugpos3 = top;*/
            EnumFacing bottomside = bottomraytraceresult.sideHit;
            vec bottomnormal = this.getnormal(bottomside);
            
            // calculate where bottomhitvec was along the rope in the previous tick
//            double ropelen = top.sub(bottom).length();
            double prevropelen = prevtop.sub(prevbottom).length();
            
//            double bottomtohit = bottom.sub(bottomhitvec).length();
//            double prevbottomtohit = bottomtohit * ropelen / prevropelen;
            
//            vec prevbottomhit = prevtop.sub(prevbottom).changelen(prevbottomtohit).add(prevbottom);
            
            // use prevbottomhit to calculate the velocity of that part of the rope when it hit the block
 //           vec motionalonghit = bottomhitvec.sub(prevbottomhit);
            
            // calculate the motion parallel to the block side
//            vec motionparallel = motionalonghit.removealong(bottomnormal);
            
            // the rope must have hit the corner on the plane across the edge of the block
            // and is bounded by the quadrilateral top, prevtop, prevbottom, bottom
            vec cornerbound1 = bottomhitvec.add(bottomnormal.changelen(-intoblock));
            
            vec cornerbound2 = null;
            double cornerlinedist = Double.POSITIVE_INFINITY;
            
            vec bound_option1 = line_plane_intersection(prevtop, prevbottom, cornerbound1, bottomnormal);
            if (/*cornerbound1.sub(bound_option1).dot(motionparallel) > 0 &&*/ cornerbound1.sub(bound_option1).length() < cornerlinedist) {
            	cornerbound2 = bound_option1;
            	cornerlinedist = cornerbound1.sub(bound_option1).length();
            }
            vec bound_option2 = line_plane_intersection(top, prevtop, cornerbound1, bottomnormal);
            if (/*cornerbound1.sub(bound_option2).dot(motionparallel) > 0 &&*/ cornerbound1.sub(bound_option2).length() < cornerlinedist) {
            	cornerbound2 = bound_option2;
            	cornerlinedist = cornerbound1.sub(bound_option2).length();
            }
            vec bound_option3 = line_plane_intersection(prevbottom, bottom, cornerbound1, bottomnormal);
            if (/*cornerbound1.sub(bound_option3).dot(motionparallel) > 0 &&*/ cornerbound1.sub(bound_option3).length() < cornerlinedist) {
            	cornerbound2 = bound_option3;
            	cornerlinedist = cornerbound1.sub(bound_option3).length();
            }
            
            if (cornerbound2 != null) {
            	// the corner must be in the line (cornerbound2, cornerbound1)
                RayTraceResult cornerraytraceresult = this.world.rayTraceBlocks(cornerbound2.toVec3d(), cornerbound1.toVec3d());
                if (cornerraytraceresult != null) {
                	vec cornerhitpos = new vec(cornerraytraceresult.hitVec.x, cornerraytraceresult.hitVec.y, cornerraytraceresult.hitVec.z);
                	EnumFacing cornerside = cornerraytraceresult.sideHit;
                	
                	if (cornerside == bottomside || 
                			cornerside.getOpposite() == bottomside) {
                		// this should not happen
//                		System.out.println("Warning: corner is same or opposite of bottomside");
                		return;
                	} else {
                		// add a bend around the corner
                		vec actualcorner = cornerhitpos.add(bottomnormal.changelen(intoblock));
                		vec bend = actualcorner.add(bottomnormal.changelen(bendoffset)).add(getnormal(cornerside).changelen(bendoffset));
                		vec topropevec = bend.sub(top);
                		vec bottomropevec = bend.sub(bottom);
                		
                		if (topropevec.length() > 0.05 && bottomropevec.length() > 0.05) { // ignore bends that are too close to another bend
                    		this.actuallyaddsegment(index, bend, bottomside, cornerside);
                    		
                    		// if not enough rope length left, undo
                    		if(this.getDistToAnchor() + .2 > this.ropelen) {
//                    			System.out.println("Warning: not enough length left, removing");
                    			this.removesegment(index);
                    			return;
                    		}
                    		
                    		// now to recurse on top section of rope
                    		double newropelen = topropevec.length() + bottomropevec.length();
                    		
                    		double prevtoptobend = topropevec.length() * prevropelen / newropelen;
                    		vec prevbend = prevtop.add(prevbottom.sub(prevtop).changelen(prevtoptobend));
                    		
                    		if (numberrecursions < 10) {
                        		updatesegment(top, prevtop, bend, prevbend, index, numberrecursions+1);
                    		} else {
                    			System.out.println("Warning: number recursions exceeded");
                    		}
                		} else {
//                			System.out.println("Warning: bends are too close");
                		}
                	}
                } else {
//                	System.out.println("Warning: no corner collision");
                }
            } else {
//            	System.out.println("Warning: cornerbound2 is null");
            }
            
            
            
/*            RayTraceResult topraytraceresult = this.world.rayTraceBlocks(top.toVec3d(), bottom.toVec3d());
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
            }*/
        }
	}
	
	public vec line_plane_intersection(vec linepoint1, vec linepoint2, vec planepoint, vec planenormal) {
		// calculate the intersection of a line and a plane
		// formula: https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection#Algebraic_form
		
		vec linevec = linepoint2.sub(linepoint1);
		
		double d = planepoint.sub(linepoint1).dot(planenormal) / linevec.dot(planenormal);
		return linepoint1.add(linevec.mult(d));
	}
	
	public vec getnormal(EnumFacing facing) {
		Vec3i facingvec = facing.getDirectionVec();
		return new vec(facingvec.getX(), facingvec.getY(), facingvec.getZ());
	}
	
/*	public void addsegment(vec bottomhit, vec tophit, EnumFacing bottomside, EnumFacing topside, int index, vec top, vec prevtop, vec bottom, vec prevbottom) {
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
	}*/
	
	public void actuallyaddsegment(int index, vec bendpoint, EnumFacing bottomside, EnumFacing topside) {
        segments.add(index, bendpoint);
        segmentbottomsides.add(index, bottomside);
        segmenttopsides.add(index, topside);

        /*System.out.println("added segment");
		this.print();*/
		
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
