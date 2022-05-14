package com.yyon.grapplinghook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import com.yyon.grapplinghook.controllers.airfrictionController;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.controllers.repelController;
import com.yyon.grapplinghook.entities.RenderGrappleArrow;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.KeypressItem;
import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.items.launcherItem;
import com.yyon.grapplinghook.items.repeller;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class ClientProxyClass extends CommonProxyClass {
	public boolean prevkeys[] = {false, false, false, false, false};
	
	public HashMap<Integer, Long> enderlaunchtimer = new HashMap<Integer, Long>();
	
	public double rocketFuel = 1.0;
	public double rocketIncreaseTick = 0.0;
	public double rocketDecreaseTick = 0.0;
	
	public static long prev_rope_jump_time = 0;

	public int tickswallrunning = 0;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		RenderingRegistry.registerEntityRenderingHandler(grappleArrow.class, new IRenderFactory<grappleArrow>() {
			@Override
			public Render<? super grappleArrow> createRenderFor(
					RenderManager manager) {
				return new RenderGrappleArrow<grappleArrow>(manager, grapplemod.grapplebowitem, Minecraft.getMinecraft().getRenderItem());
			}
		});
		
	    ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation("grapplemod:block_grapple_modifier", "inventory");
	    final int DEFAULT_ITEM_SUBTYPE = 0;
		ModelLoader.setCustomModelResourceLocation(grapplemod.itemBlockGrappleModifier, DEFAULT_ITEM_SUBTYPE, itemModelResourceLocation);
	}
	
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
	public ModelResourceLocation hookloc = new ModelResourceLocation("grapplemod:hook", "inventory");
	
	public ResourceLocation doubleJumpSoundLoc = new ResourceLocation("grapplemod", "doublejump");
	public ResourceLocation slideSoundLoc = new ResourceLocation("grapplemod", "slide");
	
	private void setgrapplebowtextures(Item item, final ModelResourceLocation notinusetexture, final ModelResourceLocation inusetexture) {
		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
		    	if (ClientProxyClass.isactive(stack)) {
		    		return inusetexture;
		    	}
		    	return notinusetexture;
			}
		});
		ModelBakery.registerItemVariants(item, notinusetexture);
		ModelBakery.registerItemVariants(item, inusetexture);
	}
	
	private void registerItemModels() {
//		setgrapplebowtextures(grapplemod.grapplebowitem, grapplinghookloc, ropeloc);
		registerItemModel(grapplemod.launcheritem);
		registerItemModel(grapplemod.longfallboots);
		setgrapplebowtextures(grapplemod.repelleritem, repellerloc, repelleronloc);
		registerItemModel(grapplemod.baseupgradeitem);
		registerItemModel(grapplemod.doubleupgradeitem);
		registerItemModel(grapplemod.forcefieldupgradeitem);
		registerItemModel(grapplemod.magnetupgradeitem);
		registerItemModel(grapplemod.motorupgradeitem);
		registerItemModel(grapplemod.ropeupgradeitem);
		registerItemModel(grapplemod.staffupgradeitem);
		registerItemModel(grapplemod.swingupgradeitem);
		registerItemModel(grapplemod.throwupgradeitem);
		registerItemModel(grapplemod.limitsupgradeitem);
		registerItemModel(grapplemod.rocketupgradeitem);
		
		ItemMeshDefinition itemmeshdefinition = new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				boolean active = !ClientProxyClass.isactive(stack);
		    	if (stack.hasTagCompound()) {
		    		NBTTagCompound compound = stack.getTagCompound();
		    		if (compound.hasKey("hook")) {
		    			return hookloc;
		    		}
		    		if (compound.getBoolean("rocket")) {
		    			if (compound.getBoolean("doublehook")) {
		    				return active ? odmloc : odmropeloc;
		    			} else {
		    				return active ? rocketloc : rocketropeloc;
		    			}
		    		}
		    		if (compound.getBoolean("motor")) {
		    			if (compound.getBoolean("doublehook")) {
		    				return active ? multihookloc : multihookropeloc;
		    			}
		    			if (compound.getBoolean("smartmotor")) {
		    				return active ? smarthookloc : smarthookropeloc;
		    			}
		    			return active ? hookshotloc : hookshotropeloc;
		    		}
		    		if (compound.getBoolean("enderstaff")) {
		    			return active ? enderhookloc : ropeloc;
		    		}
		    		if (compound.getBoolean("repel") || compound.getBoolean("attract")) {
		    			return active ? magnetbowloc : ropeloc;
		    		}
		    	}

		    	return active ? grapplinghookloc : ropeloc;
			}
		};
		
		ModelLoader.setCustomMeshDefinition(grapplemod.grapplebowitem, itemmeshdefinition);
		ModelLoader.setCustomMeshDefinition(grapplemod.motorhookitem, itemmeshdefinition);
		ModelLoader.setCustomMeshDefinition(grapplemod.smarthookitem, itemmeshdefinition);
		ModelLoader.setCustomMeshDefinition(grapplemod.doublemotorhookitem, itemmeshdefinition);
		ModelLoader.setCustomMeshDefinition(grapplemod.rocketdoublemotorhookitem, itemmeshdefinition);
		ModelLoader.setCustomMeshDefinition(grapplemod.enderhookitem, itemmeshdefinition);
		ModelLoader.setCustomMeshDefinition(grapplemod.magnethookitem, itemmeshdefinition);
		ModelLoader.setCustomMeshDefinition(grapplemod.rockethookitem, itemmeshdefinition);
		for (ResourceLocation loc : new ResourceLocation[] {multihookloc, multihookropeloc, smarthookloc, smarthookropeloc, hookshotloc, hookshotropeloc, enderhookloc, magnetbowloc, grapplinghookloc, ropeloc, odmloc, odmropeloc, rocketloc, rocketropeloc, hookloc}) {
			ModelBakery.registerItemVariants(grapplemod.grapplebowitem, loc);
			ModelBakery.registerItemVariants(grapplemod.motorhookitem, loc);
			ModelBakery.registerItemVariants(grapplemod.smarthookitem, loc);
			ModelBakery.registerItemVariants(grapplemod.doublemotorhookitem, loc);
			ModelBakery.registerItemVariants(grapplemod.rocketdoublemotorhookitem, loc);
			ModelBakery.registerItemVariants(grapplemod.enderhookitem, loc);
			ModelBakery.registerItemVariants(grapplemod.magnethookitem, loc);
			ModelBakery.registerItemVariants(grapplemod.rockethookitem, loc);
		}
	}

	@SubscribeEvent
	public void registerAllModels(final ModelRegistryEvent event) {
		System.out.println("REGISTERING ALL MODELS!!!!!!!!!!!!!");
		this.registerItemModels();
	}
	
	private void registerItemModel(Item item) {
		registerItemModel(item, item.getRegistryName().toString());
	}

	private void registerItemModel(Item item, String modelLocation) {
		final ModelResourceLocation fullModelLocation = new ModelResourceLocation(modelLocation, "inventory");
//		ModelBakery.registerItemVariants(item, fullModelLocation); // Ensure the custom model is loaded and prevent the default model from being loaded
		ModelLoader.setCustomModelResourceLocation(item, 0, fullModelLocation);
	}
	
	
	public static ArrayList<KeyBinding> keyBindings = new ArrayList<KeyBinding>();
	
	public static KeyBinding createkeybinding(String desc, int key, String category) {
		KeyBinding k = new NonConflictingKeyBinding(desc, key, category);
		keyBindings.add(k);
		return k;
	}
	
	public static KeyBinding key_boththrow = createkeybinding("key.boththrow.desc", -99, "key.grapplemod.category");
	public static KeyBinding key_leftthrow = createkeybinding("key.leftthrow.desc", 0, "key.grapplemod.category");
	public static KeyBinding key_rightthrow = createkeybinding("key.rightthrow.desc", 0, "key.grapplemod.category");
	public static KeyBinding key_motoronoff = createkeybinding("key.motoronoff.desc", Keyboard.KEY_LSHIFT, "key.grapplemod.category");
	public static KeyBinding key_jumpanddetach = createkeybinding("key.jumpanddetach.desc", Keyboard.KEY_SPACE, "key.grapplemod.category");
	public static KeyBinding key_slow = createkeybinding("key.slow.desc", Keyboard.KEY_LSHIFT, "key.grapplemod.category");
	public static KeyBinding key_climb = createkeybinding("key.climb.desc", Keyboard.KEY_LSHIFT, "key.grapplemod.category");
	public static KeyBinding key_climbup = createkeybinding("key.climbup.desc", 0, "key.grapplemod.category");
	public static KeyBinding key_climbdown = createkeybinding("key.climbdown.desc", 0, "key.grapplemod.category");
	public static KeyBinding key_enderlaunch = createkeybinding("key.enderlaunch.desc", -100, "key.grapplemod.category");
	public static KeyBinding key_rocket = createkeybinding("key.rocket.desc", -100, "key.grapplemod.category");
	public static KeyBinding key_slide = createkeybinding("key.slide.desc", Keyboard.KEY_LSHIFT, "key.grapplemod.category");


	@Override
	public void init(FMLInitializationEvent event, grapplemod grappleModInst) {
		super.init(event, grappleModInst);
//		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.grapplebowitem, 0, new ModelResourceLocation("grapplemod:grapplinghook", "inventory"));
//		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.hookshotitem, 0, new ModelResourceLocation("grapplemod:hookshot", "inventory"));
//		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.launcheritem, 0, new ModelResourceLocation("grapplemod:launcheritem", "inventory"));
//		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.longfallboots, 0, new ModelResourceLocation("grapplemod:longfallboots", "inventory"));
//		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.enderhookitem, 0, new ModelResourceLocation("grapplemod:enderhook", "inventory"));
		
		  
		// register all the key bindings
		for (int i = 0; i < keyBindings.size(); ++i) 
		{
		    ClientRegistry.registerKeyBinding(keyBindings.get(i));
		}
	}
	
	public crosshairRenderer crosshairrenderer;
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		
		crosshairrenderer = new crosshairRenderer();
	}
	
	public ItemStack getKeypressStack(EntityPlayer player) {
		if (player != null) {
           ItemStack stack = player.getHeldItemMainhand();
           if (stack != null) {
               Item item = stack.getItem();
               if (item instanceof KeypressItem) {
            	   return stack;
               }
           }
           
           stack = player.getHeldItemOffhand();
           if (stack != null) {
        	   Item item = stack.getItem();
        	   if (item instanceof KeypressItem) {
        		   return stack;
        	   }
           }
		}
		return null;
	}
	
	public boolean isLookingAtModifierBlock(EntityPlayer player) {
		RayTraceResult raytraceresult = Minecraft.getMinecraft().objectMouseOver;
		if (raytraceresult != null && raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos pos = raytraceresult.getBlockPos();
			IBlockState state = player.world.getBlockState(pos);
			return (state.getBlock() == grapplemod.blockGrappleModifier);
		}
		return false;
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player != null) {
			if (!Minecraft.getMinecraft().isGamePaused() || !Minecraft.getMinecraft().isSingleplayer()) {
//				System.out.println(vec.motionvec(player).removealong(new vec(0,1,0)).length() + " " + grapplemod.controllers.containsKey(player.getEntityId()));
				
				if (player.onGround || (grapplemod.controllers.containsKey(player.getEntityId()) && grapplemod.controllers.get(player.getEntityId()).controllerid == grapplemod.GRAPPLEID)) {
					tickswallrunning = 0;
				}
				
				if (this.iswallrunning(player, vec.motionvec(player))) {
					if (!grapplemod.controllers.containsKey(player.getEntityId())) {
						grappleController controller = this.createControl(grapplemod.AIRID, -1, player.getEntityId(), player.world, new vec(0,0,0), null, null);
						if (controller.getwalldirection() == null) {
							controller.unattach();
						}
					}
					
					if (grapplemod.controllers.containsKey(player.getEntityId())) {
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
				
				if (Minecraft.getMinecraft().currentScreen == null) {
					// keep in same order as enum from KeypressItem
					boolean keys[] = {key_enderlaunch.isKeyDown(), key_leftthrow.isKeyDown(), key_rightthrow.isKeyDown(), key_boththrow.isKeyDown(), key_rocket.isKeyDown()};
					
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
				
				if (player.onGround) {
					if (enderlaunchtimer.containsKey(player.getEntityId())) {
						long timer = player.world.getTotalWorldTime() - enderlaunchtimer.get(player.getEntityId());
						if (timer > 10) {
							this.resetlaunchertime(player.getEntityId());
						}
					}
				}
			}
		}
	}
	
	private void checkslide(EntityPlayer player) {
		if (key_slide.isKeyDown() && !grapplemod.controllers.containsKey(player.getEntityId()) && this.issliding(player, vec.motionvec(player))) {
			this.createControl(grapplemod.AIRID, -1, player.getEntityId(), player.world, new vec(0,0,0), null, null);
		}
	}

	public static class RocketSound extends MovingSound {
		grappleController controller;
		boolean stopping = false;
		public float changespeed;
		protected RocketSound(grappleController controller, SoundEvent p_i46532_1_, SoundCategory p_i46532_2_) {
			super(p_i46532_1_, p_i46532_2_);
			this.repeat = true;
			this.controller = controller;
			controller.rocket_key = true;
			controller.rocket_on = 1.0F;
			this.changespeed = GrappleConfig.client_options.rocket_sound_volume * 0.5F * 0.2F;
			this.volume = this.changespeed;
			this.repeatDelay = 0;
			this.attenuationType = ISound.AttenuationType.NONE;
		}

		@Override
		public void update() {
			if (!controller.rocket_key || !controller.attached) {
				this.stopping = true;
			}
			float targetvolume = (float) controller.rocket_on * GrappleConfig.client_options.rocket_sound_volume * 0.5F;
			if (this.stopping) {
				targetvolume = 0;
			}
			float diff = Math.abs(targetvolume - this.volume);
			if (diff > changespeed) {
				this.volume = this.volume + changespeed * (this.volume > targetvolume ? -1 : 1);
			} else {
				this.volume = targetvolume;
			}
			if (this.volume == 0 && this.stopping) {
				this.donePlaying = true;
			}
			this.xPosF = (float) controller.entity.posX;
			this.yPosF = (float) controller.entity.posY;
			this.zPosF = (float) controller.entity.posZ;
		}
	}

	@Override
	public void startrocket(EntityPlayer player, GrappleCustomization custom) {
		if (!custom.rocket) return;
		
		grappleController controller;
		if (!grapplemod.controllers.containsKey(player.getEntityId())) {
			controller = this.createControl(grapplemod.AIRID, -1, player.getEntityId(), player.world, new vec(0,0,0), null, custom);
		} else {
			controller = grapplemod.controllers.get(player.getEntityId());
			if (controller.custom == null || !controller.custom.rocket) {
				if (controller.custom == null) {controller.custom = custom;}
				controller.custom.rocket = true;
				controller.custom.rocket_active_time = custom.rocket_active_time;
				controller.custom.rocket_force = custom.rocket_force;
				controller.custom.rocket_refuel_ratio = custom.rocket_refuel_ratio;
				this.updateRocketRegen(custom.rocket_active_time, custom.rocket_refuel_ratio);
			}
		}

		RocketSound sound = new RocketSound(controller, new SoundEvent(new ResourceLocation("grapplemod", "rocket")), SoundCategory.PLAYERS);
		Minecraft.getMinecraft().getSoundHandler().playSound(sound);
	}

	
	@Override
	public void launchplayer(EntityPlayer player) {
		long prevtime;
		if (enderlaunchtimer.containsKey(player.getEntityId())) {
			prevtime = enderlaunchtimer.get(player.getEntityId());
		} else {
			prevtime = 0;
		}
		long timer = player.world.getTotalWorldTime() - prevtime;
		if (timer > GrappleConfig.getconf().ender_staff_recharge) {
			if ((player.getHeldItemMainhand()!=null && (player.getHeldItemMainhand().getItem() instanceof launcherItem || player.getHeldItemMainhand().getItem() instanceof grappleBow)) || (player.getHeldItemOffhand()!=null && (player.getHeldItemOffhand().getItem() instanceof launcherItem || player.getHeldItemOffhand().getItem() instanceof grappleBow))) {
				enderlaunchtimer.put(player.getEntityId(), player.world.getTotalWorldTime());
				
	        	vec facing = new vec(player.getLookVec());
//				vec playermotion = vec.motionvec(player);
//				vec newvec = playermotion.add(facing.mult(3));
				
				/*
				if (!grapplemod.controllers.containsKey(player.getEntityId())) {
					player.motionX = newvec.x;
					player.motionY = newvec.y;
					player.motionZ = newvec.z;
					
					if (player instanceof EntityPlayerMP) {
						((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
					} else {
						grapplemod.network.sendToServer(new PlayerMovementMessage(player.getEntityId(), player.posX, player.posY, player.posZ, player.motionX, player.motionY, player.motionZ));
					}
				} else {
					facing.mult_ip(3);
					grapplemod.receiveEnderLaunch(player.getEntityId(), facing.x, facing.y, facing.z);
				}
				*/
	        	
	        	GrappleCustomization custom = null;
	        	if (player.getHeldItemMainhand().getItem() instanceof grappleBow) {
	        		custom = ((grappleBow) player.getHeldItemMainhand().getItem()).getCustomization(player.getHeldItemMainhand());
	        	} else if (player.getHeldItemOffhand().getItem() instanceof grappleBow) {
	        		custom = ((grappleBow) player.getHeldItemOffhand().getItem()).getCustomization(player.getHeldItemOffhand());
	        	}
	        	
				if (!grapplemod.controllers.containsKey(player.getEntityId())) {
					player.onGround = false;
					this.createControl(grapplemod.AIRID, -1, player.getEntityId(), player.world, new vec(0,0,0), null, custom);
				}
				facing.mult_ip(GrappleConfig.getconf().ender_staff_strength);
				grapplemod.receiveEnderLaunch(player.getEntityId(), facing.x, facing.y, facing.z);
//				player.playSound(new SoundEvent(new ResourceLocation("grapplemod", "enderstaff")), GrappleConfig.client_options.enderstaff_sound_volume * 0.5F, 1.0F);
				this.playSound(new ResourceLocation("grapplemod", "enderstaff"), GrappleConfig.client_options.enderstaff_sound_volume * 0.5F);
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
		if (entity == Minecraft.getMinecraft().player) {
			return (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak));
		} else {
			return entity.isSneaking();
		}
	}
	
	@Override
    public void blockbreak(BreakEvent event) {
		if (event.getPos() != null) {
			if (grapplemod.controllerpos.containsKey(event.getPos())) {
				grappleController control = grapplemod.controllerpos.get(event.getPos());

				control.unattach();
				
				grapplemod.controllerpos.remove(event.getPos());
			}
		}
    }
	
	@Override
	public String getkeyname(CommonProxyClass.keys keyenum) {
		KeyBinding binding = null;
		
		GameSettings gs = Minecraft.getMinecraft().gameSettings;
		
		if (keyenum == keys.keyBindAttack) {
			binding = gs.keyBindAttack;
		} else if (keyenum == keys.keyBindBack) {
			binding = gs.keyBindBack;
		} else if (keyenum == keys.keyBindForward) {
			binding = gs.keyBindForward;
		} else if (keyenum == keys.keyBindJump) {
			binding = gs.keyBindJump;
		} else if (keyenum == keys.keyBindLeft) {
			binding = gs.keyBindLeft;
		} else if (keyenum == keys.keyBindRight) {
			binding = gs.keyBindRight;
		} else if (keyenum == keys.keyBindSneak) {
			binding = gs.keyBindSneak;
		} else if (keyenum == keys.keyBindUseItem) {
			binding = gs.keyBindUseItem;
		}
		
		if (binding == null) {
			return "";
		}
		
		String displayname = binding.getDisplayName();
		if (displayname.equals("Button 1")) {
			return "Left Click";
		} else if (displayname.equals("Button 2")) {
			return "Right Click";
		} else {
			return displayname;
		}
	}

	public static boolean isactive(ItemStack stack) {
		EntityPlayer p = Minecraft.getMinecraft().player;
//		if (p.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) == stack || p.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND) == stack) {
			int entityid = p.getEntityId();
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
	
	@Override
	public void openModifierScreen(TileEntityGrappleModifier tileent) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiModifier(tileent));

	}
	
	@SubscribeEvent
	public void onPlayerLoggedOutEvent(ClientDisconnectionFromServerEvent e) {
		System.out.println("deleting server options");
		GrappleConfig.setserveroptions(null);
	}


	@SubscribeEvent
	public void clientTick(ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
//			if (keyBindings[0].isKeyDown()) {
//				System.out.println("Down");
//			}
		}
	}

	@Override
	public String localize(String string) {
		return I18n.format(string);
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
		if (entity.collidedHorizontally && !entity.onGround && !entity.isSneaking()) {
			if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isOnLadder()) {
				return false;
			}
			for (ItemStack stack : entity.getArmorInventoryList()) {
				if (stack != null) {
					Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
					if (enchantments.containsKey(grapplemod.wallrunenchantment)) {
						if (enchantments.get(grapplemod.wallrunenchantment) >= 1) {
							if (!key_jumpanddetach.isKeyDown() && !Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()) {
								RayTraceResult raytraceresult = entity.world.rayTraceBlocks(entity.getPositionVector(), vec.positionvec(entity).add(new vec(0, -1, 0)).toVec3d(), false, true, false);
								if (raytraceresult == null || raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
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
		EntityPlayer player = Minecraft.getMinecraft().player;
		
		if (player.onGround) {
			tickssincelastonground = 0;
			alreadyuseddoublejump = false;
		} else {
			tickssincelastonground++;
		}
		
//		if (grapplemod.controllers.containsKey(player.getEntityId()) && !(grapplemod.controllers.get(player.getEntityId()) instanceof airfrictionController)) {
//			tickssincelastonground = 0;
//			alreadyuseddoublejump = false;
//		}
		
		boolean isjumpbuttondown = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
		
		if (isjumpbuttondown && !prevjumpbutton && !player.isInWater() && !player.isInLava()) {
			
			if (tickssincelastonground > 3) {
				if (!alreadyuseddoublejump) {
					if (wearingdoublejumpenchant(player)) {
						if (!grapplemod.controllers.containsKey(player.getEntityId())) {
							this.createControl(grapplemod.AIRID, -1, player.getEntityId(), player.world, new vec(0,0,0), null, null);
						}
						grappleController controller = grapplemod.controllers.get(player.getEntityId());
						if (controller instanceof airfrictionController) {
							alreadyuseddoublejump = true;
							controller.doublejump();
						}
						this.playDoubleJumpSound(controller.entity);
					}
				}
			}
		}
		
		prevjumpbutton = isjumpbuttondown;
		
	}

	public boolean wearingdoublejumpenchant(Entity entity) {
		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying) {
			return false;
		}
//		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
//			return false;
//		}
		
		for (ItemStack stack : entity.getArmorInventoryList()) {
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
		for (ItemStack stack : entity.getArmorInventoryList()) {
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
		if (entity.isInWater() || entity.isInLava()) {return false;}
		
		if (entity.onGround && key_slide.isKeyDown()) {
			if (ClientProxyClass.isWearingSlidingEnchant(entity)) {
//				System.out.println("check sliding: " + Double.toString(vec.motionvec(entity).removealong(new vec (0,1,0)).length()));
				boolean was_sliding = false;
				int id = entity.getEntityId();
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

	@SubscribeEvent(priority=EventPriority.LOW)
	public void onKeyInputEvent(KeyInputEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		
		grappleController controller = null;
		if (grapplemod.controllers.containsKey(player.getEntityId())) {
			controller = grapplemod.controllers.get(player.getEntityId());
		}
		
		if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()) {
			if (controller != null) {
				if (controller instanceof airfrictionController && ((airfrictionController) controller).was_sliding) {
					controller.slidingJump();
				}
			}
		}	

		this.checkslide(Minecraft.getMinecraft().player);
	}
	
	@SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
		int id = Minecraft.getMinecraft().player.getEntityId();
		if (grapplemod.controllers.containsKey(id)) {
			MovementInput input = event.getMovementInput();
			grappleController control = grapplemod.controllers.get(id);
			control.receivePlayerMovementMessage(input.moveStrafe, input.moveForward, input.jump, input.sneak);
			
			boolean overrideMovement = true;
			if (Minecraft.getMinecraft().player.onGround) {
				if (!(control instanceof airfrictionController) && !(control instanceof repelController)) {
					overrideMovement = false;
				}
			}
			
			if (overrideMovement) {
				input.jump = false;
				input.backKeyDown = false;
				input.forwardKeyDown = false;
				input.leftKeyDown = false;
				input.rightKeyDown = false;
				input.moveForward = 0;
				input.moveStrafe = 0;
//				input.sneak = false; // fix alternate throw angles
			}
		}
	}
	
	public float currentCameraTilt = 0;

	@SubscribeEvent
	public void CameraSetup(CameraSetup event)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		int id = player.getEntityId();
		int targetCameraTilt = 0;
		if (grapplemod.controllers.containsKey(id)) {
			grappleController controller = grapplemod.controllers.get(id);
			if (controller instanceof airfrictionController) {
				airfrictionController afcontroller = (airfrictionController) controller;
				if (afcontroller.was_wallrunning) {
					vec walldirection = afcontroller.getwalldirection();
					if (walldirection != null) {
						vec lookdirection = new vec(player.getLookVec());
						int dir = lookdirection.cross(walldirection).y > 0 ? 1 : -1;
						targetCameraTilt = dir;
					}
				}
			}
		}
		
		if (currentCameraTilt != targetCameraTilt) {
			float cameraDiff = targetCameraTilt - currentCameraTilt;
			if (cameraDiff != 0) {
				float anim_s = GrappleConfig.client_options.wallrun_camera_animation_s;
				float speed = (anim_s == 0) ? 9999 :  1.0f / (anim_s * 20.0f);
				if (speed > Math.abs(cameraDiff)) {
					currentCameraTilt = targetCameraTilt;
				} else {
					currentCameraTilt += speed * (cameraDiff > 0 ? 1 : -1);
				}
			}
		}
		
		if (currentCameraTilt != 0) {
		    event.setRoll(event.getRoll() + currentCameraTilt*GrappleConfig.client_options.wallrun_camera_tilt_degrees);
		}
	}

	@Override
	public grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, BlockPos blockpos, GrappleCustomization custom) {
		grappleArrow arrow = null;
		Entity arrowentity = world.getEntityByID(arrowid);
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
/*					System.out.println("Couldn't create");
					grapplemod.removesubarrow(arrowid);*/
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
		
		Entity e = world.getEntityByID(entityid);
		if (e != null && e instanceof EntityPlayerSP) {
			EntityPlayerSP p = (EntityPlayerSP) e;
			control.receivePlayerMovementMessage(p.movementInput.moveStrafe, p.movementInput.moveForward, p.movementInput.jump, p.movementInput.sneak);
		}
		
		return control;
	}

	public void playSlideSound(Entity entity) {
		this.playSound(this.slideSoundLoc, GrappleConfig.client_options.slide_sound_volume);
	}

	private void playDoubleJumpSound(Entity entity) {
		this.playSound(this.doubleJumpSoundLoc, GrappleConfig.client_options.doublejump_sound_volume * 0.7F);
	}

	@Override
	public void playWallrunJumpSound(Entity entity) {
		this.playSound(this.doubleJumpSoundLoc, GrappleConfig.client_options.wallrunjump_sound_volume * 0.7F);
	}

	
	@Override
	public void playSound(ResourceLocation loc, float volume) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(loc, SoundCategory.PLAYERS, volume, 1.0F, false, 0, ISound.AttenuationType.NONE, (float) player.posX, (float) player.posY, (float) player.posZ));
	}

	@Override
	public int getWallrunTicks() {
		return this.tickswallrunning;
	}
	
	@Override
	public void setWallrunTicks(int newWallrunTicks) {
		this.tickswallrunning = newWallrunTicks;
	}
}
