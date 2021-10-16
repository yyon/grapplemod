package com.yyon.grapplinghook.entities.grapplehook;

import java.util.HashMap;

import javax.annotation.Nonnull;

import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.config.GrappleConfigUtils;
import com.yyon.grapplinghook.network.GrappleAttachMessage;
import com.yyon.grapplinghook.network.GrappleAttachPosMessage;
import com.yyon.grapplinghook.server.ServerControllerManager;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.GrapplemodUtils;
import com.yyon.grapplinghook.utils.Vec;

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

public class GrapplehookEntity extends ProjectileItemEntity implements IEntityAdditionalSpawnData {
	public GrapplehookEntity(EntityType<? extends GrapplehookEntity> type, World world) {
		super(type, world);

		this.segmentHandler = new SegmentHandler(this.level, this, Vec.positionVec(this), Vec.positionVec(this));
		this.customization = new GrappleCustomization();
	}

	public GrapplehookEntity(World world, LivingEntity shooter,
			boolean righthand, GrappleCustomization customization, boolean isdouble) {
		super(CommonSetup.grapplehookEntityType, shooter.position().x, shooter.position().y + shooter.getEyeHeight(), shooter.position().z, world);
		
		this.shootingEntity = shooter;
		this.shootingEntityID = this.shootingEntity.getId();
		
		this.isDouble = isdouble;
		
		Vec pos = Vec.positionVec(this.shootingEntity).add(new Vec(0, this.shootingEntity.getEyeHeight(), 0));

		this.segmentHandler = new SegmentHandler(this.level, this, new Vec(pos), new Vec(pos));

		this.customization = customization;
		this.r = customization.maxlen;
		
		this.rightHand = righthand;
	}

	public Entity shootingEntity = null;
	public int shootingEntityID;
	
	private boolean firstAttach = false;
	public Vec thisPos;
	
	public boolean rightHand = true;
	
	public boolean attached = false;
	
	public double pull;
	
	public double taut = 1;
	
	public boolean ignoreFrustumCheck = true;
	
	public boolean isDouble = false;
	
	public double r;
	
	public SegmentHandler segmentHandler = null;
	
	public GrappleCustomization customization = null;
	
	// magnet attract
	public Vec prevPos = null;
	public boolean foundBlock = false;
	public boolean wasInAir = false;
	public BlockPos magnetBlock = null;
	
