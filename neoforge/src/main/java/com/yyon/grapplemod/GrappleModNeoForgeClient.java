package com.yyon.grapplemod;

import com.yyon.grapplemod.client.ClientEventHandlers;
import com.yyon.grapplemod.client.ClientSetup;
import com.yyon.grapplemod.entities.grapplehook.RenderGrapplehookEntity;
import com.yyon.grapplemod.init.EntityInit;
import com.yyon.grapplemod.init.ItemInit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Constants.MODID, dist = Dist.CLIENT)
public class GrappleModNeoForgeClient {
	private static final IKeyConflictContext NON_CONFLICTING = new IKeyConflictContext() {
		@Override
		public boolean isActive() {
			return false;
		}

		@Override
		public boolean conflicts(IKeyConflictContext other) {
			return false;
		}
	};

	public GrappleModNeoForgeClient(IEventBus eventBus, ModContainer container) {
		ClientSetup.init();
		ClientSetup.keyBindings.forEach(key -> key.setKeyConflictContext(NON_CONFLICTING));

		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

		eventBus.addListener(RegisterKeyMappingsEvent.class, event -> ClientSetup.keyBindings.forEach(event::register));
		eventBus.addListener(EntityRenderersEvent.RegisterRenderers.class, event -> event.registerEntityRenderer(EntityInit.GRAPPLEHOOK.get(), context -> new RenderGrapplehookEntity<>(context, ItemInit.GRAPPLING_HOOK.get())));
		eventBus.addListener(FMLClientSetupEvent.class, event -> event.enqueueWork(ClientSetup::registerPropertyOverride));

		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Pre.class, event -> ClientEventHandlers.instance.onClientTick());
		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> ClientEventHandlers.instance.onClientTick());
		NeoForge.EVENT_BUS.addListener(ViewportEvent.ComputeCameraAngles.class, event -> {
			ClientEventHandlers.instance.updateCameraTilt();
			event.setRoll(event.getRoll() + ClientEventHandlers.instance.cameraRoll);
		});
	}
}
