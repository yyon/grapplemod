package com.yyon.grapplemod;

import com.yyon.grapplemod.common.CommonEventHandlers;
import com.yyon.grapplemod.config.GrappleConfig;
import com.yyon.grapplemod.config.GrappleConfigSpec;
import com.yyon.grapplemod.network.BaseMessageClient;
import com.yyon.grapplemod.network.BaseMessageServer;
import com.yyon.grapplemod.network.GrappleNetwork;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.neoforged.fml.config.ModConfig;

public class GrappleModFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		NeoForgeModConfigEvents.loading(Constants.MODID).register(GrappleModFabric::onConfigLoad);
		NeoForgeModConfigEvents.reloading(Constants.MODID).register(GrappleModFabric::onConfigLoad);
		NeoForgeModConfigEvents.unloading(Constants.MODID).register(GrappleModFabric::onConfigUnload);

		NeoForgeConfigRegistry.INSTANCE.register(Constants.MODID, ModConfig.Type.STARTUP, GrappleConfig.STARTUP.getSpec());
		GrappleConfig.STARTUP.onLoad();
		NeoForgeConfigRegistry.INSTANCE.register(Constants.MODID, ModConfig.Type.SERVER, GrappleConfig.SERVER.getSpec());
		NeoForgeConfigRegistry.INSTANCE.register(Constants.MODID, ModConfig.Type.CLIENT, GrappleConfig.CLIENT.getSpec());

		CommonClass.init();

		GrappleNetwork.SERVERBOUND.forEach(GrappleModFabric::registerServerbound);
		GrappleNetwork.CLIENTBOUND.forEach(GrappleModFabric::registerClientbound);

		ServerLifecycleEvents.SERVER_STARTED.register(CommonEventHandlers::onServerStarted);
	}

	private static <T extends BaseMessageServer> void registerServerbound(GrappleNetwork.Payload<T> payload) {
		PayloadTypeRegistry.playC2S().register(payload.type(), payload.codec());
		ServerPlayNetworking.registerGlobalReceiver(payload.type(), (message, context) -> message.processMessage(context.player()));
	}

	private static <T extends BaseMessageClient> void registerClientbound(GrappleNetwork.Payload<T> payload) {
		PayloadTypeRegistry.playS2C().register(payload.type(), payload.codec());
	}

	private static void onConfigLoad(ModConfig config) {
		GrappleConfigSpec spec = GrappleConfig.getSpec(config.getSpec());
		if (spec != null) {
			spec.onLoad();
		}
	}

	private static void onConfigUnload(ModConfig config) {
		GrappleConfigSpec spec = GrappleConfig.getSpec(config.getSpec());
		if (spec != null) {
			spec.onUnload();
		}
	}
}
