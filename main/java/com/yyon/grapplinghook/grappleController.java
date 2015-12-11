package com.yyon.grapplinghook;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class grappleController {
	public int arrowId;
	public int entityId;
	public World world;
	public Vec3 pos;
	
	public grappleArrow arrow;
	public Entity entity;
	
	public boolean attached = true;
	
	public double r;
	public Vec3 motion;
	
	public double playerforward = 0;
	public double playerstrafe = 0;
	public boolean playerjump = false;
	public Vec3 playermovement = new Vec3(0,0,0);
	
	public int counter = 0;
	
	public grappleController(int arrowId, int entityId, World world, Vec3 pos) {
		System.out.println("GrappleStart " + this.toString());
		
		this.arrowId = arrowId;
		this.entityId = entityId;
		this.world = world;
		this.pos = pos;
		
		this.arrow = (grappleArrow) world.getEntityByID(arrowId);
		this.entity = world.getEntityByID(entityId);
		
		this.r = this.pos.subtract(entity.getPositionVector()).lengthVector();
		this.motion = new Vec3(this.entity.motionX, this.entity.motionY, this.entity.motionZ);
		
		grapplemod.registerController(entityId, this);
	}
	
	public void unattach() {
		System.out.println("GrappleEnd " + this.toString());
		
		this.attached = false;
		
		arrow.remove();
		
		grapplemod.network.sendToServer(new GrappleEndMessage(this.entityId, this.arrowId));
	}
	
	public void doClientTick() {
		if (this.attached) {
			grapplemod.proxy.getplayermovement(this, this.entityId);
			this.updatePlayerPos();
		}
	}
	
	public void receivePlayerMovementMessage(float strafe,
			float forward, boolean jump) {
		playerforward = forward;
		playerstrafe = strafe;
		playerjump = jump;
		playermovement = new Vec3(strafe, 0, forward);
		playermovement = playermovement.rotateYaw((float) (this.entity.rotationYaw * (-Math.PI / 180.0)));
	}
		
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null) {
				if (true) {
					counter++;
					if (counter > 1000) {
						counter = 0;
						System.out.println("pulling " + this.toString());
					}
					
					Vec3 arrowpos = this.pos;//this.getPositionVector();
					Vec3 playerpos = entity.getPositionVector();
//					Vec3 playermotion = new Vec3(entity.motionX, entity.motionY, entity.motionZ);
					
					Vec3 oldspherevec = playerpos.subtract(arrowpos);
					Vec3 spherevec = changelen(oldspherevec, r);
//					Vec3 spherechange = spherevec.subtract(oldspherevec);
//					Vec3 spherepos = spherevec.add(arrowpos);
					
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
							
							this.unattach();
							
//							player.ocity(player.motionX, player.motionY + jumppower, player.motionZ);
							player.motionY = player.motionY + jumppower;
//							if (entity instanceof EntityPlayerMP) {
//								((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
//							}
							
							this.updateServerPos();
							
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
					
//					if (entity instanceof EntityPlayerMP) {
						
//						((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
						
						/*
						counter++;
						if (counter > 100) {
							counter = 0;
							grapplemod.network.sendTo(new PlayerPosMessage(entity.getEntityId(), entity.posX, entity.posY, entity.posZ), (EntityPlayerMP) entity);
						}
						*/
//					}
					
					entity.fallDistance = 0;
					
					this.updateServerPos();
				}
			}
		}
	}
	
	public void updateServerPos() {
		grapplemod.network.sendToServer(new PlayerMovementMessage(this.entityId, this.entity.posX, this.entity.posY, this.entity.posZ, this.entity.motionX, this.entity.motionY, this.entity.motionZ));
	}
	
	// Vector stuff:
	
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

	public void receiveGrappleClick(boolean leftclick) {
		if (!leftclick) {
			this.unattach();
		}
	}

	public void receiveEnderLaunch(double x, double y, double z) {
		System.out.println("wrong!");
	}
}
