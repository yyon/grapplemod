package com.yyon.grapplinghook.entities;

import java.util.HashMap;

import javax.annotation.Nonnull;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;
import com.yyon.grapplinghook.network.GrappleAttachMessage;
import com.yyon.grapplinghook.network.GrappleAttachPosMessage;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

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

public class grappleArrow extends ProjectileItemEntity implements IEntityAdditionalSpawnData {
	public grappleArrow(EntityType<? extends grappleArrow> type, World world) {
		super(type, world);

		this.segmenthandler = new SegmentHandler(this.level, this, vec.positionvec(this), vec.positionvec(this));
		this.customization = new GrappleCustomization();
	}

	public grappleArrow(World world, LivingEntity shooter,
			boolean righthand, GrappleCustomization customization, boolean isdouble) {
		super(grapplemod.grappleArrowType, shooter.position().x, shooter.position().y + shooter.getEyeHeight(), shooter.position().z, world);
		
		this.shootingEntity = shooter;
		this.shootingEntityID = this.shootingEntity.getId();
		
		this.isdouble = isdouble;
		
		vec pos = vec.positionvec(this.shootingEntity).add(new vec(0, this.shootingEntity.getEyeHeight(), 0));

		this.segmenthandler = new SegmentHandler(this.level, this, new vec(pos), new vec(pos));

		this.customization = customization;
		this.r = customization.maxlen;
		
		this.righthand = righthand;
	}

	public Entity shootingEntity = null;
	public int shootingEntityID;
	
	private boolean firstattach = false;
	public vec thispos;
	
	public boolean righthand = true;
	
	public boolean attached = false;
	
	public double pull;
	
	public double taut = 1;
	
	public boolean ignoreFrustumCheck = true;
	
	public boolean isdouble = false;
	
//	public double maxlen = 20;
	public double r;
	
	public SegmentHandler segmenthandler = null;
	
	public GrappleCustomization customization = null;
	
/*	public vec debugpos = null;
	public vec debugpos2 = null;
	public vec debugpos3 = null;*/
	
	// magnet attract
	public vec prevpos = null;
	public boolean foundblock = false;
	public boolean wasinair = false;
	public BlockPos magnetblock = null;
	
	@Override
    public void writeSpawnData(PacketBuffer data)
    {
	    data.writeInt(this.shootingEntity != null ? this.shootingEntity.getId() : 0);
	    data.writeBoolean(this.righthand);
	    data.writeBoolean(this.isdouble);
	    if (this.customization == null) {
	    	System.out.println("error: customization null");
	    }
	    this.customization.writeToBuf(data);
    }
	
	@Override
    public void readSpawnData(PacketBuffer data)
    {
    	this.shootingEntityID = data.readInt();
	    this.shootingEntity = this.level.getEntity(this.shootingEntityID);
	    this.righthand = data.readBoolean();
	    this.isdouble = data.readBoolean();
	    this.customization = new GrappleCustomization();
	    this.customization.readFromBuf(data);
    }

	@Override
	public void defineSynchedData() {
		super.defineSynchedData();
	}
	
	public void removeServer() {
		this.remove();
		this.shootingEntityID = 0;
	}

	public float getVelocity() {
        return (float) this.customization.throwspeed;
    }
	