	@Override
    public void writeSpawnData(PacketBuffer data)
    {
	    data.writeInt(this.shootingEntity != null ? this.shootingEntity.getId() : 0);
	    data.writeBoolean(this.rightHand);
	    data.writeBoolean(this.isDouble);
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
	    this.rightHand = data.readBoolean();
	    this.isDouble = data.readBoolean();
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
		
		if (this.firstAttach) {
			this.setDeltaMovement(0, 0, 0);
			this.firstAttach = false;
			super.setPos(this.thisPos.x, this.thisPos.y, this.thisPos.z);
		}
		
		if (this.attached) {
			this.setDeltaMovement(0, 0, 0);
		}
		
		super.tick();
		
		if (!this.level.isClientSide) {
			if (this.shootingEntity != null)  {
				if (!this.attached) {
					if (this.segmentHandler.hookPastBend(this.r)) {
						System.out.println("around bend");
						Vec farthest = this.segmentHandler.getFarthest();
						this.serverAttach(this.segmentHandler.getBendBlock(1), farthest, null);
					}
					
					if (!this.customization.phaserope) {
						this.segmentHandler.update(Vec.positionVec(this), Vec.positionVec(this.shootingEntity).add(new Vec(0, this.shootingEntity.getEyeHeight(), 0)), this.r, true);
						
						if (this.customization.sticky) {
							if (this.segmentHandler.segments.size() > 2) {
								int bendnumber = this.segmentHandler.segments.size() - 2;
								Vec closest = this.segmentHandler.segments.get(bendnumber);
								this.serverAttach(this.segmentHandler.getBendBlock(bendnumber), closest, null);
							}
						}
					} else {
						this.segmentHandler.updatePos(Vec.positionVec(this), Vec.positionVec(this.shootingEntity).add(new Vec(0, this.shootingEntity.getEyeHeight(), 0)), this.r);
					}
					
					Vec farthest = this.segmentHandler.getFarthest();
					double distToFarthest = this.segmentHandler.getDistToFarthest();
					
					Vec ropevec = Vec.positionVec(this).sub(farthest);
					double d = ropevec.length();
					
					if (this.customization.reelin && this.shootingEntity.isCrouching()) {
						double newdist = d + distToFarthest - 0.4;
						if (newdist > 1 && newdist <= this.customization.maxlen) {
							this.r = newdist;
						}
					}


					if (d + distToFarthest > this.r) {
						Vec motion = Vec.motionVec(this);
						
						if (motion.dot(ropevec) > 0) {
							motion = motion.removeAlong(ropevec);
						}
						
						this.setVelocityActually(motion.x, motion.y, motion.z);
						
						ropevec.changeLen_ip(this.r - distToFarthest);
						Vec newpos = ropevec.add(farthest);
						
						this.setPos(newpos.x, newpos.y, newpos.z);
					}
					
				}
			}
		}
		
		// magnet attraction
		if (!this.attached && this.customization.attract && Vec.positionVec(this).sub(Vec.positionVec(this.shootingEntity)).length() > this.customization.attractradius) {
	    	if (this.shootingEntity == null) {return;}
	    	if (!this.foundBlock) {
	    		if (!this.level.isClientSide) {
	    			Vec playerpos = Vec.positionVec(this.shootingEntity);
	    			Vec pos = Vec.positionVec(this);
	    			if (magnetBlock == null) {
		    			if (prevPos != null) {
			    			HashMap<BlockPos, Boolean> checkedset = new HashMap<BlockPos, Boolean>();
			    			Vec vector = pos.sub(prevPos);
			    			if (vector.length() > 0) {
				    			Vec normvector = vector.normalize();
				    			for (int i = 0; i < vector.length(); i++) {
				    				double dist = prevPos.sub(playerpos).length();
				    				int radius = (int) dist / 4;
				    				BlockPos found = this.check(prevPos, checkedset);
				    				if (found != null) {
//				    					if (wasinair) {
								    		Vec distvec = new Vec(found.getX(), found.getY(), found.getZ());
								    		distvec.sub_ip(prevPos);
								    		if (distvec.length() < radius) {
						    					this.setPosAndOldPos(prevPos.x, prevPos.y, prevPos.z);
						    					pos = prevPos;
						    					
						    					magnetBlock = found;
						    					
						    					break;
								    		}
//				    					}
				    				} else {
				    					wasInAir = true;
				    				}
				    				
				    				prevPos.add_ip(normvector);
				    			}
			    			}
		    			}
	    			}
	    			
	    			if (magnetBlock != null) {
				    	BlockState blockstate = this.level.getBlockState(magnetBlock);
				    	VoxelShape BB = blockstate.getCollisionShape(this.level, magnetBlock);

						Vec blockvec = new Vec(magnetBlock.getX() + (BB.max(Axis.X) + BB.min(Axis.X)) / 2, magnetBlock.getY() + (BB.max(Axis.Y) + BB.min(Axis.Y)) / 2, magnetBlock.getZ() + (BB.max(Axis.Z) + BB.min(Axis.Z)) / 2);
						Vec newvel = blockvec.sub(pos);
						
						double l = newvel.length();
						
						newvel.changeLen(this.getVelocity());
						
						this.setDeltaMovement(newvel.x, newvel.y, newvel.z);
						
						if (l < 0.2) {
							this.serverAttach(magnetBlock, blockvec, Direction.UP);
						}
	    			}
	    			
	    			prevPos = pos;
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
		return this.segmentHandler.getBoundingBox(Vec.positionVec(this), Vec.positionVec(this.shootingEntity).add(new Vec(0, this.shootingEntity.getEyeHeight(), 0)));
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
				
				Vec vec3d = Vec.positionVec(this);
		        Vec vec3d1 = vec3d.add(Vec.motionVec(this));

				if (movingobjectposition instanceof EntityRayTraceResult && !GrappleConfig.getConf().grapplinghook.other.hookaffectsentities) {
					onHit(GrapplemodUtils.rayTraceBlocks(this.level, vec3d, vec3d1));
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
						if (GrappleConfigUtils.breaksBlock(block)) {
							this.level.destroyBlock(blockpos, true);
					        onHit(GrapplemodUtils.rayTraceBlocks(this.level, vec3d, vec3d1));
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
					
					Vec playerpos = Vec.positionVec(this.shootingEntity);
					Vec entitypos = Vec.positionVec(entity);
					Vec yank = playerpos.sub(entitypos).mult(0.4);
					yank.y = Math.min(yank.y, 2);
					Vec newmotion = Vec.motionVec(entity).add(yank);
					entity.setDeltaMovement(newmotion.toVec3d());
					
					this.removeServer();
					return;
				} else if (blockhit != null) {
					BlockPos blockpos = blockhit.getBlockPos();
					
					Vec vec3 = new Vec(movingobjectposition.getLocation());

					this.serverAttach(blockpos, vec3, blockhit.getDirection());
				} else {
					System.out.println("unknown impact?");
				}
			}
		}
	}

	@Override
	protected Item getDefaultItem() {
		return CommonSetup.grapplingHookItem;
	}
	
	public void serverAttach(BlockPos blockpos, Vec pos, Direction sideHit) {
		if (this.attached) {
			return;
		}
		this.attached = true;
		
		if (blockpos != null) {
			Block block = this.level.getBlockState(blockpos).getBlock();

			if (!GrappleConfigUtils.attachesBlock(block)) {
				this.removeServer();
				return;
			}
		}
		
		Vec vec3 = Vec.positionVec(this);
		vec3.add_ip(Vec.motionVec(this));
		if (pos != null) {
            vec3 = pos;
            
            this.setPosAndOldPos(vec3.x, vec3.y, vec3.z);
		}
		
		//west -x
		//north -z
		Vec curpos = Vec.positionVec(this);
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
		curpos.setPos(this);
		
		this.setDeltaMovement(0, 0, 0);
        
        this.thisPos = Vec.positionVec(this);
		this.firstAttach = true;
		ServerControllerManager.attached.add(this.shootingEntityID);
		
		GrapplemodUtils.sendToCorrectClient(new GrappleAttachMessage(this.getId(), this.position().x, this.position().y, this.position().z, this.getControlId(), this.shootingEntityID, blockpos, this.segmentHandler.segments, this.segmentHandler.segmentTopSides, this.segmentHandler.segmentBottomSides, this.customization), this.shootingEntityID, this.level);
		
		GrappleAttachPosMessage msg = new GrappleAttachPosMessage(this.getId(), this.position().x, this.position().y, this.position().z);
		CommonSetup.network.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(new BlockPos(this.position().x, this.position().y, this.position().z))), msg);
	}
	
