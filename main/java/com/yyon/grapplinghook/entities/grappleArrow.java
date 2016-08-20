package com.yyon.grapplinghook.entities;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.network.GrappleAttachMessage;
import com.yyon.grapplinghook.network.GrappleAttachPosMessage;

/* // 1.8 Compatability
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
/*/ // 1.7.10 Compatability
import com.yyon.grapplinghook.BlockPos;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
//*/

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

public class grappleArrow extends EntityThrowable implements IEntityAdditionalSpawnData
{
//	public boolean attached;
//	public boolean firstattach = true;
//	public double r = -1;
	
//	public boolean doposupdate = false;
//	public Vec3 thispos = null;
	
//	public Vec3 motion = null;
	
//	public double playerforward = 0;
//	public double playerstrafe = 0;
//	public boolean playerjump = false;
//	public Vec3 playermovement = new Vec3(0,0,0);
	
	public Entity shootingEntity = null;
	public int shootingEntityID;
	
//	public EntityPlayer shootingplayer = null;
	
//	public int counter = 0;
	
//	public grappleController control;
	private boolean firstattach = false;
	public vec thispos;
	
	public grappleArrow(World worldIn) {
		super(worldIn);
//		System.out.println("init (1) " + this.toString());
	}
	
	public grappleArrow(World worldIn, EntityLivingBase shooter,
			float p_i1756_3_) {
		super(worldIn, shooter);
		
		this.shootingEntity = shooter;
//		if (this.shootingEntity instanceof EntityPlayer) {
//			this.shootingplayer = (EntityPlayer) this.shootingEntity;
//		}
		this.shootingEntityID = this.shootingEntity.getEntityId();
//		System.out.println("init (2) " + this.toString());
		
//		System.out.println(this.shootingEntityID);
//		System.out.println(this.shootingEntity.worldObj);
//		System.out.println(this.shootingEntity.getUniqueID());

		grapplemod.updateMaxLen();
		grapplemod.updateGrapplingBlocks();
	}
	
	/*
	public grappleArrow(World world, EntityLivingBase shooter) {
		this(world, shooter, 0);
		System.out.println("ERROR! init (3)");
	}
    public grappleArrow(World worldIn, double x, double y, double z) {
    	this(worldIn);
		System.out.println("ERROR! init (4)");
    }
    */
	
//* // 1.7.10 Compatability
	@Override
//*/

	public void onEntityUpdate(){
		super.onEntityUpdate();
		
		if (this.shootingEntityID == 0) { // removes ghost grappling hooks
			this.kill();
		}
//		if(this.shootingEntity == null || !(this.shootingEntity instanceof Entity)) {
//			this.grappleend();
//		}
		
		if (this.firstattach) {
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;
			this.firstattach = false;
			super.setPosition(this.thispos.x, this.thispos.y, this.thispos.z);
//			System.out.println("Re-updated pos");
		}
		
		
		if (this.toofaraway()) {
			this.removeServer();
		}
//		if (this.doposupdate) {
//			this.doposupdate = false;
//            this.setPositionAndUpdate(this.thispos.xCoord, this.thispos.yCoord, this.thispos.zCoord);
//		}
	}
	
