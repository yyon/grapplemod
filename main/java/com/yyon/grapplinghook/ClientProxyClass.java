package com.yyon.grapplinghook;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.entities.RenderGrappleArrow;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.clickitem;
import com.yyon.grapplinghook.items.enderBow;
import com.yyon.grapplinghook.items.launcherItem;
import com.yyon.grapplinghook.network.PlayerMovementMessage;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;


public class ClientProxyClass extends CommonProxyClass {
	public boolean leftclick;
	public HashMap<Integer, Long> enderlaunchtimer = new HashMap<Integer, Long>();
	public final int reusetime = 50;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}
	
	@Override
	public void init(FMLInitializationEvent event, grapplemod grappleModInst) {
		super.init(event, grappleModInst);
		/*
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.grapplebowitem, 0, new ModelResourceLocation("grapplemod:grapplinghook", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.hookshotitem, 0, new ModelResourceLocation("grapplemod:hookshot", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.launcheritem, 0, new ModelResourceLocation("grapplemod:launcheritem", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.longfallboots, 0, new ModelResourceLocation("grapplemod:longfallboots", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.enderhookitem, 0, new ModelResourceLocation("grapplemod:enderhook", "inventory"));
		RenderingRegistry.registerEntityRenderingHandler(grappleArrow.class, 
				new RenderGrappleArrow(Minecraft.getMinecraft().getRenderManager(), Items.iron_pickaxe, Minecraft.getMinecraft().getRenderItem()));
		*/
		
		RenderGrappleArrow rga = new RenderGrappleArrow(Items.iron_pickaxe);
		RenderingRegistry.registerEntityRenderingHandler(grappleArrow.class, rga);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	/*
	@Override
	public void sendplayermovementmessage(grappleArrow grappleArrow, int playerid, int arrowid) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (player.getEntityId() == playerid) {
			grapplemod.network.sendToServer(new PlayerMovementMessage(arrowid, player.moveStrafing, player.moveForward, ((EntityPlayerSP) player).movementInput.jump));
			grappleArrow.receivePlayerMovementMessage(player.moveStrafing, player.moveForward, ((EntityPlayerSP) player).movementInput.jump);
		}
	}
	*/
	
	@Override
	public void getplayermovement(grappleController control, int playerid) {
//		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
//		if (player == null) {
//			System.out.println("NULL PLAYER!");
//			return;
//		}
		Entity entity = control.entity;
		if (entity instanceof EntityPlayerSP) {
//		if (player.getEntityId() == playerid) {
			EntityPlayerSP player = (EntityPlayerSP) entity;
			control.receivePlayerMovementMessage(player.moveStrafing, player.moveForward, player.movementInput.jump);
		}
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (player != null) {
			if (!Minecraft.getMinecraft().isGamePaused() || !Minecraft.getMinecraft().isSingleplayer()) {
				try {
					Collection<grappleController> controllers = grapplemod.controllers.values();
					for (grappleController controller : controllers) {
						controller.doClientTick();
					}
				} catch (ConcurrentModificationException e) {
					System.out.println("ConcurrentModificationException caught");
				}
				
				if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindAttack) && Minecraft.getMinecraft().currentScreen == null) {
					leftclick = true;
				} else if (leftclick) {
					leftclick = false;
					if (player != null) {
						ItemStack stack = player.getHeldItem();
						if (stack != null) {
							Item item = stack.getItem();
							if (item instanceof clickitem) {
								((clickitem)item).onLeftClick(stack, player);
			//								this.leftclick(stack, player.worldObj, player);
							}
						}
					}
				}
				
				if (player.onGround) {
					if (enderlaunchtimer.containsKey(player.getEntityId())) {
						long timer = player.worldObj.getTotalWorldTime() - enderlaunchtimer.get(player.getEntityId());
						if (timer > 10) {
							this.resetlaunchertime(player.getEntityId());
						}
					}
				}
			}
		}
	}
	
	@Override
	public void launchplayer(EntityPlayer player) {
		long prevtime;
		if (enderlaunchtimer.containsKey(player.getEntityId())) {
			prevtime = enderlaunchtimer.get(player.getEntityId());
		} else {
			prevtime = 0;
		}
		long timer = player.worldObj.getTotalWorldTime() - prevtime;
		if (timer > reusetime) {
			if (player.getHeldItem().getItem() instanceof enderBow || player.getHeldItem().getItem() instanceof launcherItem) {
				
	//			playerused = player;
	//			reusetimer = reusetime;
//				compound.setLong("lastused", world.getTotalWorldTime());
				enderlaunchtimer.put(player.getEntityId(), player.worldObj.getTotalWorldTime());
				
	        	vec facing = new vec(player.getLookVec());
				vec playermotion = vec.motionvec(player);
				vec newvec = playermotion.add(facing.mult(3));
				
//				grappleArrow arrow = this.getArrow(stack, world);
				
				if (!grapplemod.controllers.containsKey(player.getEntityId())) {
//					player.setVelocity(newvec.xCoord, newvec.yCoord, newvec.zCoord);
					player.motionX = newvec.x;
					player.motionY = newvec.y;
					player.motionZ = newvec.z;
					
					if (player instanceof EntityPlayerMP) {
						((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
					} else {
						grapplemod.network.sendToServer(new PlayerMovementMessage(player.getEntityId(), player.posX, player.posY, player.posZ, player.motionX, player.motionY, player.motionZ));
					}
				} else {
					facing.mult_ip(3);
//					if (player instanceof EntityPlayerMP) {
//						System.out.println("Sending EnderGrappleLaunchMessage");
//						grapplemod.sendtocorrectclient(new EnderGrappleLaunchMessage(player.getEntityId(), facing.xCoord, facing.yCoord, facing.zCoord), player.getEntityId(), player.worldObj);
//					} else {
					grapplemod.receiveEnderLaunch(player.getEntityId(), facing.x, facing.y, facing.z);
//					}

//					arrow.control.motion = arrow.control.motion.add(newvec);
				}
			}
		}
	}
	
	@Override
	public void resetlaunchertime(int playerid) {
		if (enderlaunchtimer.containsKey(playerid)) {
			enderlaunchtimer.put(playerid, (long) 0);
		}
	}
	
	@Override
	public boolean isSneaking(Entity entity) {
		if (entity == Minecraft.getMinecraft().thePlayer) {
			return (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak));
		} else {
			return entity.isSneaking();
		}
	}
	
	@Override
    public void blockbreak(BreakEvent event) {
		BlockPos eventpos = new BlockPos(event.x, event.y, event.z);
		if (eventpos != null) {
			if (grapplemod.controllerpos.containsKey(eventpos)) {
				grappleController control = grapplemod.controllerpos.get(eventpos);
				control.unattach();
				grapplemod.controllerpos.remove(eventpos);
			}
		}
    }
	
	@Override
	public void handleDeath(Entity entity) {
		int id = entity.getEntityId();
		if (grapplemod.controllers.containsKey(id)) {
			grappleController controller = grapplemod.controllers.get(id);
			controller.unattach();
		}
	}
}
