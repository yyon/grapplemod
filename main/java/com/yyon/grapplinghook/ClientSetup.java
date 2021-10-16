package com.yyon.grapplinghook;

import java.util.ArrayList;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import com.yyon.grapplinghook.controllers.airfrictionController;
import com.yyon.grapplinghook.controllers.repelController;
import com.yyon.grapplinghook.entities.RenderGrappleArrow;
import com.yyon.grapplinghook.entities.grappleArrow;

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

	public crosshairRenderer crosshairrenderer;
	public ClientEventHandlers clienteventhandlers;
	public ClientControllerManager clientcontrollermanager;
	
	public static ArrayList<KeyBinding> keyBindings = new ArrayList<KeyBinding>();
	
	public static KeyBinding createkeybinding(KeyBinding k) {
		keyBindings.add(k);
		return k;
	}
	
	public static KeyBinding key_boththrow = createkeybinding(new NonConflictingKeyBinding("key.boththrow.desc", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_2, "key.grapplemod.category"));
	public static KeyBinding key_leftthrow = createkeybinding(new NonConflictingKeyBinding("key.leftthrow.desc", InputMappings.UNKNOWN.getValue(), "key.grapplemod.category"));
	public static KeyBinding key_rightthrow = createkeybinding(new NonConflictingKeyBinding("key.rightthrow.desc", InputMappings.UNKNOWN.getValue(), "key.grapplemod.category"));
	public static KeyBinding key_motoronoff = createkeybinding(new NonConflictingKeyBinding("key.motoronoff.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
	public static KeyBinding key_jumpanddetach = createkeybinding(new NonConflictingKeyBinding("key.jumpanddetach.desc", GLFW.GLFW_KEY_SPACE, "key.grapplemod.category"));
	public static KeyBinding key_slow = createkeybinding(new NonConflictingKeyBinding("key.slow.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
	public static KeyBinding key_climb = createkeybinding(new NonConflictingKeyBinding("key.climb.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
	public static KeyBinding key_climbup = createkeybinding(new NonConflictingKeyBinding("key.climbup.desc", GLFW.GLFW_KEY_W, "key.grapplemod.category"));
	public static KeyBinding key_climbdown = createkeybinding(new NonConflictingKeyBinding("key.climbdown.desc", GLFW.GLFW_KEY_S, "key.grapplemod.category"));
	public static KeyBinding key_enderlaunch = createkeybinding(new NonConflictingKeyBinding("key.enderlaunch.desc", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
	public static KeyBinding key_rocket = createkeybinding(new NonConflictingKeyBinding("key.rocket.desc", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
	public static KeyBinding key_slide = createkeybinding(new NonConflictingKeyBinding("key.slide.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));

	@SubscribeEvent
	public static void clientSetup(final FMLClientSetupEvent event) {
	    instance = new ClientSetup();
	    instance.onClientSetup();
	}
	
	private static class grappleArrowRenderFactory implements IRenderFactory<grappleArrow> {
	    @Override
	    public EntityRenderer<? super grappleArrow> createRenderFor(EntityRendererManager manager) {
	      return new RenderGrappleArrow<>(manager, CommonSetup.grapplebowitem);
	    	
	    }
	}
	
	public void onClientSetup() {
		// register all the key bindings
		for (int i = 0; i < keyBindings.size(); ++i) 
		{
		    ClientRegistry.registerKeyBinding(keyBindings.get(i));
		}
		
	    RenderingRegistry.registerEntityRenderingHandler(CommonSetup.grappleArrowType, new grappleArrowRenderFactory());

	    GuiRegistry registry = AutoConfig.getGuiRegistry(GrappleConfig.class);

		ModLoadingContext.get().registerExtensionPoint(
                ExtensionPoint.CONFIGGUIFACTORY,
                () -> ((ClientProxyClass) CommonProxyClass.proxy)::onConfigScreen);
		
	    this.registerPropertyOverride();
	    
		crosshairrenderer = new crosshairRenderer();
		clientcontrollermanager = new ClientControllerManager();
		clienteventhandlers = new ClientEventHandlers();
	}
	
	public void registerPropertyOverride() {
		ItemModelsProperties.register(CommonSetup.grapplebowitem, new ResourceLocation("rocket"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return CommonSetup.grapplebowitem.getPropertyRocket(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.grapplebowitem, new ResourceLocation("double"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return CommonSetup.grapplebowitem.getPropertyDouble(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.grapplebowitem, new ResourceLocation("motor"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return CommonSetup.grapplebowitem.getPropertyMotor(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.grapplebowitem, new ResourceLocation("smart"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return CommonSetup.grapplebowitem.getPropertySmart(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.grapplebowitem, new ResourceLocation("enderstaff"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return CommonSetup.grapplebowitem.getPropertyEnderstaff(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.grapplebowitem, new ResourceLocation("magnet"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return CommonSetup.grapplebowitem.getPropertyMagnet(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.grapplebowitem, new ResourceLocation("attached"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				if (entity == null) {return 0;}
				return (ClientControllerManager.controllers.containsKey(entity.getId()) && !(ClientControllerManager.controllers.get(entity.getId()) instanceof airfrictionController)) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(CommonSetup.repelleritem, new ResourceLocation("attached"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				if (entity == null) {return 0;}
				return (ClientControllerManager.controllers.containsKey(entity.getId()) && ClientControllerManager.controllers.get(entity.getId()) instanceof repelController) ? 1 : 0;
			}
		});
	}
}