	public boolean toofaraway() {
		if (!this.worldObj.isRemote) {
			if (!grapplemod.attached.contains(this.shootingEntityID)) {
				if (grapplemod.grapplingLength != 0) {
					double d = vec.positionvec(this).sub(vec.positionvec(this.shootingEntity)).length();
					if (d > grapplemod.grapplingLength) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
//* // 1.7.10 Compatability
	@Override
//*/

	public void setPosition(double x, double y, double z) {
		if (this.thispos != null) {
			x = this.thispos.x;
			y = this.thispos.y;
			z = this.thispos.z;
		}
		super.setPosition(x, y, z);
	}
	
	/*
	public void receivePlayerMovementMessage(double strafe, double forward, boolean jump) {
		if (this.shootingEntity != null) {
			playerforward = forward;
			playerstrafe = strafe;
			playerjump = jump;
			playermovement = new Vec3(strafe, 0, forward);
			playermovement = playermovement.rotateYaw((float) (this.shootingEntity.rotationYaw * (-Math.PI / 180.0)));
		}
	}
	*/
	
	
//	public void grappleend() {
//		if (!this.worldObj.isRemote) {
//			this.unattach();
//		}
//	}
	
	
	@Override
    public void writeSpawnData(ByteBuf data)
    {
	    data.writeInt(this.shootingEntity != null ? this.shootingEntity.getEntityId() : 0);
    }
	
	@Override
    public void readSpawnData(ByteBuf data)
    {
    	this.shootingEntityID = data.readInt();
	    this.shootingEntity = this.worldObj.getEntityByID(this.shootingEntityID);
	    
//	    this.shootingplayer = (EntityPlayer) this.shootingEntity;
    }
	
	public void remove() {
		this.kill();
	}
	
	@Override
	public String toString() {
		return super.toString() + String.valueOf(System.identityHashCode(this)) + "]";
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
		if (!this.worldObj.isRemote) {
			if (this.shootingEntityID != 0) {
				if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
					// hit entity
					Entity entityhit = movingobjectposition.entityHit;
					vec playerpos = vec.positionvec(this.shootingEntity);
					vec entitypos = vec.positionvec(entityhit);
					vec yank = playerpos.sub(entitypos).mult(0.4);
					entityhit.addVelocity(yank.x, Math.min(yank.y, 2), yank.z);
					
					this.removeServer();
					return;
				}
				
				BlockPos blockpos = new BlockPos(0,0,0);
				
				if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
/* // 1.8 Compatability
					blockpos = movingobjectposition.getBlockPos();
/*/ // 1.7.10 Compatability
					blockpos = new BlockPos(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);//movingobjectposition.getBlockPos();
//*/

					if (!grapplemod.anyblocks) {
/* // 1.8 Compatability
						Block block = this.worldObj.getBlockState(blockpos).getBlock();
/*/ // 1.7.10 Compatability
						Block block = this.worldObj.getBlock(blockpos.x, blockpos.y, blockpos.z);
//*/

						if ((!grapplemod.removeblocks && !grapplemod.grapplingblocks.contains(block))
								|| (grapplemod.removeblocks && grapplemod.grapplingblocks.contains(block))) {
//							System.out.println("Hit invalid block");
							this.removeServer();
							return;
						}
					}
				}
				
	//			this.attached = true;
//				System.out.println("attaching! (server) " + this.toString());
				
	//	        Vec3 vec31 = new Vec3(this.posX, this.posY, this.posZ);
//		        vec vec3 = new vec(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
				vec vec3 = vec.positionvec(this);
				vec3.add_ip(vec.motionvec(this));
		        
		        if (movingobjectposition != null)
		        {
		            vec3 = new vec(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
		            
	//	            doposupdate = true;
		            
/* // 1.8 Compatability
		            this.setPositionAndUpdate(vec3.x, vec3.y, vec3.z);
/*/ // 1.7.10 Compatability
		            this.setPosition(vec3.x, vec3.y, vec3.z);
//*/

		        }
		        
				if (this.toofaraway()) {
					return;
				}
		        
		        this.motionX = 0;
		        this.motionY = 0;
		        this.motionZ = 0;
		        
		        this.thispos = vec.positionvec(this);
				this.firstattach = true;
		        
	//			r = this.getDistanceToEntity(this.shootingEntity);
	//			motion = new Vec3(this.shootingEntity.motionX, this.shootingEntity.motionY, this.shootingEntity.motionZ);
				
				grapplemod.attached.add(this.shootingEntityID);
//				System.out.println(grapplemod.attached);
				
				grapplemod.sendtocorrectclient(new GrappleAttachMessage(this.getEntityId(), this.posX, this.posY, this.posZ, this.getControlId(), this.shootingEntityID, grapplemod.grapplingLength, blockpos), this.shootingEntityID, this.worldObj);
	//			grapplemod.network.sendToAll(new GrappleAttachPosMessage(this.getEntityId(), this.posX, this.posY, this.posZ));
				if (this.shootingEntity instanceof EntityPlayerMP) { // fixes strange bug in LAN
					EntityPlayerMP sender = (EntityPlayerMP) this.shootingEntity;
					int dimension = sender.dimension;
					MinecraftServer minecraftServer = sender.mcServer;
					for (EntityPlayerMP player : (List<EntityPlayerMP>)minecraftServer.getConfigurationManager().playerEntityList) {
						GrappleAttachPosMessage msg = new GrappleAttachPosMessage(this.getEntityId(), this.posX, this.posY, this.posZ);   // must generate a fresh message for every player!
						if (dimension == player.dimension) {
	//						StartupCommon.simpleNetworkWrapper.sendTo(msg, player);
							grapplemod.sendtocorrectclient(msg, player.getEntityId(), player.worldObj);
						}
					}
				}
			}
		}
	}
	
	public void clientAttach(double x, double y, double z) {
//		System.out.println("attaching! (client) " + this.toString());
		
		this.setAttachPos(x, y, z);
		
		if (this.shootingEntity instanceof EntityPlayer) {
			grapplemod.proxy.resetlaunchertime(this.shootingEntityID);
		}
//		this.attached = true;
		
//		this.thispos = ;
//		this.doposupdate = true;
//		this.r = r;
//		this.motion = new Vec3(mx, my, mz);
		
//		this.createControl();
		
//		System.out.println(motion);
	}
	
//	public void createControl() {
//		System.out.println("Creating grapple controller");
//		this.control = new grappleController(this.getEntityId(), this.shootingEntity.getEntityId(), this.worldObj, new Vec3(this.posX, this.posY, this.posZ));
//	}
	
	@Override
    protected float getGravityVelocity()
    {
        return 0F;
    }
	
/* // 1.8 Compatability
    protected float getVelocity()
/*/ // 1.7.10 Compatability
	@Override
    protected float func_70182_d()
//*/

    {
        return 5F;
    }

	public void removeServer() {
		this.kill();
		this.shootingEntityID = 0;
//* // 1.7.10 Compatability
		System.out.println("REMOVE SERVER!");
//*/

	}
	
	public int getControlId() {
		return grapplemod.GRAPPLEID;
	}

	public void setAttachPos(double x, double y, double z) {
/* // 1.8 Compatability
		this.setPositionAndUpdate(x, y, z);
/*/ // 1.7.10 Compatability
		this.setPosition(x, y, z);
//*/

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.firstattach = true;
        this.thispos = new vec(x, y, z);
	}
}
