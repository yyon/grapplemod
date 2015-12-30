package com.yyon.grapplinghook;

import net.minecraft.entity.Entity;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;


public class ServerProxyClass extends CommonProxyClass {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event, grapplemod grapplemod) {
		super.init(event, grapplemod);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	/*
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		for (int entityid : grapplemod.attached) {
		}
	}
	*/
	
	@Override
	public void handleDeath(Entity entity) {
		grapplemod.attached.remove(new Integer(entity.getEntityId()));
	}
}
