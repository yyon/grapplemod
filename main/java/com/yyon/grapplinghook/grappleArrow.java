package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

//TODO
// stop when collided
// inside sphere
// starting velocity incorrect
// fighting
//make sure SMP works

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
	
	public grappleController control;
	private boolean firstattach = false;
	public Vec3 thispos;
	
	
	public grappleArrow(World worldIn) {
		super(worldIn);
		System.out.println("init (1) " + this.toString());
	}
	
	public grappleArrow(World worldIn, EntityLivingBase shooter,
			float p_i1756_3_) {
		super(worldIn, shooter);
		
		this.shootingEntity = shooter;
//		if (this.shootingEntity instanceof EntityPlayer) {
//			this.shootingplayer = (EntityPlayer) this.shootingEntity;
//		}
		this.shootingEntityID = this.shootingEntity.getEntityId();
		System.out.println("init (2) " + this.toString());
	}
	
	public grappleArrow(World world, EntityLivingBase shooter) {
		this(world, shooter, 0);
		System.out.println("ERROR! init (3)");
	}
    public grappleArrow(World worldIn, double x, double y, double z) {
    	this(worldIn);
		System.out.println("ERROR! init (4)");
    }
	
	public void onEntityUpdate(){
		super.onEntityUpdate();
		
//		if(this.shootingEntity == null || !(this.shootingEntity instanceof Entity)) {
//			this.grappleend();
//		}
		
		if (this.firstattach) {
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;
			this.firstattach = false;
			super.setPosition(this.thispos.xCoord, this.thispos.yCoord, this.thispos.zCoord);
			System.out.println("Re-updated pos");
		}
		
		
//		if (this.doposupdate) {
//			this.doposupdate = false;
//            this.setPositionAndUpdate(this.thispos.xCoord, this.thispos.yCoord, this.thispos.zCoord);
//		}
	}
	
	public void setPosition(double x, double y, double z) {
		if (this.thispos != null) {
			x = this.thispos.xCoord;
			y = this.thispos.yCoord;
			z = this.thispos.zCoord;
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
		grapplemod.network.sendToServer(new GrappleEndMessage(this.getEntityId()));
	}
	
	@Override
	public String toString() {
		return super.toString() + String.valueOf(System.identityHashCode(this)) + "]";
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
		if (!this.worldObj.isRemote) {
			if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
				// hit entity
				Entity entityhit = movingobjectposition.entityHit;
				Vec3 playerpos = this.shootingEntity.getPositionVector();
				Vec3 entitypos = entityhit.getPositionVector();
				Vec3 yank = multvec(playerpos.subtract(entitypos), 0.4);
				entityhit.addVelocity(yank.xCoord, Math.min(yank.yCoord, 2), yank.zCoord);
				
				this.removeServer();
				return;
			}
			
//			this.attached = true;
			System.out.println("attaching! (server) " + this.toString());
			
//	        Vec3 vec31 = new Vec3(this.posX, this.posY, this.posZ);
	        Vec3 vec3 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
	        
	        if (movingobjectposition != null)
	        {
	            vec3 = new Vec3(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
	            
//	            doposupdate = true;
	            
	            this.setPositionAndUpdate(vec3.xCoord, vec3.yCoord, vec3.zCoord);
	        }
	        
	        this.motionX = 0;
	        this.motionY = 0;
	        this.motionZ = 0;
	        
	        this.thispos = new Vec3(this.posX, this.posY, this.posZ);
			this.firstattach = true;
	        
//			r = this.getDistanceToEntity(this.shootingEntity);
//			motion = new Vec3(this.shootingEntity.motionX, this.shootingEntity.motionY, this.shootingEntity.motionZ);
			
			grapplemod.attached.add(this.shootingEntityID);
			grapplemod.network.sendToAll(new GrappleAttachMessage(this.getEntityId(), this.posX, this.posY, this.posZ));
		}
	}
	
	public void clientAttach(double x, double y, double z) {
		System.out.println("attaching! (client) " + this.toString());
		
//		this.attached = true;
		
		this.setPositionAndUpdate(x, y, z);
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.firstattach = true;
        this.thispos = new Vec3(x, y, z);
//		this.thispos = ;
//		this.doposupdate = true;
//		this.r = r;
//		this.motion = new Vec3(mx, my, mz);
		
		this.createControl();
		
//		System.out.println(motion);
	}
	
	public void createControl() {
		System.out.println("Creating grapple controller");
		this.control = new grappleController(this.getEntityId(), this.shootingEntity.getEntityId(), this.worldObj, new Vec3(this.posX, this.posY, this.posZ));
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
	}
	
	public Vec3 multvec(Vec3 a, double changefactor) {
		return new Vec3(a.xCoord * changefactor, a.yCoord * changefactor, a.zCoord * changefactor);
	}
}
