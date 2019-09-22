package com.yyon.grapplinghook;

import com.yyon.grapplinghook.network.LoggedInMessage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;



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
	
	@Override
	public void handleDeath(Entity entity) {
		grapplemod.attached.remove(new Integer(entity.getEntityId()));
	}
	
	@SubscribeEvent
	public void onPlayerLoggedInEvent(PlayerLoggedInEvent e) {
		System.out.println("Player logged in event");
		if (e.player instanceof EntityPlayerMP) {
			grapplemod.network.sendTo(new LoggedInMessage(GrappleConfig.options), (EntityPlayerMP) e.player);
		} else {
			System.out.println("Not an EntityPlayerMP");
		}
	}
}
