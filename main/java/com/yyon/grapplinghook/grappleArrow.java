package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

//TODO
// stop when collided
// inside sphere
// starting velocity incorrect
// fighting
//make sure SMP works

public class grappleArrow extends EntityThrowable implements IEntityAdditionalSpawnData
{
	public boolean attached;
	public boolean firstattach = true;
	public double r = -1;
	
	public boolean doposupdate = false;
	public Vec3 thispos = null;
	
	public Vec3 motion = null;
	
	public double playerforward = 0;
	public double playerstrafe = 0;
	public boolean playerjump = false;
	public Vec3 playermovement = new Vec3(0,0,0);
	
	public Entity shootingEntity = null;
	public int shootingEntityID;
	
	public EntityPlayer shootingplayer = null;
	
	public int counter = 0;
	
	
	public grappleArrow(World worldIn) {
		super(worldIn);
		FMLCommonHandler.instance().bus().register(this);
		System.out.println("init (1) " + this.toString());
	}
	
	public grappleArrow(World worldIn, EntityLivingBase shooter,
			float p_i1756_3_) {
		super(worldIn, shooter);
		FMLCommonHandler.instance().bus().register(this);
		this.shootingEntity = shooter;
		if (this.shootingEntity instanceof EntityPlayer) {
			this.shootingplayer = (EntityPlayer) this.shootingEntity;
		}
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
		
		if(this.shootingEntity == null || !(this.shootingEntity instanceof Entity)) {
			this.grappleend();
		}
		
		if (this.attached && this.firstattach) {
//			this.ocity(0, 0, 0);
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;
			this.firstattach = false;
		}
		
		
		if (this.doposupdate) {
			this.doposupdate = false;
            this.setPositionAndUpdate(this.thispos.xCoord, this.thispos.yCoord, this.thispos.zCoord);
		}
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (this.attached) {
			grapplemod.proxy.sendplayermovementmessage(this, this.shootingEntityID, this.getEntityId());
		}
//		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
//		if (player.getEntityId() == this.shootingEntityID) {
//			this.updatePlayerPos(player);
//			grapplemod.network.sendToServer(new PlayerMovementMessage(this.getEntityId(), player.moveStrafing, player.moveForward, ((EntityPlayerSP) player).movementInput.jump));
//		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.player.getEntityId() == this.shootingEntityID) {
//			if (!this.worldObj.isRemote && this.worldObj == event.player.worldObj) {
				this.updatePlayerPos(event.player);
//			}
		}
	}
	
	public void receivePlayerMovementMessage(double strafe, double forward, boolean jump) {
		if (this.shootingEntity != null) {
			playerforward = forward;
			playerstrafe = strafe;
			playerjump = jump;
			playermovement = new Vec3(strafe, 0, forward);
			playermovement = playermovement.rotateYaw((float) (this.shootingEntity.rotationYaw * (-Math.PI / 180.0)));
		}
	}
	
	public Vec3 proj (Vec3 a, Vec3 b) {
		b = b.normalize();
		double dot = a.dotProduct(b);
		return changelen(b, dot);
	}
	
	public Vec3 removealong(Vec3 a, Vec3 b) {
		return a.subtract(proj(a, b));
	}
	
	public Vec3 multvec(Vec3 a, double changefactor) {
		return new Vec3(a.xCoord * changefactor, a.yCoord * changefactor, a.zCoord * changefactor);
	}
	
	public Vec3 changelen(Vec3 a, double l) {
		double oldl = a.lengthVector();
		if (oldl != 0) {
			double changefactor = l / oldl;
			return multvec(a, changefactor);
		} else {
			return a;
		}
	}
	
	public void printvec(Vec3 a) {
		System.out.printf("%f %f %f\n", a.xCoord, a.yCoord, a.zCoord);
	}
	
	public void updatePlayerPos(EntityPlayer theplayer) {
		Entity entity = this.shootingEntity;
		
		if (this.attached) {
			if(entity != null) {
				if (true) {
					counter++;
					if (counter > 1000) {
						counter = 0;
						System.out.println("pulling " + this.toString());
					}
					
					Vec3 arrowpos = this.thispos;//this.getPositionVector();
					Vec3 playerpos = entity.getPositionVector();
					Vec3 playermotion = new Vec3(entity.motionX, entity.motionY, entity.motionZ);
					
					Vec3 oldspherevec = playerpos.subtract(arrowpos);
					Vec3 spherevec = changelen(oldspherevec, r);
					Vec3 spherechange = spherevec.subtract(oldspherevec);
					Vec3 spherepos = spherevec.add(arrowpos);
					
					Vec3 additionalmotion = new Vec3(0,0,0);
					
					double dist = oldspherevec.lengthVector();
					
					if (entity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) entity;
						if (playerjump) {
							Vec3 jump = new Vec3(0, 0.5, 0);
							jump = proj(jump, spherevec);
							double jumppower = jump.yCoord;
							if (jumppower < 0) {
								jumppower = 0;
							}
							if (r < 2 || player.onGround || player.isCollided) {
								jumppower = 0.5;
							}
							
							this.grappleend();
							
//							player.ocity(player.motionX, player.motionY + jumppower, player.motionZ);
							player.motionY = player.motionY + jumppower;
							if (entity instanceof EntityPlayerMP) {
								((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
							}
							
							return;
						} else if (entity.isSneaking()) {
							motion = multvec(motion, 0.9);
							if (this.playerforward != 0) {
								additionalmotion = new Vec3(0, this.playerforward, 0);
								this.r = dist;
							}
						} else {
							motion = motion.add(changelen(this.playermovement, 0.01));
						}
					}
						
					if (!entity.onGround) {
						motion = motion.addVector(0, -0.05, 0);
					}
					
					motion = removealong(motion, spherevec);
					
					Vec3 newmotion = motion.add(additionalmotion);
					
//					entity.setVelocity(newmotion.xCoord, newmotion.yCoord, newmotion.zCoord);
					entity.motionX = newmotion.xCoord;
					entity.motionY = newmotion.yCoord;
					entity.motionZ = newmotion.zCoord;
					
					if (entity instanceof EntityPlayerMP) {
						
//						((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
						
						/*
						counter++;
						if (counter > 100) {
							counter = 0;
							grapplemod.network.sendTo(new PlayerPosMessage(entity.getEntityId(), entity.posX, entity.posY, entity.posZ), (EntityPlayerMP) entity);
						}
						*/
					}
					
					entity.fallDistance = 0;
				}
			}
		}
	}
	
	public void grappleend() {
		if (!this.worldObj.isRemote) {
			this.unattach();
			grapplemod.network.sendToAll(new GrappleEndMessage(this.getEntityId()));
		}
	}
	
	public void unattach() { // call grappleend, not this
		System.out.println("GrappleEnd " + this.toString());
		
		Thread.dumpStack();
		
//		if (this.shootingEntity != null && this.shootingEntity instanceof EntityPlayerMP) {
//			((EntityPlayerMP) this.shootingEntity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(this.shootingEntity));
//		}
		
		this.shootingEntity = null;
		this.attached = false;
		
		FMLCommonHandler.instance().bus().unregister(this);
		
		this.kill();
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
	    
	    this.shootingplayer = (EntityPlayer) this.shootingEntity;
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
				
				this.grappleend();
				return;
			}
			
			this.attached = true;
			System.out.println("attaching! (server) " + this.toString());
			
	        Vec3 vec31 = new Vec3(this.posX, this.posY, this.posZ);
	        Vec3 vec3 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
	        
	        if (movingobjectposition != null)
	        {
	            vec3 = new Vec3(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
	            
	            thispos = vec3;
	            doposupdate = true;
	            
	            this.setPositionAndUpdate(vec3.xCoord, vec3.yCoord, vec3.zCoord);
	        }
	        
			r = this.getDistanceToEntity(this.shootingEntity);
			motion = new Vec3(this.shootingEntity.motionX, this.shootingEntity.motionY, this.shootingEntity.motionZ);
			
			grapplemod.network.sendToAll(new GrappleAttachMessage(this.getEntityId(), this.r, this.posX, this.posY, this.posZ, this.shootingEntity.motionX, this.shootingEntity.motionY, this.shootingEntity.motionZ));
		}
	}
	
	public void clientAttach(double r, double x, double y, double z, double mx, double my, double mz) {
		System.out.println("attaching! (client) " + this.toString());
		
		this.attached = true;
		
		this.setPositionAndUpdate(x, y, z);
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.thispos = new Vec3(x, y, z);
//		this.doposupdate = true;
//		this.r = r;
//		this.motion = new Vec3(mx, my, mz);
		
		this.r = this.getDistanceToEntity(this.shootingEntity);
		this.motion = new Vec3(this.shootingEntity.motionX, this.shootingEntity.motionY, this.shootingEntity.motionZ);
//		System.out.println(motion);
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
}
