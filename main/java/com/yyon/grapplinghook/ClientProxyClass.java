package com.yyon.grapplinghook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import com.yyon.grapplinghook.controllers.airfrictionController;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.controllers.repelController;
import com.yyon.grapplinghook.entities.RenderGrappleArrow;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.KeypressItem;
import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.items.launcherItem;
import com.yyon.grapplinghook.network.BaseMessageClient;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.Hand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
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
import net.minecraftforge.fml.network.NetworkEvent.Context;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxyClass implements CommonProxyClass {
	public boolean prevkeys[] = {false, false, false, false, false};
	
	public HashMap<Integer, Long> enderlaunchtimer = new HashMap<Integer, Long>();
	
	public double rocketFuel = 1.0;
	public double rocketIncreaseTick = 0.0;
	public double rocketDecreaseTick = 0.0;
	
	public static long prev_rope_jump_time = 0;
	
	public ModelResourceLocation grapplinghookloc = new ModelResourceLocation("grapplemod:grapplinghook", "inventory");
	public ModelResourceLocation hookshotloc = new ModelResourceLocation("grapplemod:hookshot", "inventory");
	public ModelResourceLocation smarthookloc = new ModelResourceLocation("grapplemod:smarthook", "inventory");
	public ModelResourceLocation smarthookropeloc = new ModelResourceLocation("grapplemod:smarthookrope", "inventory");
	public ModelResourceLocation enderhookloc = new ModelResourceLocation("grapplemod:enderhook", "inventory");
	public ModelResourceLocation magnetbowloc = new ModelResourceLocation("grapplemod:magnetbow", "inventory");
	public ModelResourceLocation ropeloc = new ModelResourceLocation("grapplemod:rope", "inventory");
	public ModelResourceLocation hookshotropeloc = new ModelResourceLocation("grapplemod:hookshotrope", "inventory");
	public ModelResourceLocation repellerloc = new ModelResourceLocation("grapplemod:repeller", "inventory");
	public ModelResourceLocation repelleronloc = new ModelResourceLocation("grapplemod:repelleron", "inventory");
	public ModelResourceLocation multihookloc = new ModelResourceLocation("grapplemod:multihook", "inventory");
	public ModelResourceLocation multihookropeloc = new ModelResourceLocation("grapplemod:multihookrope", "inventory");
	public ModelResourceLocation odmloc = new ModelResourceLocation("grapplemod:odm", "inventory");
	public ModelResourceLocation odmropeloc = new ModelResourceLocation("grapplemod:odmrope", "inventory");
	public ModelResourceLocation rocketloc = new ModelResourceLocation("grapplemod:rocket", "inventory");
	public ModelResourceLocation rocketropeloc = new ModelResourceLocation("grapplemod:rocketrope", "inventory");
	
	public ResourceLocation doubleJumpSoundLoc = new ResourceLocation("grapplemod", "doublejump");
	public ResourceLocation slideSoundLoc = new ResourceLocation("grapplemod", "slide");

	public ClientProxyClass() {
		crosshairrenderer = new crosshairRenderer();
	}
	
	@SubscribeEvent
	public static void clientSetup(final FMLClientSetupEvent event) {
		// register all the key bindings
		for (int i = 0; i < keyBindings.size(); ++i) 
		{
		    ClientRegistry.registerKeyBinding(keyBindings.get(i));
		}
		
	    RenderingRegistry.registerEntityRenderingHandler(grapplemod.grappleArrowType, new grappleArrowRenderFactory());

	    MinecraftForge.EVENT_BUS.register(grapplemod.proxy);

	    event.enqueueWork(grapplemod.proxy::registerPropertyOverride);
	    
	    GuiRegistry registry = AutoConfig.getGuiRegistry(GrappleConfig.class);

		ModLoadingContext.get().registerExtensionPoint(
                ExtensionPoint.CONFIGGUIFACTORY,
                () -> ((ClientProxyClass) grapplemod.proxy)::onConfigScreen);
}
	
	@Override
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
	}
	
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

	public crosshairRenderer crosshairrenderer;
	
	private static class grappleArrowRenderFactory implements IRenderFactory<grappleArrow> {
	    @Override
	    public EntityRenderer<? super grappleArrow> createRenderFor(EntityRendererManager manager) {
	      return new RenderGrappleArrow<>(manager, grapplemod.grapplebowitem);
	    	
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
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (player != null) {
			if (!Minecraft.getInstance().isPaused()) {
				if (this.iswallrunning(player, vec.motionvec(player))) {
					if (!grapplemod.controllers.containsKey(player.getId())) {
						grappleController controller = this.createControl(grapplemod.AIRID, -1, player.getId(), player.level, new vec(0,0,0), null, null);
						if (controller.getwalldirection() == null) {
							controller.unattach();
						}
					}
					
					if (grapplemod.controllers.containsKey(player.getId())) {
						tickssincelastonground = 0;
						alreadyuseddoublejump = false;
					}
				}
				
				this.checkdoublejump();
				
				this.checkslide(player);
				
				this.rocketFuel += this.rocketIncreaseTick;
				
				try {
					Collection<grappleController> controllers = grapplemod.controllers.values();
					for (grappleController controller : controllers) {
						controller.doClientTick();
					}
				} catch (ConcurrentModificationException e) {
					System.out.println("ConcurrentModificationException caught");
				}

				if (this.rocketFuel > 1) {this.rocketFuel = 1;}
				
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
				
				if (player.isOnGround()) {
					if (enderlaunchtimer.containsKey(player.getId())) {
						long timer = grapplemod.getTime(player.level) - enderlaunchtimer.get(player.getId());
						if (timer > 10) {
							this.resetlaunchertime(player.getId());
						}
					}
				}
			}
		}
	}
	
	private void checkslide(PlayerEntity player) {
		if (key_slide.isDown() && !grapplemod.controllers.containsKey(player.getId()) && this.issliding(player, vec.motionvec(player))) {
			this.createControl(grapplemod.AIRID, -1, player.getId(), player.level, new vec(0,0,0), null, null);
		}
	}

	@Override
	public void startrocket(PlayerEntity player, GrappleCustomization custom) {
		if (!custom.rocket) return;
		
		if (!grapplemod.controllers.containsKey(player.getId())) {
			this.createControl(grapplemod.AIRID, -1, player.getId(), player.level, new vec(0,0,0), null, custom);
		} else {
			grappleController controller = grapplemod.controllers.get(player.getId());
			if (controller.custom == null || !controller.custom.rocket) {
				if (controller.custom == null) {controller.custom = custom;}
				controller.custom.rocket = true;
				controller.custom.rocket_active_time = custom.rocket_active_time;
				controller.custom.rocket_force = custom.rocket_force;
				controller.custom.rocket_refuel_ratio = custom.rocket_refuel_ratio;
				this.updateRocketRegen(custom.rocket_active_time, custom.rocket_refuel_ratio);
			}
		}
	}

	@Override
	public void launchplayer(PlayerEntity player) {
		long prevtime;
		if (enderlaunchtimer.containsKey(player.getId())) {
			prevtime = enderlaunchtimer.get(player.getId());
		} else {
			prevtime = 0;
		}
		long timer = grapplemod.getTime(player.level) - prevtime;
		if (timer > GrappleConfig.getconf().ender_staff_recharge) {
			if ((player.getItemInHand(Hand.MAIN_HAND)!=null && (player.getItemInHand(Hand.MAIN_HAND).getItem() instanceof launcherItem || player.getItemInHand(Hand.MAIN_HAND).getItem() instanceof grappleBow)) || (player.getItemInHand(Hand.OFF_HAND)!=null && (player.getItemInHand(Hand.OFF_HAND).getItem() instanceof launcherItem || player.getItemInHand(Hand.OFF_HAND).getItem() instanceof grappleBow))) {
				enderlaunchtimer.put(player.getId(), grapplemod.getTime(player.level));
				
	        	vec facing = vec.lookvec(player);
	        	
	        	GrappleCustomization custom = null;
	        	if (player.getItemInHand(Hand.MAIN_HAND).getItem() instanceof grappleBow) {
	        		custom = ((grappleBow) player.getItemInHand(Hand.MAIN_HAND).getItem()).getCustomization(player.getItemInHand(Hand.MAIN_HAND));
	        	} else if (player.getItemInHand(Hand.OFF_HAND).getItem() instanceof grappleBow) {
	        		custom = ((grappleBow) player.getItemInHand(Hand.OFF_HAND).getItem()).getCustomization(player.getItemInHand(Hand.OFF_HAND));
	        	}
	        	
				if (!grapplemod.controllers.containsKey(player.getId())) {
					player.setOnGround(false);
					this.createControl(grapplemod.AIRID, -1, player.getId(), player.level, new vec(0,0,0), null, custom);
				}
				facing.mult_ip(GrappleConfig.getconf().ender_staff_strength);
				grapplemod.receiveEnderLaunch(player.getId(), facing.x, facing.y, facing.z);
			}
		}
	}
	
	@Override
	public void resetlaunchertime(int playerid) {
		if (enderlaunchtimer.containsKey(playerid)) {
			enderlaunchtimer.put(playerid, (long) 0);
		}
	}
	
	@Override
	public boolean isSneaking(Entity entity) {
		if (entity == Minecraft.getInstance().player) {
			return (Minecraft.getInstance().options.keyShift.isDown());
		} else {
			return entity.isCrouching();
		}
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
	
	public String getkeyname(grapplemod.keys keyenum) {
		KeyBinding binding = null;
		
		GameSettings gs = Minecraft.getInstance().options;
		
		if (keyenum == grapplemod.keys.keyBindAttack) {
			binding = gs.keyAttack;
		} else if (keyenum == grapplemod.keys.keyBindBack) {
			binding = gs.keyDown;
		} else if (keyenum == grapplemod.keys.keyBindForward) {
			binding = gs.keyUp;
		} else if (keyenum == grapplemod.keys.keyBindJump) {
			binding = gs.keyJump;
		} else if (keyenum == grapplemod.keys.keyBindLeft) {
			binding = gs.keyLeft;
		} else if (keyenum == grapplemod.keys.keyBindRight) {
			binding = gs.keyRight;
		} else if (keyenum == grapplemod.keys.keyBindSneak) {
			binding = gs.keyShift;
		} else if (keyenum == grapplemod.keys.keyBindUseItem) {
			binding = gs.keyUse;
		}
		
		if (binding == null) {
			return "";
		}
		
		String displayname = binding.getTranslatedKeyMessage().getString();
		if (displayname.equals("Button 1")) {
			return "Left Click";
		} else if (displayname.equals("Button 2")) {
			return "Right Click";
		} else {
			return displayname;
		}
	}

	/*
	public static boolean isactive(ItemStack stack) {
		PlayerEntity p = Minecraft.getMinecraft().player;
//		if (p.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) == stack || p.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND) == stack) {
			int entityid = p.getId();
			if (grapplemod.controllers.containsKey(entityid)) {
				Item item = stack.getItem();
				grappleController controller = grapplemod.controllers.get(entityid);
				if (item instanceof grappleBow && controller.controllerid == grapplemod.GRAPPLEID) {
					return true;
				} else if (item.getClass() == repeller.class && controller.controllerid == grapplemod.REPELID) {
					return true;
				}
			}
//		}
		return false;
	}
	*/
	
	@Override
	public void openModifierScreen(TileEntityGrappleModifier tileent) {
		Minecraft.getInstance().setScreen(new GuiModifier(tileent));

	}
	
	@SubscribeEvent
	public void onPlayerLoggedOutEvent(LoggedOutEvent e) {
		GrappleConfig.setserveroptions(null);
	}
	
	public String localize(String string) {
		return I18n.get(string);
	}

	@Override
	public void onMessageReceivedClient(BaseMessageClient msg, Context ctx) {
		msg.processMessage(ctx);
	}

	@Override
	public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio) {
		this.rocketDecreaseTick = 0.05 / 2.0 / rocket_active_time;
		this.rocketIncreaseTick = 0.05 / 2.0 / rocket_active_time / rocket_refuel_ratio;
	}
	
	@Override
	public double getRocketFunctioning() {
		this.rocketFuel -= this.rocketIncreaseTick;
		this.rocketFuel -= this.rocketDecreaseTick;
		if (this.rocketFuel >= 0) {
			return 1;
		} else {
			this.rocketFuel = 0;
			return this.rocketIncreaseTick / this.rocketDecreaseTick / 2.0;
		}
	}
	
	@Override
	public boolean iswallrunning(Entity entity, vec motion) {
		if (entity.horizontalCollision && !entity.isOnGround() && !entity.isCrouching()) {
			for (ItemStack stack : entity.getArmorSlots()) {
				if (stack != null) {
					Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
					if (enchantments.containsKey(grapplemod.wallrunenchantment)) {
						if (enchantments.get(grapplemod.wallrunenchantment) >= 1) {
							if (!key_jumpanddetach.isDown() && !Minecraft.getInstance().options.keyJump.isDown()) {
								BlockRayTraceResult raytraceresult = grapplemod.rayTraceBlocks(entity.level, vec.positionvec(entity), vec.positionvec(entity).add(new vec(0, -1, 0)));
								if (raytraceresult == null) {
									double current_speed = Math.sqrt(Math.pow(motion.x, 2) + Math.pow(motion.z,  2));
									if (current_speed >= GrappleConfig.getconf().wallrun_min_speed) {
										return true;
									}
								}
							}
						}
						break;
					}
				}
			}
		}
		return false;
	}

	boolean prevjumpbutton = false;
	int tickssincelastonground = 0;
	boolean alreadyuseddoublejump = false;
	
	public void checkdoublejump() {
		PlayerEntity player = Minecraft.getInstance().player;
		
		if (player.isOnGround()) {
			tickssincelastonground = 0;
			alreadyuseddoublejump = false;
		} else {
			tickssincelastonground++;
		}
		
		boolean isjumpbuttondown = Minecraft.getInstance().options.keyJump.isDown();
		
		if (isjumpbuttondown && !prevjumpbutton && !player.isInWater()) {
			
			if (tickssincelastonground > 3) {
				if (!alreadyuseddoublejump) {
					if (wearingdoublejumpenchant(player)) {
						if (!grapplemod.controllers.containsKey(player.getId()) || grapplemod.controllers.get(player.getId()) instanceof airfrictionController) {
							if (!grapplemod.controllers.containsKey(player.getId())) {
								this.createControl(grapplemod.AIRID, -1, player.getId(), player.level, new vec(0,0,0), null, null);
							}
							grappleController controller = grapplemod.controllers.get(player.getId());
							if (controller instanceof airfrictionController) {
								alreadyuseddoublejump = true;
								controller.doublejump();
							}
							this.playDoubleJumpSound(controller.entity);
						}
					}
				}
			}
		}
		
		prevjumpbutton = isjumpbuttondown;
		
	}

	public boolean wearingdoublejumpenchant(Entity entity) {
		if (entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.flying) {
			return false;
		}
		
		for (ItemStack stack : entity.getArmorSlots()) {
			if (stack != null) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
				if (enchantments.containsKey(grapplemod.doublejumpenchantment)) {
					if (enchantments.get(grapplemod.doublejumpenchantment) >= 1) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean isWearingSlidingEnchant(Entity entity) {
		for (ItemStack stack : entity.getArmorSlots()) {
			if (stack != null) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
				if (enchantments.containsKey(grapplemod.slidingenchantment)) {
					if (enchantments.get(grapplemod.slidingenchantment) >= 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean issliding(Entity entity, vec motion) {
		if (entity.isInWater()) {return false;}
		
		if (entity.isOnGround() && key_slide.isDown()) {
			if (ClientProxyClass.isWearingSlidingEnchant(entity)) {
				boolean was_sliding = false;
				int id = entity.getId();
				if (grapplemod.controllers.containsKey(id)) {
					grappleController controller = grapplemod.controllers.get(id);
					if (controller instanceof airfrictionController) {
						airfrictionController afc = (airfrictionController) controller;
						if (afc.was_sliding) {
							was_sliding = true;
						}
					}
				}
				double speed = motion.removealong(new vec (0,1,0)).length();
				if (speed > GrappleConfig.getconf().sliding_end_min_speed && (was_sliding || speed > GrappleConfig.getconf().sliding_min_speed)) {
					return true;
				}
			}
		}
		
		return false;
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

		this.checkslide(Minecraft.getInstance().player);
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
				float anim_s = GrappleConfig.getclientconf().wallrun_camera_animation_s;
				float speed = (anim_s == 0) ? 9999 :  1.0f / (anim_s * 20.0f);
				if (speed > Math.abs(cameraDiff)) {
					currentCameraTilt = targetCameraTilt;
				} else {
					currentCameraTilt += speed * (cameraDiff > 0 ? 1 : -1);
				}
			}
		}
		
		if (currentCameraTilt != 0) {
		    event.setRoll(event.getRoll() + currentCameraTilt*GrappleConfig.getclientconf().wallrun_camera_tilt_degrees);
		}
	}

	@Override
	public grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, BlockPos blockpos, GrappleCustomization custom) {
		grappleArrow arrow = null;
		Entity arrowentity = world.getEntity(arrowid);
		if (arrowentity != null && arrowentity instanceof grappleArrow) {
			arrow = (grappleArrow) arrowentity;
		}
		
		boolean multi = (custom != null) && (custom.doublehook);
		
		grappleController currentcontroller = grapplemod.controllers.get(entityid);
		if (currentcontroller != null && !(multi && currentcontroller.custom != null && currentcontroller.custom.doublehook)) {
			currentcontroller.unattach();
		}
		
//		System.out.println(blockpos);
		
		grappleController control = null;
		if (id == grapplemod.GRAPPLEID) {
			if (!multi) {
				control = new grappleController(arrowid, entityid, world, pos, id, custom);
			} else {
				control = grapplemod.controllers.get(entityid);
				boolean created = false;
				if (control != null && control.getClass().equals(grappleController.class)) {
					grappleController c = (grappleController) control;
					if (control.custom.doublehook) {
						if (arrow != null && arrow instanceof grappleArrow) {
							grappleArrow multiarrow = (grappleArrow) arrowentity;
							created = true;
							c.addArrow(multiarrow);
						}
					}
				}
				if (!created) {
					control = new grappleController(arrowid, entityid, world, pos, id, custom);
				}
			}
		} else if (id == grapplemod.REPELID) {
			control = new repelController(arrowid, entityid, world, pos, id);
		} else if (id == grapplemod.AIRID) {
			control = new airfrictionController(arrowid, entityid, world, pos, id, custom);
		} else {
			return null;
		}
		if (blockpos != null && control != null) {
			grapplemod.controllerpos.put(blockpos, control);
		}
		
		Entity e = world.getEntity(entityid);
		if (e != null && e instanceof ClientPlayerEntity) {
			ClientPlayerEntity p = (ClientPlayerEntity) e;
			control.receivePlayerMovementMessage(p.input.leftImpulse, p.input.forwardImpulse, p.input.jumping, p.input.shiftKeyDown);
		}
		
		return control;
	}

	public void playSlideSound(Entity entity) {
		entity.playSound(new SoundEvent(this.slideSoundLoc), GrappleConfig.getclientconf().slide_sound_volume, 1.0F);
	}

	private void playDoubleJumpSound(Entity entity) {
		entity.playSound(new SoundEvent(this.doubleJumpSoundLoc), GrappleConfig.getclientconf().doublejump_sound_volume * 0.7F, 1.0F);
	}

	@Override
	public void playWallrunJumpSound(Entity entity) {
		entity.playSound(new SoundEvent(this.doubleJumpSoundLoc), GrappleConfig.getclientconf().wallrunjump_sound_volume * 0.7F, 1.0F);
	}
	
	List<ItemStack> grapplinghookvariants = null;

	@Override
	public void fillGrappleVariants(ItemGroup tab, NonNullList<ItemStack> items) {
		if (Minecraft.getInstance().isRunning() == false || Minecraft.getInstance().player == null || Minecraft.getInstance().player.level == null || Minecraft.getInstance().player.level.getRecipeManager() == null) {
			return;
		}
		
		if (grapplinghookvariants == null) {
			grapplinghookvariants = new ArrayList<ItemStack>();
			RecipeManager recipemanager = Minecraft.getInstance().player.level.getRecipeManager();
			recipemanager.getRecipeIds().filter(loc -> loc.getNamespace().equals(grapplemod.MODID)).forEach(loc -> {
				ItemStack stack = recipemanager.byKey(loc).get().getResultItem();
				if (stack.getItem() instanceof grappleBow) {
					if (!grapplemod.grapplebowitem.getCustomization(stack).equals(new GrappleCustomization())) {
						grapplinghookvariants.add(stack);
					}
				}
			});
		}
		
		items.addAll(grapplinghookvariants);
	}
	
	public Screen onConfigScreen(Minecraft mc, Screen screen) {
		return AutoConfig.getConfigScreen(GrappleConfig.class, screen).get();
	}
}
