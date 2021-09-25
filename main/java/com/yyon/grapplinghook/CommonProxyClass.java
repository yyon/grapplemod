package com.yyon.grapplinghook;

import java.lang.reflect.Method;

import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.grappleBow;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

	Method capturePosition = null;
	
	public void preInit(FMLPreInitializationEvent event) {
	    MinecraftForge.EVENT_BUS.register(this);

		capturePosition = ObfuscationReflectionHelper.findMethod(NetHandlerPlayServer.class, "func_184342_d", Void.class);
    	if (capturePosition == null) {
    		System.out.println("Error: could not access capturePosition function");
    	}
}

	public void init(FMLInitializationEvent event, grapplemod grapplemod) {
		
	}
	
	public void postInit(FMLPostInitializationEvent event) {
	}
	
	public void sendplayermovementmessage(grappleArrow grappleArrow, int playerid, int arrowid) {
	}

	public void getplayermovement(grappleController control, int playerid) {
	}
	
//	@SubscribeEvent
//	public void onLivingFallEvent(LivingFallEvent event)
//	{
//		if (event.getEntity() != null && grapplemod.attached.contains(event.getEntity().getEntityId()))
//		{
//			event.setCanceled(true);
//		}
//	}
	
	
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
	    	ItemStack stack = player.getHeldItemMainhand();
	    	if (stack != null) {
	    		Item item = stack.getItem();
	    		if (item instanceof grappleBow) {
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
    public void onLivingHurt(LivingHurtEvent event) {
    	if (event.getSource() == DamageSource.IN_WALL) {
    		if (grapplemod.attached.contains(event.getEntity().getEntityId())) {
    			event.setCanceled(true);
    		}
    	}
    }
    
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
    	this.handleDeath(event.getEntity());
    }
    
    public void handleDeath(Entity entity) {
    }
    
	public String getkeyname(CommonProxyClass.keys keyenum) {
		return null;
	}

	public void openModifierScreen(TileEntityGrappleModifier tileent) {
	}
	
	public String localize(String string) {
		return string;
	}

	public void startrocket(EntityPlayer player, GrappleCustomization custom) {
	}
	
	public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio) {
	}

	public double getRocketFunctioning() {
		return 0;
	}

	public boolean iswallrunning(Entity entity, vec motion) {
		return false;
	}
	
	public boolean issliding(Entity entity, vec motion) {
		return false;
	}
	
	public Method getCapturePositionMethod() {
		return capturePosition;
	}
	
	public grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, BlockPos blockpos, GrappleCustomization custom) {
		return null;
	}
}
