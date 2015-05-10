package com.yyon.grapplinghook;

import org.lwjgl.input.Keyboard;

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
	public double v = -1;
	public double oldy = -1;
	
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
	
	
//	public Vec3 oldvel = null;
	
	public grappleArrow(World worldIn) {
		super(worldIn);
	}
	
	public grappleArrow(World worldIn, EntityLivingBase shooter,
			float p_i1756_3_) {
		super(worldIn, shooter);
		FMLCommonHandler.instance().bus().register(this);
		this.shootingEntity = shooter;
		if (this.shootingEntity instanceof EntityPlayer) {
			this.shootingplayer = (EntityPlayer) this.shootingEntity;
			this.shootingEntityID = this.shootingEntity.getEntityId();//((EntityPlayer) this.shootingEntity).getUUID(((EntityPlayer) this.shootingEntity).getGameProfile());
		}
//		MinecraftForge.EVENT_BUS.register(this);
		
	}
	
/*	public void onCollideWithPlayer(EntityPlayer entity){
		super.onCollideWithPlayer(entity);
		int i = MathHelper.floor_double(this.getBoundingBox().minX + 0.001D);
		int j = MathHelper.floor_double(this.getBoundingBox().minY + 0.001D);
		int k = MathHelper.floor_double(this.getBoundingBox().minZ + 0.001D);
		World world = this.worldObj;

	}*/
	public void onEntityUpdate(){
		super.onEntityUpdate();
		
		if(this.shootingEntity == null || !(this.shootingEntity instanceof Entity)) {
			this.grappleend();
		}
		
		if (this.attached && this.firstattach) {
			this.setVelocity(0, 0, 0);
			this.firstattach = false;
		}
		
		
		if (this.doposupdate) {
			this.doposupdate = false;
            this.setPositionAndUpdate(this.thispos.xCoord, this.thispos.yCoord, this.thispos.zCoord);
		}
		/*
//		System.out.println("update");
		int i = MathHelper.floor_double(this.posX + 0.001D);
		int j = MathHelper.floor_double(this.posY + 0.001D);
		int k = MathHelper.floor_double(this.posZ + 0.001D);
		World world = this.worldObj;
//		System.out.println(world.isRemote);
		Entity entity = this.shootingEntity;
//		Entity entity = (Entity)par3EntityPlayer;
		
		if (!this.attached) {
            Vec3 vec31 = new Vec3(this.posX, this.posY, this.posZ);
            Vec3 vec3 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec31, vec3, false, true, false);
            
            if (movingobjectposition != null) {
		        BlockPos blockpos = movingobjectposition.getBlockPos();
	            IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
	            Block block = iblockstate.getBlock();
	
	            if (block.getMaterial() != Material.air) {
					this.attached = true;
					
					r = this.getDistanceToEntity(entity);
					
					/*
					double x = this.posX;
					double y = this.posY;
					double z = this.posZ;
					double dx = entity.posX - x;
					double dy = entity.posY - y;
					double dz = entity.posZ - z;
//					System.out.println(dx);
					x += dx;
					y += dy;
					z += dz;
					
					double mx = entity.motionX;
					double my = entity.motionY;
					double mz = entity.motionZ;
					
					double dot = (mx * dx) + (my * dy) + (mz * dz);
					
					double mr_orig = vlen(mx, my, mz);
					
					mx = mx - dot * dx / r / r;
					my = my - dot * dy / r / r;
					mz = mz - dot * dz / r / r; *
					
					System.out.println(entity.lastTickPosX - entity.posX);
					motion = new Vec3(entity.motionX, entity.motionY, entity.motionZ);
					this.v = motion.lengthVector();//vlen(mx, my, mz);//entity.motionX, entity.motionY, entity.motionZ);
				}
            }
		}
//		this.updatePlayerPos();
		*/
	}
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
//		System.out.println("PLAYERTICK");
//		System.out.println(event.side);
//		System.out.println(event.player.worldObj.isRemote);
		if (event.player.getEntityId() == this.shootingEntityID) {
			if (event.player.worldObj.isRemote) {
				// client
				grapplemod.network.sendToServer(new PlayerMovementMessage(this.getEntityId(), event.player.moveStrafing, event.player.moveForward, ((EntityPlayerSP) event.player).movementInput.jump));
			} else {
				// server
				this.updatePlayerPos(event.player);
			}
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
		
//		System.out.println(entity == theplayer);
//		System.out.println(entity.worldObj.isRemote);
		
		if (this.attached) {
			if(entity != null) {// && entity instanceof EntityPlayer && entity == theplayer) {
//				EntityPlayer player = ((EntityPlayer)this.riddenByEntity);
//				double l = this.getDistanceToEntity(entity);
				if (true) {
					System.out.println("moving");
					Vec3 arrowpos = this.getPositionVector();
					Vec3 playerpos = entity.getPositionVector();
					Vec3 playermotion = new Vec3(entity.motionX, entity.motionY, entity.motionZ);
					
					
					/*
					if (oldvel != null) {
						Vec3 lastpos = new Vec3(player.lastTickPosX, player.lastTickPosY, player.lastTickPosZ);
						Vec3 actualmotion = playerpos.subtract(lastpos);
						if (actualmotion.xCoord == 0 && actualmotion.yCoord == 0 && actualmotion.zCoord == 0) {
							// no motion
						} else {
							Vec3 change = actualmotion.subtract(oldvel);
							printvec(change);
							if (player.isCollided) {
								motion = motion.add(change);
							}
						}
					}
					*/
					Vec3 oldspherevec = playerpos.subtract(arrowpos);
					Vec3 spherevec = changelen(oldspherevec, r);
					Vec3 spherechange = spherevec.subtract(oldspherevec);
					Vec3 spherepos = spherevec.add(arrowpos);
					
//					double actualdx = entity.posX+entity.motionX - x;
//					double actualdy = entity.posY+entity.motionY - y;
//					double actualdz = entity.posZ+entity.motionZ - z;
					
//					Vec3 motion = new Vec3(entity.motionX, entity.motionY, entity.motionZ);
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
							player.setVelocity(player.motionX, player.motionY + jumppower, player.motionZ);
							
							this.grappleend();
							return;
						} else if (this.shootingEntity.isSneaking()) {
							motion = multvec(motion, 0.9);
							if (this.playerforward != 0) {
								if (this.r > this.playerforward * 0.5) {
									this.r -= this.playerforward * 0.5;
								}
								System.out.println(this.r);
							}
						} else {
							motion = motion.add(changelen(this.playermovement, 0.01));
						}
					}
						
					if (!entity.onGround) {
						motion = motion.addVector(0, -0.05, 0);
					}
					
					motion = removealong(motion, spherevec);
					Vec3 newmotion = motion.add(spherechange);
					
					//player.setPositionAndUpdate(player.posX, player.posY, player.posZ);
					
					//motion = changelen(motion, v);
					
					/*
					double dot = (mx * dx) + (my * dy) + (mz * dz);
					
					double mr_orig = vlen(mx, my, mz);
					
					mx = mx - dot * dx / r / r;
					my = my - dot * dy / r / r;
					mz = mz - dot * dz / r / r;
					*/
					
//					double v = 0.4;
					
					/*
					double oldv = vlen(mx, my, mz);
					
					mx = mx / oldv * v;
					my = my / oldv * v;
					mz = mz / oldv * v;
					*/
					
					// add in gravity
					
/*					if (!entity.onGround) {
						my -= 0.08;
						
						dot = (mx * dx) + (my * dy) + (mz * dz);
						
						mx = mx - dot * dx / r / r;
						my = my - dot * dy / r / r;
						mz = mz - dot * dz / r / r;
					}*/
					
					// move to r
					
					/* aaa
					double ndl = vlen(dx, dy, dz);
					double nx = -dx / ndl;
					double ny = -dy / ndl;
					double nz = -dz / ndl;
					
					double nforce = l - r;
//					if (l < r) {
//						nforce *= -1;
//					}
					
					mx += nx * nforce;
					my += ny * nforce;
					mz += nz * nforce;
					
					
					// Fix conservation of energy
					if (oldy != -1) {
						double changeiny = entity.posY - oldy;
						v += -0.08 * changeiny * 2;
					}
					oldy = entity.posY;
					
					v *= 0.995;
					
					System.out.print(l);
					System.out.print(" ");
					System.out.println(r);
					*/
					
					// Fix conservation of energy
					/*
					if (oldy != -1) {
						double changeiny = entity.posY - oldy;
						v += -0.08 * changeiny * 2;
					}
					oldy = entity.posY;
					*/
					
//					double newlen = Math.abs(r * Math.tan(mr_orig / r));
//					double oldlen = vlen(mx, my, mz);
					
//					System.out.println(newlen / oldlen);
					
//					mx = mx * newlen / oldlen;
//					my = my * newlen / oldlen;
//					mz = mz * newlen / oldlen;
					
//					double changeiny = dy;// + my - entity.motionY;
//					System.out.println(dy);

//					mr_orig -= changeiny;
					
/*					double mr_after = vlen(mx, my, mz);
					mx = mx * mr_orig / mr_after;
					my = my * mr_orig / mr_after;
					mz = mz * mr_orig / mr_after;
					
					double nx = x + mx;
					double ny = y + my;
					double nz = z + mz;
					double ndx = nx - this.posX;
					double ndy = ny - this.posY;
					double ndz = nz - this.posZ;
					double nl = vlen(ndx, ndy, ndz);
					ndx = ndx / nl * r;
					ndy = ndy / nl * r;
					ndz = ndz / nl * r;
					nx = this.posX + ndx;
					ny = this.posY + ndy;
					nz = this.posZ + ndz;
					mx = nx - x;
					my = ny - y;
					mz = nz - z;*/
					
//					System.out.println(r);
//					entity.setPositionAndUpdate(spherepos.xCoord, spherepos.yCoord, spherepos.zCoord);
					entity.setVelocity(newmotion.xCoord, newmotion.yCoord, newmotion.zCoord);
					
//					oldvel = newmotion;
					
					if (entity instanceof EntityPlayerMP) {
						
						((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
						
						counter++;
						if (counter > 100 || this.shootingEntity.isSneaking() || spherechange.lengthVector() > 2) {
							counter = 0;
							grapplemod.network.sendTo(new PlayerPosMessage(entity.getEntityId(), entity.posX, entity.posY, entity.posZ), (EntityPlayerMP) entity);
						}
//						((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(new S08PacketPlayerPosLook(x, y, z, entity.rotationYaw, 
						//entity.rotationPitch, Collections.emptySet()));
					}
					
					entity.fallDistance = 0;
				}
				
//				if (this.getDistanceToEntity(this.riddenByEntity) > r) {
//			        this.riddenByEntity.setPosition(this.posX, this.posY, this.posZ);
//				}
				
//				System.out.println(this.getDistanceToEntity(entity));

//		        player.lastTickPosX = player.prevPosX = player.posX = pos.getX();
//		        player.lastTickPosY = player.prevPosY = player.posY = pos.getY();
//		        player.lastTickPosZ = player.prevPosZ = player.posZ = pos.getZ();
//		        player.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
//		        //				player.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());//.setPositionAndUpdate(i, j, k);
			}
		}
	}
	
	public void grappleend() {
		try {
			System.out.println("GrappleEnd");
			if (this.shootingEntity != null && this.shootingEntity instanceof EntityPlayerMP) {
				((EntityPlayerMP) this.shootingEntity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(this.shootingEntity));
			}
			
			this.shootingEntity = null;
			this.kill();
			FMLCommonHandler.instance().bus().unregister(this);
		} catch (Exception e) {
			
		}
	}
	
    public void writeSpawnData(ByteBuf data)
    {
	    data.writeInt(this.shootingEntity != null ? this.shootingEntity.getEntityId() : 0);
    }

    public void readSpawnData(ByteBuf data)
    {
	    this.shootingEntity = this.worldObj.getEntityByID(data.readInt());
    }

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
		if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
			// hit entity
			Entity entityhit = movingobjectposition.entityHit;
			Vec3 playerpos = this.shootingEntity.getPositionVector();
			Vec3 entitypos = entityhit.getPositionVector();
			Vec3 yank = multvec(playerpos.subtract(entitypos), 0.4);
			entityhit.addVelocity(yank.xCoord, Math.min(yank.yCoord, 2), yank.zCoord);
			
			this.grappleend();
			return;
			
//			this.shootingEntity = entityhit;
//			thispos = playerpos;
//            this.setPositionAndUpdate(playerpos.xCoord, playerpos.yCoord, playerpos.zCoord);
		}
		
		this.attached = true;
		
        Vec3 vec31 = new Vec3(this.posX, this.posY, this.posZ);
        Vec3 vec3 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
//        MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec31, vec3, false, true, false);
//        vec31 = new Vec3(this.posX, this.posY, this.posZ);
//        vec3 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        
        if (movingobjectposition != null)
        {
            vec3 = new Vec3(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
            
            thispos = vec3;
            doposupdate = true;
            
            this.setPositionAndUpdate(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            
//    		this.setVelocity(0, 0, 0);

//            Vec3 newvel = vec3.subtract(vec31);
//            System.out.println(newvel.xCoord == 0);
//            this.setVelocity(newvel.xCoord, newvel.yCoord, newvel.zCoord);
        }
        
		r = this.getDistanceToEntity(this.shootingEntity);
		motion = new Vec3(this.shootingEntity.motionX, this.shootingEntity.motionY, this.shootingEntity.motionZ);
		this.v = motion.lengthVector();//vlen(mx, my, mz);//entity.motionX, entity.motionY, entity.motionZ);
		
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
