package com.yyon.grapplinghook.client;

import java.util.ArrayList;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.controllers.AirfrictionController;
import com.yyon.grapplinghook.controllers.ForcefieldController;
import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.entities.grapplehook.RenderGrapplehookEntity;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
	public static ClientSetup instance = null;
	
	public ClientSetup() {
	}

	public CrosshairRenderer crosshairRenderer;
	public ClientEventHandlers clientEventHandlers;
	public ClientControllerManager clientControllerManager;
	
	public static ArrayList<KeyBinding> keyBindings = new ArrayList<KeyBinding>();
	
	public static KeyBinding createKeyBinding(KeyBinding k) {
		keyBindings.add(k);
		return k;
	}
	
	public static KeyBinding key_boththrow = createKeyBinding(new NonConflictingKeyBinding("key.boththrow.desc", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_2, "key.grapplemod.category"));
	public static KeyBinding key_leftthrow = createKeyBinding(new NonConflictingKeyBinding("key.leftthrow.desc", InputMappings.UNKNOWN.getValue(), "key.grapplemod.category"));
	public static KeyBinding key_rightthrow = createKeyBinding(new NonConflictingKeyBinding("key.rightthrow.desc", InputMappings.UNKNOWN.getValue(), "key.grapplemod.category"));
	public static KeyBinding key_motoronoff = createKeyBinding(new NonConflictingKeyBinding("key.motoronoff.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
	public static KeyBinding key_jumpanddetach = createKeyBinding(new NonConflictingKeyBinding("key.jumpanddetach.desc", GLFW.GLFW_KEY_SPACE, "key.grapplemod.category"));
	public static KeyBinding key_slow = createKeyBinding(new NonConflictingKeyBinding("key.slow.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
	public static KeyBinding key_climb = createKeyBinding(new NonConflictingKeyBinding("key.climb.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
	public static KeyBinding key_climbup = createKeyBinding(new NonConflictingKeyBinding("key.climbup.desc", GLFW.GLFW_KEY_W, "key.grapplemod.category"));
	public static KeyBinding key_climbdown = createKeyBinding(new NonConflictingKeyBinding("key.climbdown.desc", GLFW.GLFW_KEY_S, "key.grapplemod.category"));
	public static KeyBinding key_enderlaunch = createKeyBinding(new NonConflictingKeyBinding("key.enderlaunch.desc", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
	public static KeyBinding key_rocket = createKeyBinding(new NonConflictingKeyBinding("key.rocket.desc", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
	public static KeyBinding key_slide = createKeyBinding(new NonConflictingKeyBinding("key.slide.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));

	@SubscribeEvent
	public static void clientSetup(final FMLClientSetupEvent event) {
	    instance = new ClientSetup();
	    instance.onClientSetup();
	}
	
	private static class GrapplehookEntityRenderFactory implements IRenderFactory<GrapplehookEntity> {
	    @Override
	    public EntityRenderer<? super GrapplehookEntity> createRenderFor(EntityRendererManager manager) {
	      return new RenderGrapplehookEntity<>(manager, CommonSetup.grapplingHookItem);
	    	
	    }
	}
	
	public void onClientSetup() {
		// register all the key bindings
		for (int i = 0; i < keyBindings.size(); ++i) 
		{
		    ClientRegistry.registerKeyBinding(keyBindings.get(i));
		}
		
	    RenderingRegistry.registerEntityRenderingHandler(CommonSetup.grapplehookEntityType, new GrapplehookEntityRenderFactory());

		ModLoadingContext.get().registerExtensionPoint(
                ExtensionPoint.CONFIGGUIFACTORY,
                () -> ((ClientProxy) ClientProxyInterface.proxy)::onConfigScreen);
		
	    this.registerPropertyOverride();
	    
		crosshairRenderer = new CrosshairRenderer();
		clientControllerManager = new ClientControllerManager();
		clientEventHandlers = new ClientEventHandlers();
	}
	
	public void registerPropertyOverride() {
		ItemModelsProperties.register(CommonSetup.grapplingHookItem, new ResourceLocation("rocket"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return CommonSetup.grapplingHookItem.getPropertyRocket(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.grapplingHookItem, new ResourceLocation("double"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return CommonSetup.grapplingHookItem.getPropertyDouble(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.grapplingHookItem, new ResourceLocation("motor"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return CommonSetup.grapplingHookItem.getPropertyMotor(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.grapplingHookItem, new ResourceLocation("smart"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return CommonSetup.grapplingHookItem.getPropertySmart(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.grapplingHookItem, new ResourceLocation("enderstaff"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return CommonSetup.grapplingHookItem.getPropertyEnderstaff(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.grapplingHookItem, new ResourceLocation("magnet"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return CommonSetup.grapplingHookItem.getPropertyMagnet(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.grapplingHookItem, new ResourceLocation("attached"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				if (entity == null) {return 0;}
				return (ClientControllerManager.controllers.containsKey(entity.getId()) && !(ClientControllerManager.controllers.get(entity.getId()) instanceof AirfrictionController)) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.forcefieldItem, new ResourceLocation("attached"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				if (entity == null) {return 0;}
				return (ClientControllerManager.controllers.containsKey(entity.getId()) && ClientControllerManager.controllers.get(entity.getId()) instanceof ForcefieldController) ? 1 : 0;
			}
		});
	}
}