	@Override
	public void tick() {
		if (this.shootingEntityID == 0 || this.shootingEntity == null) { // removes ghost grappling hooks
			this.remove();
		}
		
		if (this.firstattach) {
			this.setDeltaMovement(0, 0, 0);
			this.firstattach = false;
			super.setPos(this.thispos.x, this.thispos.y, this.thispos.z);
		}
		
		if (this.attached) {
			this.setDeltaMovement(0, 0, 0);
		}
		
		super.tick();
		
		if (!this.level.isClientSide) {
			if (this.shootingEntity != null)  {
				if (!this.attached) {
					if (this.segmenthandler.hookpastbend(this.r)) {
						System.out.println("around bend");
						vec farthest = this.segmenthandler.getfarthest();
						this.serverAttach(this.segmenthandler.getbendblock(1), farthest, null);
					}
					
					if (!this.customization.phaserope) {
						this.segmenthandler.update(vec.positionvec(this), vec.positionvec(this.shootingEntity).add(new vec(0, this.shootingEntity.getEyeHeight(), 0)), this.r, true);
						
						if (this.customization.sticky) {
							if (this.segmenthandler.segments.size() > 2) {
								int bendnumber = this.segmenthandler.segments.size() - 2;
								vec closest = this.segmenthandler.segments.get(bendnumber);
								this.serverAttach(this.segmenthandler.getbendblock(bendnumber), closest, null);
							}
						}
					} else {
						this.segmenthandler.updatepos(vec.positionvec(this), vec.positionvec(this.shootingEntity).add(new vec(0, this.shootingEntity.getEyeHeight(), 0)), this.r);
					}
					
					vec farthest = this.segmenthandler.getfarthest();
					double distToFarthest = this.segmenthandler.getDistToFarthest();
					
					vec ropevec = vec.positionvec(this).sub(farthest);
					double d = ropevec.length();
					
					if (this.customization.reelin && this.shootingEntity.isCrouching()) {
						double newdist = d + distToFarthest - 0.4;
						if (newdist > 1 && newdist <= this.customization.maxlen) {
							this.r = newdist;
						}
					}


					if (d + distToFarthest > this.r) {
						vec motion = vec.motionvec(this);
						
						if (motion.dot(ropevec) > 0) {
							motion = motion.removealong(ropevec);
						}
						
						this.setVelocityActually(motion.x, motion.y, motion.z);
						
						ropevec.changelen_ip(this.r - distToFarthest);
						vec newpos = ropevec.add(farthest);
						
						this.setPos(newpos.x, newpos.y, newpos.z);
					}
					
				}
			}
		}
		
		// magnet attraction
		if (this.customization.attract && vec.positionvec(this).sub(vec.positionvec(this.shootingEntity)).length() > this.customization.attractradius) {
	    	if (this.shootingEntity == null) {return;}
	    	if (!this.foundblock) {
	    		if (!this.level.isClientSide) {
	    			vec playerpos = vec.positionvec(this.shootingEntity);
	    			vec pos = vec.positionvec(this);
	    			if (magnetblock == null) {
		    			if (prevpos != null) {
			    			HashMap<BlockPos, Boolean> checkedset = new HashMap<BlockPos, Boolean>();
			    			vec vector = pos.sub(prevpos);
			    			vec normvector = vector.normalize();
			    			for (int i = 0; i < vector.length(); i++) {
			    				double dist = prevpos.sub(playerpos).length();
			    				int radius = (int) dist / 4;
			    				BlockPos found = this.check(prevpos, checkedset);
			    				if (found != null) {
//			    					if (wasinair) {
							    		vec distvec = new vec(found.getX(), found.getY(), found.getZ());
							    		distvec.sub_ip(prevpos);
							    		if (distvec.length() < radius) {
					    					this.setPosAndOldPos(prevpos.x, prevpos.y, prevpos.z);
					    					pos = prevpos;
					    					
					    					magnetblock = found;
					    					
					    					break;
							    		}
//			    					}
			    				} else {
			    					wasinair = true;
			    				}
			    				
			    				prevpos.add_ip(normvector);
			    			}
		    			}
	    			}
	    			
	    			if (magnetblock != null) {
				    	BlockState blockstate = this.level.getBlockState(magnetblock);
				    	VoxelShape BB = blockstate.getCollisionShape(this.level, magnetblock);

						vec blockvec = new vec(magnetblock.getX() + (BB.max(Axis.X) + BB.min(Axis.X)) / 2, magnetblock.getY() + (BB.max(Axis.Y) + BB.min(Axis.Y)) / 2, magnetblock.getZ() + (BB.max(Axis.Z) + BB.min(Axis.Z)) / 2);
						vec newvel = blockvec.sub(pos);
						
						double l = newvel.length();
						
						newvel.changelen(this.getVelocity());
						
						this.setDeltaMovement(newvel.x, newvel.y, newvel.z);
						
						if (l < 0.2) {
							this.serverAttach(magnetblock, blockvec, Direction.UP);
						}
	    			}
	    			
	    			prevpos = pos;
	    		}
	    	}
		}
	}
	
