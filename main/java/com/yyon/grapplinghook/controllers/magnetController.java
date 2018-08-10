package com.yyon.grapplinghook.controllers;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.yyon.grapplinghook.grapplemod;
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

public class magnetController extends grappleController {
	public int repelconf = 0;
	public final double playermovementmult = 1.5;
		
	public magnetController(int arrowId, int entityId, World world, vec pos, double maxlen, int id, int repelconf) {
		super(arrowId, entityId, world, pos, maxlen, id);
		this.repelconf = repelconf;
	}

	public void receiveGrappleClick(boolean leftclick) {
		super.receiveGrappleClick(leftclick);
		
	}
	
	HashMap<BlockPos, Boolean> blockcache = new HashMap<BlockPos, Boolean>();
	
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null) {
				if (true) {
					this.normalGround();
					this.normalCollisions();
//					this.applyAirFriction();
					
					vec arrowpos = this.pos;
					vec playerpos = vec.positionvec(entity);
					
					
					vec oldspherevec = playerpos.sub(arrowpos);
					vec spherevec = oldspherevec.changelen(r);
					vec spherechange = spherevec.sub(oldspherevec);
					
					vec additionalmotion;
					if (arrowpos.sub(playerpos).length() < this.r) {
						additionalmotion = new vec(0,0,0);
					} else {
						additionalmotion = spherechange;
					}
					
					double dist = oldspherevec.length();
					this.calctaut(dist);
					
					boolean domagnet = true;
					
					if (entity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) entity;
						if (this.isjumping()) {
							this.dojump(player, spherevec);
							return;
						} else if (grapplemod.proxy.isSneaking(entity)) {
							domagnet = false;
							if (arrowpos.y > playerpos.y) {
								vec motiontorwards = spherevec.changelen(-0.1);
								motiontorwards = new vec(motiontorwards.x, 0, motiontorwards.z);
								if (motion.dot(motiontorwards) < 0) {
									motion.add_ip(motiontorwards);
								}
								
								vec newmotion = dampenmotion(motion, motiontorwards);
								motion = new vec(newmotion.x, motion.y, newmotion.z);
								
								if (this.playerforward != 0) {
										if (dist < maxlen || this.playerforward > 0 || maxlen == 0) {
											additionalmotion = new vec(0, this.playerforward, 0);
											this.r = dist;
											this.r -= this.playerforward*0.3;
											if (this.r < 0) {
												this.r = dist;
											}
										}
								}
							}
						} else {
							applyPlayerMovement();
						}
					}
					
					if (domagnet && this.repelconf != grapplemod.REPELNONE) {
						vec blockpush = check(playerpos, entity.world);
						
//						if (this.repelconf == grapplemod.REPELSPEED) {
//							blockpush.mult_ip(0.5 + motion.length()/2);//0.5);//0.005);
//							System.out.println();
						if (this.repelconf == grapplemod.REPELSTRONG) {
							blockpush.mult_ip(1.5);
						} else if (this.repelconf == grapplemod.REPELWEAK) {
							blockpush.mult_ip(0.5);
						}
//						blockpush.mult_ip(motion.length());
						
//						blockpush.print();
//			        	vec facing = new vec(entity.getLookVec());
//						blockpush.removealong(facing);
						this.motion.add_ip(blockpush);
					}
						
					if (!entity.onGround) {
						motion.add_ip(0, -0.05, 0);
					}
					
					vec newmotion = motion.add(additionalmotion);
					
					if (arrowpos.sub(playerpos.add(motion)).length() > r) { // moving away
						motion = motion.removealong(spherevec);
					}
					
					entity.motionX = newmotion.x;
					entity.motionY = newmotion.y;
					entity.motionZ = newmotion.z;
					
					this.updateServerPos();
				}
			}
		}
	}
	
    public final int radius = 10;
    public final double maxpush = 0.3;//0.25;
	
    public vec check(vec p, World w) {
//    	long startTime = System.nanoTime();
    	
    	p = p.add(0.0, 0.75, 0.0);
    	vec v = new vec(0, 0, 0);
    	
    	/*
    	for (int x = (int)p.x - radius; x <= (int)p.x + radius; x++) {
        	for (int y = (int)p.y - radius; y <= (int)p.y + radius; y++) {
            	for (int z = (int)p.z - radius; z <= (int)p.z + radius; z++) {
			    	BlockPos pos = new BlockPos(x, y, z);
			    	if (pos != null) {
				    	if (hasblock(pos, w)) {
				    		vec blockvec = new vec(((double) x)+0.5, ((double) y)+0.5, ((double) z)+0.5);
				    		blockvec.sub_ip(p);
				    		blockvec.changelen_ip(-1 / Math.pow(blockvec.length(), 2));
				    		v.add_ip(blockvec);
				    	}
			    	}
            	}
	    	}
    	}
    	*/
    	
    	double t = (1.0 + Math.sqrt(5.0)) / 2.0;
    	
		BlockPos pos = new BlockPos(Math.floor(p.x), Math.floor(p.y), Math.floor(p.z));
		if (hasblock(pos, w)) {
			v.add_ip(0, 1, 0);
		} else {
	    	v.add_ip(vecdist(p, new vec(-1,  t,  0), w));
	    	v.add_ip(vecdist(p, new vec( 1,  t,  0), w));
	    	v.add_ip(vecdist(p, new vec(-1, -t,  0), w));
	    	v.add_ip(vecdist(p, new vec( 1, -t,  0), w));
	    	v.add_ip(vecdist(p, new vec( 0, -1,  t), w));
	    	v.add_ip(vecdist(p, new vec( 0,  1,  t), w));
	    	v.add_ip(vecdist(p, new vec( 0, -1, -t), w));
	    	v.add_ip(vecdist(p, new vec( 0,  1, -t), w));
	    	v.add_ip(vecdist(p, new vec( t,  0, -1), w));
	    	v.add_ip(vecdist(p, new vec( t,  0,  1), w));
	    	v.add_ip(vecdist(p, new vec(-t,  0, -1), w));
	    	v.add_ip(vecdist(p, new vec(-t,  0,  1), w));
		}
    	
    	if (v.length() > maxpush) {
    		v.changelen_ip(maxpush);
    	}
    	
//    	long endTime = System.nanoTime();

//    	long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
//    	System.out.println(duration);
    	
		return v;
	}
    
    public vec vecdist(vec p, vec v, World w) {
    	for (double i = 0.5; i < 10; i += 0.5) {
    		vec v2 = v.changelen(i);
    		BlockPos pos = new BlockPos(Math.floor(p.x + v2.x), Math.floor(p.y + v2.y), Math.floor(p.z + v2.z));
    		if (hasblock(pos, w)) {
    			vec v3 = new vec(pos.getX() + 0.5 - p.x, pos.getY() + 0.5 - p.y, pos.getZ() + 0.5 - p.z);
    			v3.changelen_ip(-1 / Math.pow(v3.length(), 2));
    			return v3;
    		}
    	}
    	
    	return new vec(0, 0, 0);
    }

	public boolean hasblock(BlockPos pos, World w) {
//    	if (!blockcache.containsKey(pos)) {
    		boolean isblock = false;
	    	IBlockState blockstate = w.getBlockState(pos);
	    	Block b = blockstate.getBlock();
	    	if (!(b.isAir(blockstate, w, pos))) {
	    		isblock = true;
	    	}
			
//			blockcache.put(pos, (Boolean) isblock);
	    	return isblock;
//    	} else {
//    		return blockcache.get(pos);
//    	}
	}

}
