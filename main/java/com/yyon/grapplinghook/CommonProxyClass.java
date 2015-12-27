package com.yyon.grapplinghook;

import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.enderBow;
import com.yyon.grapplinghook.items.launcherItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


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
	
	
	public void resetlaunchertime(EntityPlayer player) {
		if (player != null) {
			ItemStack stack = player.getHeldItem();
			if (stack != null) {
				Item item = stack.getItem();
				if (item instanceof launcherItem || item instanceof enderBow) {
					if (player.onGround) {
						NBTTagCompound compound = stack.getSubCompound("launcher", true);
						if (compound.getLong("lastused") != 0) {
							long timer = player.worldObj.getTotalWorldTime() - compound.getLong("lastused");
							if (timer > 10) {
								compound.setLong("lastused", 0);
							}
						}
					}
				}
			}
		}
	}
}
