package com.yyon.grapplinghook.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import com.yyon.grapplinghook.enderBow;
import com.yyon.grapplinghook.grappleArrow;
import com.yyon.grapplinghook.grappleController;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.launcherItem;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
						NBTTagCompound compound = grapplemod.getCompound(stack);
//						NBTTagCompound compound = stack.getSubCompound("launcher", true);
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
	
	public World getClientWorld() {
		return null;
	}
}
