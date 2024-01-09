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
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.yyon.grapplinghook.BlockPos;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.network.GrappleAttachMessage;
import com.yyon.grapplinghook.network.GrappleAttachPosMessage;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	
	public boolean righthand = true;
	
	public boolean attached = false;
	
	public double taut = 1;
	
	public boolean ignoreFrustumCheck = true;
	
	public grappleArrow(World worldIn) {
		super(worldIn);
	}
	
	public grappleArrow(World worldIn, EntityLivingBase shooter,
			boolean righthand) {
		super(worldIn, shooter);
		
		this.shootingEntity = shooter;
		this.shootingEntityID = this.shootingEntity.getEntityId();
		
		/*
		double x = 0.36;
		if (righthand) {x = -0.36;}
        vec pos = vec.positionvec(this);
        pos.add_ip(new vec(x, -0.175, 0.45).rotate_yaw(Math.toRadians(shooter.rotationYaw)));
        this.setPosition(pos.x, pos.y, pos.z);
        */

		grapplemod.updateMaxLen(worldIn);
		grapplemod.updateGrapplingBlocks(worldIn);
		
		this.righthand = righthand;
	}
	
	@Override
	public void onEntityUpdate(){
		super.onEntityUpdate();
		
		if (this.shootingEntityID == 0) { // removes ghost grappling hooks
			this.kill();
		}
		
		if (this.firstattach) {
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;
			this.firstattach = false;
			super.setPosition(this.thispos.x, this.thispos.y, this.thispos.z);
		}
		
		
		if (this.toofaraway()) {
			this.removeServer();
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	public final int RenderBoundingBoxSize = 999;
	/*
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(-RenderBoundingBoxSize, -RenderBoundingBoxSize, -RenderBoundingBoxSize, 
				RenderBoundingBoxSize, RenderBoundingBoxSize, RenderBoundingBoxSize);
	}
	*/

	public boolean toofaraway() {
    	if (this.shootingEntity == null) {return false;}
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
	    data.writeBoolean(this.righthand);
    }
	
	@Override
    public void readSpawnData(ByteBuf data)
    {
    	this.shootingEntityID = data.readInt();
	    this.shootingEntity = this.worldObj.getEntityByID(this.shootingEntityID);
	    this.righthand = data.readBoolean();
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
					if (entityhit == this.shootingEntity) {
						return;
					}
					
					vec playerpos = vec.positionvec(this.shootingEntity);
					vec entitypos = vec.positionvec(entityhit);
					vec yank = playerpos.sub(entitypos).mult(0.4);
					entityhit.addVelocity(yank.x, Math.min(yank.y, 2), yank.z);
					
					this.removeServer();
					return;
				}
				
				BlockPos blockpos = null;
				
				if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
					blockpos = new BlockPos(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);
				}
				vec vec3 = null;
		        
		        if (movingobjectposition != null)
		        {
		            vec3 = new vec(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
		        }
		        this.serverAttach(blockpos, vec3, movingobjectposition.sideHit);
			}
		}
	}
	
	public void serverAttach(BlockPos blockpos, vec pos, int sideHit) {
		if (this.attached) {
			return;
		}
		this.attached = true;
		
		if (blockpos != null) {
			if (!grapplemod.anyblocks) {
				Block block = this.worldObj.getBlock(blockpos.x, blockpos.y, blockpos.z);

				if ((!grapplemod.removeblocks && !grapplemod.grapplingblocks.contains(block))
						|| (grapplemod.removeblocks && grapplemod.grapplingblocks.contains(block))) {
					this.removeServer();
					return;
				}
			}
		}
		
		vec vec3 = vec.positionvec(this);
		vec3.add_ip(vec.motionvec(this));
		if (pos != null) {
            vec3 = pos;
            
            this.setPosition(vec3.x, vec3.y, vec3.z);
		}
		
		//west -x
		//north -z
		System.out.println(sideHit);
		if (sideHit == 0) { // bottom
			this.posY -= 0.3;
//		} else if (sideHit == 1) { // top
//			this.posY += 0.05;
//		} else if (sideHit == 2) { // east
//			this.posX += 0.05;
		} else if (sideHit == 4) { // west
			this.posX -= 0.05;
		} else if (sideHit == 2) { // north
			this.posZ -= 0.05;
//		} else if (sideHit == 5) { // south
//			this.posZ += 0.05;
		}


		if (this.toofaraway()) {
			System.out.println("TOOFAR");
			return;
		}
		
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        
        this.thispos = vec.positionvec(this);
		this.firstattach = true;
		grapplemod.attached.add(this.shootingEntityID);
		
		grapplemod.sendtocorrectclient(new GrappleAttachMessage(this.getEntityId(), this.posX, this.posY, this.posZ, this.getControlId(), this.shootingEntityID, grapplemod.grapplingLength, blockpos), this.shootingEntityID, this.worldObj);
		if (this.shootingEntity instanceof EntityPlayerMP) { // fixes strange bug in LAN
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
	
	public void clientAttach(double x, double y, double z) {
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
	
	@Override
    protected float func_70182_d()
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
		this.setPosition(x, y, z);

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.firstattach = true;
		this.attached = true;
        this.thispos = new vec(x, y, z);
	}
    public void setHeadingFromThrower(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
    {
        float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
        float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        this.setThrowableHeading((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        this.motionX += entityThrower.motionX;
        this.motionZ += entityThrower.motionZ;

        if (!entityThrower.onGround)
        {
            this.motionY += entityThrower.motionY;
        }
    }
}