	public void setVelocityActually(double x, double y, double z) {
		this.setDeltaMovement(x, y, z);

        if (this.xRotO == 0.0F && this.yRotO == 0.0F)
        {
            float f = MathHelper.sqrt(x * x + z * z);
            this.yRot = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
            this.xRot = (float)(MathHelper.atan2(y, (double)f) * (180D / Math.PI));
            this.yRotO = this.yRot;
            this.xRotO = this.xRot;
        }
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
		return true;
	}

	@Override
	public boolean shouldRender(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
		return true;
	}
	
	@Override
	public AxisAlignedBB getBoundingBoxForCulling() {
		return this.segmenthandler.getBoundingBox(vec.positionvec(this), vec.positionvec(this.shootingEntity).add(new vec(0, this.shootingEntity.getEyeHeight(), 0)));
	}

	@Override
	protected void onHit(RayTraceResult movingobjectposition) {
		if (!this.level.isClientSide) {
			if (this.attached) {
				return;
			}
			if (this.shootingEntityID != 0) {
				if (movingobjectposition == null) {
					return;
				}
				
				vec vec3d = vec.positionvec(this);
		        vec vec3d1 = vec3d.add(vec.motionvec(this));

				if (movingobjectposition instanceof EntityRayTraceResult && !GrappleConfig.getconf().hookaffectsentities) {
					onHit(grapplemod.rayTraceBlocks(this.level, vec3d, vec3d1));
			        return;
				}
				
				BlockRayTraceResult blockhit = null;
				if (movingobjectposition instanceof BlockRayTraceResult) {
					blockhit = (BlockRayTraceResult) movingobjectposition;
				}
				
				if (blockhit != null) {
					BlockPos blockpos = blockhit.getBlockPos();
					if (blockpos != null) {
						Block block = this.level.getBlockState(blockpos).getBlock();
						if (grapplemod.breaksblock(block)) {
							this.level.destroyBlock(blockpos, true);
					        onHit(grapplemod.rayTraceBlocks(this.level, vec3d, vec3d1));
					        return;
						}
					}
				}
				
				if (movingobjectposition instanceof EntityRayTraceResult) {
					// hit entity
					EntityRayTraceResult entityHit = (EntityRayTraceResult) movingobjectposition;
					Entity entity = entityHit.getEntity();
					if (entity == this.shootingEntity) {
						return;
					}
					
					vec playerpos = vec.positionvec(this.shootingEntity);
					vec entitypos = vec.positionvec(entity);
					vec yank = playerpos.sub(entitypos).mult(0.4);
					yank.y = Math.min(yank.y, 2);
					vec newmotion = vec.motionvec(entity).add(yank);
					entity.setDeltaMovement(newmotion.toVec3d());
					
					this.removeServer();
					return;
				} else if (blockhit != null) {
					BlockPos blockpos = blockhit.getBlockPos();
					
					vec vec3 = new vec(movingobjectposition.getLocation());

					this.serverAttach(blockpos, vec3, blockhit.getDirection());
				} else {
					System.out.println("unknown impact?");
				}
			}
		}
	}

	@Override
	protected Item getDefaultItem() {
		return grapplemod.grapplebowitem;
	}
	
