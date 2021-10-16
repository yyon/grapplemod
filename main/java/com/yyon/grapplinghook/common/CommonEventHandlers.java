package com.yyon.grapplinghook.common;

import java.util.HashSet;

import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.entities.grapplearrow.GrapplehookEntity;
import com.yyon.grapplinghook.items.LongFallBoots;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import com.yyon.grapplinghook.network.LoggedInMessage;
import com.yyon.grapplinghook.server.ServerControllerManager;
import com.yyon.grapplinghook.utils.GrapplemodUtils;

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
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CommonEventHandlers {
	public CommonEventHandlers() {
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
	    		if (item instanceof GrapplehookItem) {
	    			event.setCanceled(true);
	    			return;
	    		}
	    	}
    	}
    }
    
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
    	if (!event.getEntity().level.isClientSide) {
    		Entity entity = event.getEntity();
    		int id = entity.getId();
    		boolean isconnected = ServerControllerManager.allarrows.containsKey(id);
    		if (isconnected) {
    			HashSet<GrapplehookEntity> arrows = ServerControllerManager.allarrows.get(id);
    			for (GrapplehookEntity arrow: arrows) {
    				arrow.removeServer();
    			}
    			arrows.clear();

    			ServerControllerManager.attached.remove(id);
    			
    			if (GrapplehookItem.grapplearrows1.containsKey(entity)) {
    				GrapplehookItem.grapplearrows1.remove(entity);
    			}
    			if (GrapplehookItem.grapplearrows2.containsKey(entity)) {
    				GrapplehookItem.grapplearrows2.remove(entity);
    			}
    			
    			GrapplemodUtils.sendtocorrectclient(new GrappleDetachMessage(id), id, entity.level);
    		}
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
			CommonSetup.network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.getPlayer()), new LoggedInMessage(GrappleConfig.getconf()));
		} else {
			System.out.println("Not an PlayerEntityMP");
		}
	}
}
