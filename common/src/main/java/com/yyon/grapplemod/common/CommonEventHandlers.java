package com.yyon.grapplemod.common;

import com.yyon.grapplemod.config.GrappleConfig;
import com.yyon.grapplemod.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplemod.items.GrapplehookItem;
import com.yyon.grapplemod.items.LongFallBoots;
import com.yyon.grapplemod.network.GrappleDetachMessage;
import com.yyon.grapplemod.server.ServerControllerManager;
import com.yyon.grapplemod.utils.GrapplemodUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;

public class CommonEventHandlers {
	public static boolean cancelAttack(Player player) {
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		return stack.getItem() instanceof GrapplehookItem;
	}

	public static void onPlayerDrop(ServerPlayer player) {
		ItemStack stack = player.getInventory().getSelected();
		if (stack.getItem() instanceof GrapplehookItem grapplehookItem) {
			grapplehookItem.onDropped(player);
		}
	}

    public static void onLivingDeath(LivingEntity entity) {
    	if (!entity.level().isClientSide) {
    		int id = entity.getId();
    		boolean isconnected = ServerControllerManager.allGrapplehookEntities.containsKey(id);
    		if (isconnected) {
    			HashSet<GrapplehookEntity> grapplehookEntities = ServerControllerManager.allGrapplehookEntities.get(id);
    			for (GrapplehookEntity hookEntity: grapplehookEntities) {
    				hookEntity.removeServer();
    			}
    			grapplehookEntities.clear();

    			ServerControllerManager.attached.remove(id);

    			if (GrapplehookItem.grapplehookEntitiesLeft.containsKey(entity)) {
    				GrapplehookItem.grapplehookEntitiesLeft.remove(entity);
    			}
    			if (GrapplehookItem.grapplehookEntitiesRight.containsKey(entity)) {
    				GrapplehookItem.grapplehookEntitiesRight.remove(entity);
    			}

    			GrapplemodUtils.sendToCorrectClient(new GrappleDetachMessage(id), id, entity.level());
    		}
    	}
	}

	public static boolean cancelHurt(LivingEntity entity, DamageSource source) {
		return source.is(DamageTypes.FLY_INTO_WALL) && isWearingLongFallBoots(entity);
	}

	public static boolean cancelFall(LivingEntity entity) {
		return isWearingLongFallBoots(entity);
	}

	private static boolean isWearingLongFallBoots(Entity entity) {
		if (entity instanceof Player player) {
			for (ItemStack armor : player.getArmorSlots()) {
			    if (armor != null && armor.getItem() instanceof LongFallBoots) {
			    	return true;
			    }
			}
		}
		return false;
	}

	public static void onServerStarted(MinecraftServer server) {
		if (GrappleConfig.getConf().other.override_allowflight) {
			server.setFlightAllowed(true);
		}
	}
}
