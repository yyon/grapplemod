package com.yyon.grapplinghook;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.entities.grappleArrow;


public class CommonProxyClass {
	public void preInit(FMLPreInitializationEvent event) {
		
	}

	public void init(FMLInitializationEvent event, grapplemod grapplemod) {
		
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(this);
	    MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void sendplayermovementmessage(grappleArrow grappleArrow, int playerid, int arrowid) {
	}

	public void getplayermovement(grappleController control, int playerid) {
	}
	
	@SubscribeEvent
	public void onLivingFallEvent(LivingFallEvent event)
	{
		if (event.entity != null && grapplemod.attached.contains(event.entity.getEntityId()))
		{
//			System.out.println("Fall canceled");
			//don't let fall damage occur when attached
			event.setCanceled(true);
		}
	}
	
	
	public void resetlaunchertime(int playerid) {
	}

	public void launchplayer(EntityPlayer player) {
	}
	
	public boolean isSneaking(Entity entity) {
		return entity.isSneaking();
	}
}
