package com.yyon.grapplinghook;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.LongFallBoots;
import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.items.multiBow;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommonProxyClass {
	public enum keys {
		keyBindUseItem,
		keyBindForward,
		keyBindLeft,
		keyBindBack,
		keyBindRight,
		keyBindJump,
		keyBindSneak,
		keyBindAttack
	}
	
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
	
    @SubscribeEvent
    public void onBlockBreak(BreakEvent event){
    	EntityPlayer player = event.getPlayer();
    	if (player != null) {
	    	ItemStack stack = player.getHeldItem();
	    	if (stack != null) {
	    		Item item = stack.getItem();
	    		if (item instanceof grappleBow || item instanceof multiBow) {
	    			event.setCanceled(true);
	    			return;
	    		}
	    	}
    	}
    	
    	this.blockbreak(event);
    }
    
    
    public void blockbreak(BreakEvent event) {
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event){
    	if (event.source == DamageSource.inWall) {
    		if (grapplemod.attached.contains(event.entity.getEntityId())) {
    			event.setCanceled(true);
    			return;
    		}
    	}
   	}
    
    /*
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
    	if (event.source == DamageSource.inWall) {
    		if (grapplemod.attached.contains(event.entity.getEntityId())) {
    			event.setCanceled(true);
    			return;
    		}
    	}
    }
    */
    
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
    	this.handleDeath(event.entity);
    }
    
    public void handleDeath(Entity entity) {
    }
    
	public String getkeyname(CommonProxyClass.keys keyenum) {
		return null;
	}
}
