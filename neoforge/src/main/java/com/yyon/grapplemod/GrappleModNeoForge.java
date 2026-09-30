package com.yyon.grapplemod;

import com.yyon.grapplemod.common.CommonEventHandlers;
import com.yyon.grapplemod.config.GrappleConfig;
import com.yyon.grapplemod.config.GrappleConfigSpec;
import com.yyon.grapplemod.network.BaseMessageClient;
import com.yyon.grapplemod.network.BaseMessageServer;
import com.yyon.grapplemod.network.GrappleNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(Constants.MODID)
public class GrappleModNeoForge {
	public GrappleModNeoForge(IEventBus eventBus, ModContainer container) {
		eventBus.addListener(ModConfigEvent.Loading.class, event -> onConfigLoad(event.getConfig()));
		eventBus.addListener(ModConfigEvent.Reloading.class, event -> onConfigLoad(event.getConfig()));
		eventBus.addListener(ModConfigEvent.Unloading.class, event -> onConfigUnload(event.getConfig()));
		eventBus.addListener(GrappleModNeoForge::registerPayloads);

		container.registerConfig(ModConfig.Type.STARTUP, GrappleConfig.STARTUP.getSpec());
		GrappleConfig.STARTUP.onLoad();
		container.registerConfig(ModConfig.Type.SERVER, GrappleConfig.SERVER.getSpec());
		container.registerConfig(ModConfig.Type.CLIENT, GrappleConfig.CLIENT.getSpec());

		CommonClass.init();

		NeoForge.EVENT_BUS.addListener(ServerStartedEvent.class, event -> CommonEventHandlers.onServerStarted(event.getServer()));
	}

	private static void registerPayloads(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");
		GrappleNetwork.SERVERBOUND.forEach(payload -> registerServerbound(registrar, payload));
		GrappleNetwork.CLIENTBOUND.forEach(payload -> registerClientbound(registrar, payload));
	}

	private static <T extends BaseMessageServer> void registerServerbound(PayloadRegistrar registrar, GrappleNetwork.Payload<T> payload) {
		registrar.playToServer(payload.type(), payload.codec(), (message, context) -> message.processMessage((ServerPlayer) context.player()));
	}

	private static <T extends BaseMessageClient> void registerClientbound(PayloadRegistrar registrar, GrappleNetwork.Payload<T> payload) {
		registrar.playToClient(payload.type(), payload.codec(), (message, context) -> message.processMessage());
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
