package com.yyon.grapplemod;

import com.yyon.grapplemod.client.ClientEventHandlers;
import com.yyon.grapplemod.client.ClientSetup;
import com.yyon.grapplemod.entities.grapplehook.RenderGrapplehookEntity;
import com.yyon.grapplemod.init.EntityInit;
import com.yyon.grapplemod.init.ItemInit;
import com.yyon.grapplemod.network.BaseMessageClient;
import com.yyon.grapplemod.network.GrappleNetwork;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class GrappleModFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientSetup.init();
		ClientSetup.keyBindings.forEach(KeyBindingHelper::registerKeyBinding);
		ClientSetup.registerPropertyOverride();

		EntityRendererRegistry.register(EntityInit.GRAPPLEHOOK.get(), context -> new RenderGrapplehookEntity<>(context, ItemInit.GRAPPLING_HOOK.get()));

		GrappleNetwork.CLIENTBOUND.forEach(GrappleModFabricClient::registerClientbound);

		ClientTickEvents.START_CLIENT_TICK.register(client -> ClientEventHandlers.instance.onClientTick());
		ClientTickEvents.END_CLIENT_TICK.register(client -> ClientEventHandlers.instance.onClientTick());

		ConfigScreenFactoryRegistry.INSTANCE.register(Constants.MODID, ConfigurationScreen::new);
	}

	private static <T extends BaseMessageClient> void registerClientbound(GrappleNetwork.Payload<T> payload) {
		ClientPlayNetworking.registerGlobalReceiver(payload.type(), (message, context) -> message.processMessage());
	}
}