	public void clientAttach(double x, double y, double z) {
		this.setAttachPos(x, y, z);
		
		if (this.shootingEntity instanceof PlayerEntity) {
			ClientProxyInterface.proxy.resetLauncherTime(this.shootingEntityID);
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
		return GrapplemodUtils.GRAPPLEID;
	}

	public void setAttachPos(double x, double y, double z) {
		this.setPosAndOldPos(x, y, z);

		this.setDeltaMovement(0, 0, 0);
		this.firstAttach = true;
		this.attached = true;
        this.thisPos = new Vec(x, y, z);
	}
	
	// used for magnet attraction
    public BlockPos check(Vec p, HashMap<BlockPos, Boolean> checkedset) {
    	int radius = (int) Math.floor(this.customization.attractradius);
    	BlockPos closestpos = null;
    	double closestdist = 0;
    	for (int x = (int)p.x - radius; x <= (int)p.x + radius; x++) {
        	for (int y = (int)p.y - radius; y <= (int)p.y + radius; y++) {
            	for (int z = (int)p.z - radius; z <= (int)p.z + radius; z++) {
			    	BlockPos pos = new BlockPos(x, y, z);
			    	if (pos != null) {
				    	if (hasBlock(pos, checkedset)) {
				    		Vec distvec = new Vec(pos.getX(), pos.getY(), pos.getZ());
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
	public boolean hasBlock(BlockPos pos, HashMap<BlockPos, Boolean> checkedset) {
    	if (!checkedset.containsKey(pos)) {
    		boolean isblock = false;
	    	BlockState blockstate = this.level.getBlockState(pos);
	    	Block b = blockstate.getBlock();
			if (!GrappleConfigUtils.attachesBlock(b)) {
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