	public void serverAttach(BlockPos blockpos, vec pos, Direction sideHit) {
		if (this.attached) {
			return;
		}
		this.attached = true;
		
		if (blockpos != null) {
			Block block = this.level.getBlockState(blockpos).getBlock();

			if (!grapplemod.attachesblock(block)) {
				this.removeServer();
				return;
			}
		}
		
		vec vec3 = vec.positionvec(this);
		vec3.add_ip(vec.motionvec(this));
		if (pos != null) {
            vec3 = pos;
            
            this.setPosAndOldPos(vec3.x, vec3.y, vec3.z);
		}
		
		//west -x
		//north -z
		vec curpos = vec.positionvec(this);
		if (sideHit == Direction.DOWN) {
			curpos.y -= 0.3;
		} else if (sideHit == Direction.WEST) {
			curpos.x -= 0.05;
		} else if (sideHit == Direction.NORTH) {
			curpos.z -= 0.05;
		} else if (sideHit == Direction.SOUTH) {
			curpos.z += 0.05;
		} else if (sideHit == Direction.EAST) {
			curpos.x += 0.05;
		} else if (sideHit == Direction.UP) {
			curpos.y += 0.05;
		}
		curpos.setpos(this);
		
		this.setDeltaMovement(0, 0, 0);
        
        this.thispos = vec.positionvec(this);
		this.firstattach = true;
		grapplemod.attached.add(this.shootingEntityID);
		
		grapplemod.sendtocorrectclient(new GrappleAttachMessage(this.getId(), this.position().x, this.position().y, this.position().z, this.getControlId(), this.shootingEntityID, blockpos, this.segmenthandler.segments, this.segmenthandler.segmenttopsides, this.segmenthandler.segmentbottomsides, this.customization), this.shootingEntityID, this.level);
		
		GrappleAttachPosMessage msg = new GrappleAttachPosMessage(this.getId(), this.position().x, this.position().y, this.position().z);
		grapplemod.network.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(new BlockPos(this.position().x, this.position().y, this.position().z))), msg);
	}
	
	public void clientAttach(double x, double y, double z) {
		this.setAttachPos(x, y, z);
		
		if (this.shootingEntity instanceof PlayerEntity) {
			grapplemod.proxy.resetlaunchertime(this.shootingEntityID);
		}
	}
	
	
	@Override
	protected float getGravity() {
		if (this.attached) {
			return 0.0F;
		}
        return (float) this.customization.hookgravity * 0.1F;
    }
	
	public int getControlId() {
		return grapplemod.GRAPPLEID;
	}

	public void setAttachPos(double x, double y, double z) {
		this.setPosAndOldPos(x, y, z);

		this.setDeltaMovement(0, 0, 0);
		this.firstattach = true;
		this.attached = true;
        this.thispos = new vec(x, y, z);
	}
	
	// used for magnet attraction
    public BlockPos check(vec p, HashMap<BlockPos, Boolean> checkedset) {
    	int radius = (int) Math.floor(this.customization.attractradius);
    	BlockPos closestpos = null;
    	double closestdist = 0;
    	for (int x = (int)p.x - radius; x <= (int)p.x + radius; x++) {
        	for (int y = (int)p.y - radius; y <= (int)p.y + radius; y++) {
            	for (int z = (int)p.z - radius; z <= (int)p.z + radius; z++) {
			    	BlockPos pos = new BlockPos(x, y, z);
			    	if (pos != null) {
				    	if (hasblock(pos, checkedset)) {
				    		vec distvec = new vec(pos.getX(), pos.getY(), pos.getZ());
				    		distvec.sub_ip(p);
				    		double dist = distvec.length();
				    		if (closestpos == null || dist < closestdist) {
				    			closestpos = pos;
				    			closestdist = dist;
				    		}
				    	}
			    	}
            	}
	    	}
    	}
		return closestpos;
	}

	// used for magnet attraction
	public boolean hasblock(BlockPos pos, HashMap<BlockPos, Boolean> checkedset) {
    	if (!checkedset.containsKey(pos)) {
    		boolean isblock = false;
	    	BlockState blockstate = this.level.getBlockState(pos);
	    	Block b = blockstate.getBlock();
			if (!grapplemod.attachesblock(b)) {
		    	if (!(b.isAir(blockstate, this.level, pos))) {
			    	VoxelShape BB = blockstate.getCollisionShape(this.level, pos);
			    	if (BB != null && !BB.isEmpty()) {
			    		isblock = true;
			    	}
		    	}
			}
			
	    	checkedset.put(pos, (Boolean) isblock);
	    	return isblock;
    	} else {
    		return checkedset.get(pos);
    	}
	}
	
	@Nonnull
	@Override
	public IPacket<?> getAddEntityPacket() {
		  return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(this.getDefaultItem());
	}
}
