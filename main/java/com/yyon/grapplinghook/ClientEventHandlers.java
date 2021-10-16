package com.yyon.grapplinghook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import com.yyon.grapplinghook.controllers.airfrictionController;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.controllers.repelController;
import com.yyon.grapplinghook.entities.RenderGrappleArrow;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.KeypressItem;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandlers {
	public static ClientEventHandlers instance = null;
	
	public ClientEventHandlers() {
	    MinecraftForge.EVENT_BUS.register(this);
	}
	
	public crosshairRenderer crosshairrenderer;
	
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
	    instance = new ClientEventHandlers();
	    instance.onClientSetup();
	}
	
	public ClientControllerManager clientcontrollermanager;
	
	public void onClientSetup() {
		// register all the key bindings
		for (int i = 0; i < keyBindings.size(); ++i) 
		{
		    ClientRegistry.registerKeyBinding(keyBindings.get(i));
		}
		
	    RenderingRegistry.registerEntityRenderingHandler(grapplemod.grappleArrowType, new grappleArrowRenderFactory());

	    GuiRegistry registry = AutoConfig.getGuiRegistry(GrappleConfig.class);

		ModLoadingContext.get().registerExtensionPoint(
                ExtensionPoint.CONFIGGUIFACTORY,
                () -> ((ClientProxyClass) grapplemod.proxy)::onConfigScreen);
		
	    this.registerPropertyOverride();
		crosshairrenderer = new crosshairRenderer();
		
		clientcontrollermanager = new ClientControllerManager();
	}

	private static class grappleArrowRenderFactory implements IRenderFactory<grappleArrow> {
	    @Override
	    public EntityRenderer<? super grappleArrow> createRenderFor(EntityRendererManager manager) {
	      return new RenderGrappleArrow<>(manager, grapplemod.grapplebowitem);
	    	
	    }
	}
	
	public boolean prevkeys[] = {false, false, false, false, false};
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (player != null) {
			if (!Minecraft.getInstance().isPaused()) {
				this.clientcontrollermanager.onClientTick(player);
				
				if (Minecraft.getInstance().screen == null) {
					// keep in same order as enum from KeypressItem
					boolean keys[] = {key_enderlaunch.isDown(), key_leftthrow.isDown(), key_rightthrow.isDown(), key_boththrow.isDown(), key_rocket.isDown()};
					
					for (int i = 0; i < keys.length; i++) {
						boolean iskeydown = keys[i];
						boolean prevkey = prevkeys[i];
						
						if (iskeydown != prevkey) {
							KeypressItem.Keys key = KeypressItem.Keys.values()[i];
							
							ItemStack stack = getKeypressStack(player);
							if (stack != null) {
								if (!isLookingAtModifierBlock(player)) {
									if (iskeydown) {
										((KeypressItem) stack.getItem()).onCustomKeyDown(stack, player, key, true);
									} else {
										((KeypressItem) stack.getItem()).onCustomKeyUp(stack, player, key, true);
									}
								}
							}
						}
						
						prevkeys[i] = iskeydown;
					}
				}
			}
		}
	}

	public void registerPropertyOverride() {
		ItemModelsProperties.register(grapplemod.grapplebowitem, new ResourceLocation("rocket"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return grapplemod.grapplebowitem.getPropertyRocket(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(grapplemod.grapplebowitem, new ResourceLocation("double"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return grapplemod.grapplebowitem.getPropertyDouble(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(grapplemod.grapplebowitem, new ResourceLocation("motor"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return grapplemod.grapplebowitem.getPropertyMotor(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(grapplemod.grapplebowitem, new ResourceLocation("smart"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return grapplemod.grapplebowitem.getPropertySmart(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(grapplemod.grapplebowitem, new ResourceLocation("enderstaff"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return grapplemod.grapplebowitem.getPropertyEnderstaff(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(grapplemod.grapplebowitem, new ResourceLocation("magnet"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				return grapplemod.grapplebowitem.getPropertyMagnet(stack, world, entity) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(grapplemod.grapplebowitem, new ResourceLocation("attached"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				if (entity == null) {return 0;}
				return grapplemod.attached.contains(entity.getId()) ? 1 : 0;
			}
		});
		ItemModelsProperties.register(grapplemod.repelleritem, new ResourceLocation("attached"), new IItemPropertyGetter() {
			public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
				if (entity == null) {return 0;}
				return (grapplemod.controllers.containsKey(entity.getId()) && grapplemod.controllers.get(entity.getId()) instanceof repelController) ? 1 : 0;
			}
		});
	}
	
	@SubscribeEvent
    public void blockbreak(BreakEvent event) {
		if (event.getPos() != null) {
			if (grapplemod.controllerpos.containsKey(event.getPos())) {
				grappleController control = grapplemod.controllerpos.get(event.getPos());

				control.unattach();
				
				grapplemod.controllerpos.remove(event.getPos());
			}
		}
    }

	@SubscribeEvent
	public void onPlayerLoggedOutEvent(LoggedOutEvent e) {
		GrappleConfig.setserveroptions(null);
	}

	@SubscribeEvent
	public void onKeyInputEvent(KeyInputEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (!Minecraft.getInstance().isRunning() || player == null) {
			return;
		}
		
		grappleController controller = null;
		if (grapplemod.controllers.containsKey(player.getId())) {
			controller = grapplemod.controllers.get(player.getId());
		}
		
		if (Minecraft.getInstance().options.keyJump.isDown()) {
			if (controller != null) {
				if (controller instanceof airfrictionController && ((airfrictionController) controller).was_sliding) {
					controller.slidingJump();
				}
			}
		}	

		this.clientcontrollermanager.checkslide(Minecraft.getInstance().player);
	}
	
	@SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (!Minecraft.getInstance().isRunning() || player == null) {
			return;
		}
		
		int id = player.getId();
		if (grapplemod.controllers.containsKey(id)) {
			MovementInput input = event.getMovementInput();
			grappleController control = grapplemod.controllers.get(id);
			control.receivePlayerMovementMessage(input.leftImpulse, input.forwardImpulse, input.jumping, input.shiftKeyDown);
			
			boolean overrideMovement = true;
			if (Minecraft.getInstance().player.isOnGround()) {
				if (!(control instanceof airfrictionController) && !(control instanceof repelController)) {
					overrideMovement = false;
				}
			}
			
			if (overrideMovement) {
				input.jumping = false;
				input.down = false;
				input.up = false;
				input.left = false;
				input.right = false;
				input.forwardImpulse = 0;
				input.leftImpulse = 0;
//				input.sneak = false; // fix alternate throw angles
			}
		}
	}
	
	public float currentCameraTilt = 0;

	@SubscribeEvent
	public void CameraSetup(CameraSetup event) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (!Minecraft.getInstance().isRunning() || player == null) {
			return;
		}

		int id = player.getId();
		int targetCameraTilt = 0;
		if (grapplemod.controllers.containsKey(id)) {
			grappleController controller = grapplemod.controllers.get(id);
			if (controller instanceof airfrictionController) {
				airfrictionController afcontroller = (airfrictionController) controller;
				if (afcontroller.was_wallrunning) {
					vec walldirection = afcontroller.getwalldirection();
					if (walldirection != null) {
						vec lookdirection = vec.lookvec(player);
						int dir = lookdirection.cross(walldirection).y > 0 ? 1 : -1;
						targetCameraTilt = dir;
					}
				}
			}
		}
		
		if (currentCameraTilt != targetCameraTilt) {
			float cameraDiff = targetCameraTilt - currentCameraTilt;
			if (cameraDiff != 0) {
				float anim_s = GrappleConfig.getclientconf().camera.wallrun_camera_animation_s;
				float speed = (anim_s == 0) ? 9999 :  1.0f / (anim_s * 20.0f);
				if (speed > Math.abs(cameraDiff)) {
					currentCameraTilt = targetCameraTilt;
				} else {
					currentCameraTilt += speed * (cameraDiff > 0 ? 1 : -1);
				}
			}
		}
		
		if (currentCameraTilt != 0) {
		    event.setRoll(event.getRoll() + currentCameraTilt*GrappleConfig.getclientconf().camera.wallrun_camera_tilt_degrees);
		}
	}

	public ItemStack getKeypressStack(PlayerEntity player) {
		if (player != null) {
           ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
           if (stack != null) {
               Item item = stack.getItem();
               if (item instanceof KeypressItem) {
            	   return stack;
               }
           }
           
           stack = player.getItemInHand(Hand.OFF_HAND);
           if (stack != null) {
        	   Item item = stack.getItem();
        	   if (item instanceof KeypressItem) {
        		   return stack;
        	   }
           }
		}
		return null;
	}
	
	public boolean isLookingAtModifierBlock(PlayerEntity player) {
		RayTraceResult raytraceresult = Minecraft.getInstance().hitResult;
		if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
			BlockRayTraceResult bray = (BlockRayTraceResult) raytraceresult;
			BlockPos pos = bray.getBlockPos();
			BlockState state = player.level.getBlockState(pos);
			
			return (state.getBlock() == grapplemod.blockGrappleModifier);
		}
		return false;
	}
	
	
}
