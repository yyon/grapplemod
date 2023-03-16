package com.yyon.grapplinghook.common;

import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.items.LongFallBoots;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import com.yyon.grapplinghook.network.LoggedInMessage;
import com.yyon.grapplinghook.server.ServerControllerManager;
import com.yyon.grapplinghook.utils.GrapplemodUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashSet;

public class CommonEventHandlers {

	public CommonEventHandlers() {
	    MinecraftForge.EVENT_BUS.register(this);
		AutoConfig.register(GrappleConfig.class, Toml4jConfigSerializer::new);
	}

	@SubscribeEvent
    public void onBlockBreak(BreakEvent event) {
    	Player player = event.getPlayer();
    	if (player == null) return;

		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		Item item = stack.getItem();
		if (!(item instanceof GrapplehookItem)) return;

		event.setCanceled(true);
    }
    
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
    	if (!event.getEntity().level.isClientSide) {
    		Entity entity = event.getEntity();
    		int id = entity.getId();
    		boolean isconnected = ServerControllerManager.allGrapplehookEntities.containsKey(id);
    		if (isconnected) {
    			HashSet<GrapplehookEntity> grapplehookEntities = ServerControllerManager.allGrapplehookEntities.get(id);
    			for (GrapplehookEntity hookEntity: grapplehookEntities) {
    				hookEntity.removeServer();
    			}
    			grapplehookEntities.clear();

    			ServerControllerManager.attached.remove(id);

				GrapplehookItem.grapplehookEntitiesLeft.remove(entity);
				GrapplehookItem.grapplehookEntitiesRight.remove(entity);
    			
    			GrapplemodUtils.sendToCorrectClient(new GrappleDetachMessage(id), id, entity.level);
    		}
    	}
	}
	
	@SubscribeEvent
	public void onLivingHurtEvent(LivingHurtEvent event) {
		if (event.getEntity() != null && event.getEntity() instanceof Player player) {

			for (ItemStack armor : player.getArmorSlots()) {
			    if (armor != null && armor.getItem() instanceof LongFallBoots) {
			    	if (event.getSource().is(DamageTypes.FLY_INTO_WALL)) {
						// this cancels the fall event so you take no damage
						event.setCanceled(true);
			    	}
			    }
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingFallEvent(LivingFallEvent event) {
		if (event.getEntity() != null && event.getEntity() instanceof Player player) {

			for (ItemStack armor : player.getArmorSlots()) {
			    if (armor != null && armor.getItem() instanceof LongFallBoots) {
					// this cancels the fall event so you take no damage
					event.setCanceled(true);
			    }
			}
		}
	}

	@SubscribeEvent
	public void onServerStart(ServerStartedEvent event) {
		if (GrappleConfig.getConf().other.override_allowflight) {
			event.getServer().setFlightAllowed(true);
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedInEvent(PlayerLoggedInEvent e) {
		if (e.getEntity() instanceof ServerPlayer) {
			CommonSetup.network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.getEntity()), new LoggedInMessage(GrappleConfig.getConf()));
		} else {
			System.out.println("Not an PlayerEntityMP");
		}
	}
}
