package com.yyon.grapplinghook.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.yyon.grapplinghook.RenderGrappleArrow;
import com.yyon.grapplinghook.clickitem;
import com.yyon.grapplinghook.grappleArrow;
import com.yyon.grapplinghook.grappleController;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.common.CommonProxyClass;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ClientProxyClass extends CommonProxyClass {
	public boolean leftclick = false;
	public boolean rightclick = false;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}
	
	@Override
	public void init(FMLInitializationEvent event, grapplemod grappleModInst) {
		super.init(event, grappleModInst);
		RenderGrappleArrow rga = new RenderGrappleArrow(Items.iron_pickaxe);
		RenderingRegistry.registerEntityRenderingHandler(grappleArrow.class, rga);
//		RenderingRegistry.registerEntityRenderingHandler(hookArrow.class, rga);
//		RenderingRegistry.registerEntityRenderingHandler(enderArrow.class, rga);
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
		for (grappleController controller : grapplemod.controllers.values()) {
			controller.doClientTick();
		}
		
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		resetlaunchertime(player);
		
		if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindAttack)) {
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
		
		/*
		if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindUseItem)) {
			rightclick = true;
		} else if (rightclick) {
			rightclick = false;
			if (player != null) {
				ItemStack stack = player.getHeldItem();
				if (stack != null) {
					Item item = stack.getItem();
					if (item instanceof clickitem) {
						((clickitem)item).onRightClick(stack, player);
	//								this.leftclick(stack, player.worldObj, player);
					}
				}
			}
		}
		*/
	}
	
	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
}
