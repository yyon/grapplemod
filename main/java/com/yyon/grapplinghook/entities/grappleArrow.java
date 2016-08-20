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

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
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
	
	public Entity shootingEntity = null;
	public int shootingEntityID;
	private boolean firstattach = false;
	public vec thispos;
	
	public grappleArrow(World worldIn) {
		super(worldIn);
		System.out.println("init (1) " + this.toString());
	}
	
	public grappleArrow(World worldIn, EntityLivingBase shooter,
			float p_i1756_3_) {
		super(worldIn, shooter);
		
		this.shootingEntity = shooter;
		this.shootingEntityID = this.shootingEntity.getEntityId();
		System.out.println("init (2) " + this.toString());
		
		System.out.println(this.shootingEntityID);
		System.out.println(this.shootingEntity.worldObj);
		System.out.println(this.shootingEntity.getUniqueID());

		grapplemod.updateMaxLen();
		grapplemod.updateGrapplingBlocks();
	}

	public void onEntityUpdate(){
		super.onEntityUpdate();
		
		if (this.shootingEntityID == 0) {
			this.kill();
		}
		
		if (this.firstattach) {
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;
			this.firstattach = false;
			super.setPosition(this.thispos.x, this.thispos.y, this.thispos.z);
			System.out.println("Re-updated pos");
		}
		
		
		if (this.toofaraway()) {
			this.removeServer();
		}
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
	public void setPosition(double x, double y, double z) {
		if (this.thispos != null) {
			x = this.thispos.x;
			y = this.thispos.y;
			z = this.thispos.z;
		}
		super.setPosition(x, y, z);
	}
	
	
	
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
					blockpos = movingobjectposition.getBlockPos();

					if (!grapplemod.anyblocks) {
						Block block = this.worldObj.getBlockState(blockpos).getBlock();

						if (!grapplemod.grapplingblocks.contains(block)) {
							System.out.println("Hit invalid block");
							this.removeServer();
							return;
						}
					}
				}
				
				System.out.println("attaching! (server) " + this.toString());
				
				vec vec3 = vec.positionvec(this);
				vec3.add_ip(vec.motionvec(this));
		        
		        if (movingobjectposition != null)
		        {
		            vec3 = new vec(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
		            
		            this.setPositionAndUpdate(vec3.x, vec3.y, vec3.z);
		        }
		        
				if (this.toofaraway()) {
					return;
				}
		        
		        this.motionX = 0;
		        this.motionY = 0;
		        this.motionZ = 0;
		        
		        this.thispos = vec.positionvec(this);
				this.firstattach = true;
		        
				grapplemod.attached.add(this.shootingEntityID);
				System.out.println(grapplemod.attached);
				
				grapplemod.sendtocorrectclient(new GrappleAttachMessage(this.getEntityId(), this.posX, this.posY, this.posZ, this.getControlId(), this.shootingEntityID, grapplemod.grapplingLength, blockpos), this.shootingEntityID, this.worldObj);
				if (this.shootingEntity instanceof EntityPlayerMP) {
					EntityPlayerMP sender = (EntityPlayerMP) this.shootingEntity;
					int dimension = sender.dimension;
					MinecraftServer minecraftServer = sender.mcServer;
					for (EntityPlayerMP player : (List<EntityPlayerMP>)minecraftServer.getConfigurationManager().playerEntityList) {
						GrappleAttachPosMessage msg = new GrappleAttachPosMessage(this.getEntityId(), this.posX, this.posY, this.posZ);   // must generate a fresh message for every player!
						if (dimension == player.dimension) {
							grapplemod.sendtocorrectclient(msg, player.getEntityId(), player.worldObj);
						}
					}
				}
			}
		}
	}
	
	public void clientAttach(double x, double y, double z) {
		System.out.println("attaching! (client) " + this.toString());
		
		this.setAttachPos(x, y, z);
		
		if (this.shootingEntity instanceof EntityPlayer) {
			grapplemod.proxy.resetlaunchertime(this.shootingEntityID);
		}
	}
	
	@Override
    protected float getGravityVelocity()
    {
        return 0F;
    }
	
    protected float getVelocity()
    {
        return 5F;
    }

	public void removeServer() {
		this.kill();
		this.shootingEntityID = 0;
	}
	
	public int getControlId() {
		return grapplemod.GRAPPLEID;
	}

	public void setAttachPos(double x, double y, double z) {
		this.setPositionAndUpdate(x, y, z);

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.firstattach = true;
        this.thispos = new vec(x, y, z);
	}
}
