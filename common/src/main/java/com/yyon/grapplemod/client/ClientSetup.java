package com.yyon.grapplemod.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.yyon.grapplemod.client.ClientProxyInterface.GrappleKeys;
import com.yyon.grapplemod.controllers.AirfrictionController;
import com.yyon.grapplemod.controllers.ForcefieldController;
import com.yyon.grapplemod.init.ItemInit;
import com.yyon.grapplemod.mixin.client.ItemPropertiesInvoker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class ClientSetup {
	public static ClientSetup instance = null;

	public CrosshairRenderer crosshairRenderer;
	public ClientEventHandlers clientEventHandlers;
	public ClientControllerManager clientControllerManager;

	public static ArrayList<KeyMapping> keyBindings = new ArrayList<KeyMapping>();

	public static KeyMapping createKeyBinding(KeyMapping k) {
		keyBindings.add(k);
		return k;
	}

	public static KeyMapping key_boththrow = createKeyBinding(new NonConflictingKeyBinding("key.boththrow.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_2, "key.grapplemod.category"));
	public static KeyMapping key_leftthrow = createKeyBinding(new NonConflictingKeyBinding("key.leftthrow.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
	public static KeyMapping key_rightthrow = createKeyBinding(new NonConflictingKeyBinding("key.rightthrow.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
	public static KeyMapping key_motoronoff = createKeyBinding(new NonConflictingKeyBinding("key.motoronoff.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
	public static KeyMapping key_jumpanddetach = createKeyBinding(new NonConflictingKeyBinding("key.jumpanddetach.desc", GLFW.GLFW_KEY_SPACE, "key.grapplemod.category"));
	public static KeyMapping key_slow = createKeyBinding(new NonConflictingKeyBinding("key.slow.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
	public static KeyMapping key_climb = createKeyBinding(new NonConflictingKeyBinding("key.climb.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
	public static KeyMapping key_climbup = createKeyBinding(new NonConflictingKeyBinding("key.climbup.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
	public static KeyMapping key_climbdown = createKeyBinding(new NonConflictingKeyBinding("key.climbdown.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
	public static KeyMapping key_enderlaunch = createKeyBinding(new NonConflictingKeyBinding("key.enderlaunch.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
	public static KeyMapping key_rocket = createKeyBinding(new NonConflictingKeyBinding("key.rocket.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
	public static KeyMapping key_slide = createKeyBinding(new NonConflictingKeyBinding("key.slide.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));

	public static KeyMapping getKeyMapping(GrappleKeys key) {
		return switch (key) {
			case key_boththrow -> key_boththrow;
			case key_leftthrow -> key_leftthrow;
			case key_rightthrow -> key_rightthrow;
			case key_motoronoff -> key_motoronoff;
			case key_jumpanddetach -> key_jumpanddetach;
			case key_slow -> key_slow;
			case key_climb -> key_climb;
			case key_climbup -> key_climbup;
			case key_climbdown -> key_climbdown;
			case key_enderlaunch -> key_enderlaunch;
			case key_rocket -> key_rocket;
			case key_slide -> key_slide;
		};
	}

	public static void init() {
		ClientProxyInterface.proxy = new ClientProxy();
		instance = new ClientSetup();
		instance.crosshairRenderer = new CrosshairRenderer();
		instance.clientControllerManager = new ClientControllerManager();
		instance.clientEventHandlers = new ClientEventHandlers();
	}

	public static void registerPropertyOverride() {
		register(ItemInit.GRAPPLING_HOOK.get(), "rocket", (stack, world, entity, seed) -> ItemInit.GRAPPLING_HOOK.get().getPropertyRocket(stack, world, entity) ? 1 : 0);
		register(ItemInit.GRAPPLING_HOOK.get(), "double", (stack, world, entity, seed) -> ItemInit.GRAPPLING_HOOK.get().getPropertyDouble(stack, world, entity) ? 1 : 0);
		register(ItemInit.GRAPPLING_HOOK.get(), "motor", (stack, world, entity, seed) -> ItemInit.GRAPPLING_HOOK.get().getPropertyMotor(stack, world, entity) ? 1 : 0);
		register(ItemInit.GRAPPLING_HOOK.get(), "smart", (stack, world, entity, seed) -> ItemInit.GRAPPLING_HOOK.get().getPropertySmart(stack, world, entity) ? 1 : 0);
		register(ItemInit.GRAPPLING_HOOK.get(), "enderstaff", (stack, world, entity, seed) -> ItemInit.GRAPPLING_HOOK.get().getPropertyEnderstaff(stack, world, entity) ? 1 : 0);
		register(ItemInit.GRAPPLING_HOOK.get(), "magnet", (stack, world, entity, seed) -> ItemInit.GRAPPLING_HOOK.get().getPropertyMagnet(stack, world, entity) ? 1 : 0);
		register(ItemInit.GRAPPLING_HOOK.get(), "attached", (stack, world, entity, seed) -> {
			if (entity == null) {return 0;}
			return (ClientControllerManager.controllers.containsKey(entity.getId()) && !(ClientControllerManager.controllers.get(entity.getId()) instanceof AirfrictionController)) ? 1 : 0;
		});
		register(ItemInit.FORCEFIELD.get(), "attached", (stack, world, entity, seed) -> {
			if (entity == null) {return 0;}
			return (ClientControllerManager.controllers.containsKey(entity.getId()) && ClientControllerManager.controllers.get(entity.getId()) instanceof ForcefieldController) ? 1 : 0;
		});
		register(ItemInit.GRAPPLING_HOOK.get(), "hook", (stack, world, entity, seed) -> ItemInit.GRAPPLING_HOOK.get().getPropertyHook(stack, world, entity) ? 1 : 0);
	}

	private static void register(Item item, String name, ClampedItemPropertyFunction property) {
		ItemPropertiesInvoker.grapplemod$register(item, ResourceLocation.withDefaultNamespace(name), property);
	}
}
