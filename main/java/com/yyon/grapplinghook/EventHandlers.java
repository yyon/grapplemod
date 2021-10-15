package com.yyon.grapplinghook;

import java.util.HashSet;

import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.LongFallBoots;
import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import com.yyon.grapplinghook.network.LoggedInMessage;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class EventHandlers {
	public EventHandlers() {
	    MinecraftForge.EVENT_BUS.register(this);

		AutoConfig.register(GrappleConfig.class, Toml4jConfigSerializer<GrappleConfig>::new);
	}

	@SubscribeEvent
    public void onBlockBreak(BreakEvent event) {
    	PlayerEntity player = event.getPlayer();
    	if (player != null) {
	    	ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
	    	if (stack != null) {
	    		Item item = stack.getItem();
	    		if (item instanceof grappleBow) {
	    			event.setCanceled(true);
	    			return;
	    		}
	    	}
    	}
    	
    	if (event.getWorld().isClientSide()) {
        	grapplemod.proxy.blockbreak(event);
    	}
    }
    
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
		Entity entity = event.getEntity();
		int id = entity.getId();
		boolean isconnected = grapplemod.allarrows.containsKey(id);
		if (isconnected) {
			HashSet<grappleArrow> arrows = grapplemod.allarrows.get(id);
			for (grappleArrow arrow: arrows) {
				arrow.removeServer();
			}
			arrows.clear();

			grapplemod.attached.remove(id);
			
			if (grapplemod.controllers.containsKey(id)) {
				grapplemod.controllers.remove(id);
			}
			
			if (grappleBow.grapplearrows1.containsKey(entity)) {
				grappleBow.grapplearrows1.remove(entity);
			}
			if (grappleBow.grapplearrows2.containsKey(entity)) {
				grappleBow.grapplearrows2.remove(entity);
			}
			
			grapplemod.sendtocorrectclient(new GrappleDetachMessage(id), id, entity.level);
		}
	}
	
	@SubscribeEvent
	public void onLivingHurtEvent(LivingHurtEvent event) {
		if (event.getEntity() != null && event.getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntity();
			
			for (ItemStack armor : player.getArmorSlots()) {
			    if (armor != null && armor.getItem() instanceof LongFallBoots)
			    {
			    	if (event.getSource() == DamageSource.FLY_INTO_WALL) {
						// this cancels the fall event so you take no damage
						event.setCanceled(true);
			    	}
			    }
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingFallEvent(LivingFallEvent event) {
		if (event.getEntity() != null && event.getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntity();
			
			for (ItemStack armor : player.getArmorSlots()) {
			    if (armor != null && armor.getItem() instanceof LongFallBoots)
			    {
					// this cancels the fall event so you take no damage
					event.setCanceled(true);
			    }
			}
		}
	}

	@SubscribeEvent
	public void onServerStart(FMLServerStartedEvent event) {
		if (GrappleConfig.getconf().other.override_allowflight) {
			event.getServer().setFlightAllowed(true);
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedInEvent(PlayerLoggedInEvent e) {
		if (e.getPlayer() instanceof ServerPlayerEntity) {
			grapplemod.network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.getPlayer()), new LoggedInMessage(GrappleConfig.getconf()));
		} else {
			System.out.println("Not an PlayerEntityMP");
		}
	}
}
