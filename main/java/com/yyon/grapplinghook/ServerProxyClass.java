package com.yyon.grapplinghook;

import com.yyon.grapplinghook.network.LoggedInMessage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;



public class ServerProxyClass extends CommonProxyClass {
	@Override
	public void init(FMLCommonSetupEvent event) {
		super.init(event);
		if (GrappleConfig.getconf().override_allowflight) {
			ServerLifecycleHooks.getCurrentServer().setAllowFlight(true);
		}
	}
	
	@Override
	public void handleDeath(Entity entity) {
		grapplemod.attached.remove(new Integer(entity.getEntityId()));
	}
	
	@SubscribeEvent
	public void onPlayerLoggedInEvent(PlayerLoggedInEvent e) {
		System.out.println("Player logged in event");
		PlayerEntity player = e.getPlayer();
		if (player instanceof ServerPlayerEntity) {
			grapplemod.network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new LoggedInMessage(GrappleConfig.options));
		} else {
			System.out.println("Not an EntityPlayerMP");
		}
	}
}
