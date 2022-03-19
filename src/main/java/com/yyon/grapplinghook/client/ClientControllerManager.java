package com.yyon.grapplinghook.client;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.controllers.AirfrictionController;
import com.yyon.grapplinghook.controllers.ForcefieldController;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.items.EnderStaffItem;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.GrapplemodUtils;
import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

public class ClientControllerManager {
	public static ClientControllerManager instance;

	public static HashMap<Integer, GrappleController> controllers = new HashMap<Integer, GrappleController>();

	public ClientControllerManager() {
		instance = this;
	}
	
	public HashMap<Integer, Long> enderLaunchTimer = new HashMap<Integer, Long>();
	
	public double rocketFuel = 1.0;
	public double rocketIncreaseTick = 0.0;
	public double rocketDecreaseTick = 0.0;
	
	public int ticksWallRunning = 0;
	
	public void onClientTick(Player player) {
		if (player.isOnGround() || (controllers.containsKey(player.getId()) && controllers.get(player.getId()).controllerId == GrapplemodUtils.GRAPPLEID)) {
			ticksWallRunning = 0;
		}

		if (this.isWallRunning(player, Vec.motionVec(player))) {
			if (!controllers.containsKey(player.getId())) {
				GrappleController controller = this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.level, new Vec(0,0,0), null, null);
				if (controller.getWallDirection() == null) {
					controller.unattach();
				}
			}
			
			if (controllers.containsKey(player.getId())) {
				ticksSinceLastOnGround = 0;
				alreadyUsedDoubleJump = false;
			}
		}
		
		this.checkDoubleJump();
		
		this.checkSlide(player);
		
		this.rocketFuel += this.rocketIncreaseTick;
		
		try {
			for (GrappleController controller : controllers.values()) {
				controller.doClientTick();
			}
		} catch (ConcurrentModificationException e) {
			System.out.println("ConcurrentModificationException caught");
		}

		if (this.rocketFuel > 1) {this.rocketFuel = 1;}
		
		if (player.isOnGround()) {
			if (enderLaunchTimer.containsKey(player.getId())) {
				long timer = GrapplemodUtils.getTime(player.level) - enderLaunchTimer.get(player.getId());
				if (timer > 10) {
					this.resetLauncherTime(player.getId());
				}
			}
		}
	}

	public void checkSlide(Player player) {
		if (ClientSetup.key_slide.isDown() && !controllers.containsKey(player.getId()) && this.isSliding(player, Vec.motionVec(player))) {
			this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.level, new Vec(0,0,0), null, null);
		}
	}

	public void launchPlayer(Player player) {
		long prevtime;
		if (enderLaunchTimer.containsKey(player.getId())) {
			prevtime = enderLaunchTimer.get(player.getId());
		} else {
			prevtime = 0;
		}
		long timer = GrapplemodUtils.getTime(player.level) - prevtime;
		if (timer > GrappleConfig.getConf().enderstaff.ender_staff_recharge) {
			if ((player.getItemInHand(InteractionHand.MAIN_HAND)!=null && (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EnderStaffItem || player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof GrapplehookItem)) || (player.getItemInHand(InteractionHand.OFF_HAND)!=null && (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof EnderStaffItem || player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof GrapplehookItem))) {
				enderLaunchTimer.put(player.getId(), GrapplemodUtils.getTime(player.level));
				
	        	Vec facing = Vec.lookVec(player);
	        	
	        	GrappleCustomization custom = null;
	        	if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof GrapplehookItem) {
	        		custom = ((GrapplehookItem) player.getItemInHand(InteractionHand.MAIN_HAND).getItem()).getCustomization(player.getItemInHand(InteractionHand.MAIN_HAND));
	        	} else if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof GrapplehookItem) {
	        		custom = ((GrapplehookItem) player.getItemInHand(InteractionHand.OFF_HAND).getItem()).getCustomization(player.getItemInHand(InteractionHand.OFF_HAND));
	        	}
	        	
				if (!controllers.containsKey(player.getId())) {
					player.setOnGround(false);
					this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.level, new Vec(0,0,0), null, custom);
				}
				facing.mult_ip(GrappleConfig.getConf().enderstaff.ender_staff_strength);
				receiveEnderLaunch(player.getId(), facing.x, facing.y, facing.z);
//				player.playSound(new SoundEvent(new ResourceLocation("grapplemod", "enderstaff")), GrappleConfig.getClientConf().sounds.enderstaff_sound_volume * 0.5F, 1.0F);
//				Minecraft.getInstance().getSoundManager().play(new SimpleSound(new ResourceLocation("grapplemod", "enderstaff"), SoundCategory.PLAYERS, GrappleConfig.getClientConf().sounds.enderstaff_sound_volume * 0.5F, 1.0F, false, 0, ISound.AttenuationType.NONE, player.getX(), player.getY(), player.getZ(), false));
				ClientProxyInterface.proxy.playSound(new ResourceLocation("grapplemod", "enderstaff"), GrappleConfig.getClientConf().sounds.enderstaff_sound_volume * 0.5F);
			}
		}
	}
	
	public void resetLauncherTime(int playerid) {
		if (enderLaunchTimer.containsKey(playerid)) {
			enderLaunchTimer.put(playerid, (long) 0);
		}
	}

	public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio) {
		this.rocketDecreaseTick = 0.05 / 2.0 / rocket_active_time;
		this.rocketIncreaseTick = 0.05 / 2.0 / rocket_active_time / rocket_refuel_ratio;
	}
	

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
	
	public boolean isWallRunning(Entity entity, Vec motion) {
		if (entity.horizontalCollision && !entity.isOnGround() && !entity.isCrouching()) {
			if (entity instanceof LivingEntity && ((LivingEntity) entity).onClimbable()) {
				return false;
			}
			for (ItemStack stack : entity.getArmorSlots()) {
				if (stack != null) {
					Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
					if (enchantments.containsKey(CommonSetup.wallrunEnchantment)) {
						if (enchantments.get(CommonSetup.wallrunEnchantment) >= 1) {
							if (!ClientSetup.key_jumpanddetach.isDown() && !Minecraft.getInstance().options.keyJump.isDown()) {
								BlockHitResult raytraceresult = GrapplemodUtils.rayTraceBlocks(entity.level, Vec.positionVec(entity), Vec.positionVec(entity).add(new Vec(0, -1, 0)));
								if (raytraceresult == null) {
									double current_speed = Math.sqrt(Math.pow(motion.x, 2) + Math.pow(motion.z,  2));
									if (current_speed >= GrappleConfig.getConf().enchantments.wallrun.wallrun_min_speed) {
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

	boolean prevJumpButton = false;
	int ticksSinceLastOnGround = 0;
	boolean alreadyUsedDoubleJump = false;
	
	public void checkDoubleJump() {
		Player player = Minecraft.getInstance().player;
		
		if (player.isOnGround()) {
			ticksSinceLastOnGround = 0;
			alreadyUsedDoubleJump = false;
		} else {
			ticksSinceLastOnGround++;
		}
		
		boolean isjumpbuttondown = Minecraft.getInstance().options.keyJump.isDown();
		
		if (isjumpbuttondown && !prevJumpButton && !player.isInWater() && !player.isInLava()) {
			
			if (ticksSinceLastOnGround > 3) {
				if (!alreadyUsedDoubleJump) {
					if (wearingDoubleJumpEnchant(player)) {
						if (!controllers.containsKey(player.getId()) || controllers.get(player.getId()) instanceof AirfrictionController) {
							if (!controllers.containsKey(player.getId())) {
								this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.level, new Vec(0,0,0), null, null);
							}
							GrappleController controller = controllers.get(player.getId());
							if (controller instanceof AirfrictionController) {
								alreadyUsedDoubleJump = true;
								controller.doubleJump();
							}
							ClientProxyInterface.proxy.playDoubleJumpSound(controller.entity);
						}
					}
				}
			}
		}
		
		prevJumpButton = isjumpbuttondown;
		
	}

	public boolean wearingDoubleJumpEnchant(Entity entity) {
		if (entity instanceof Player && ((Player) entity).getAbilities().flying) {
			return false;
		}
		
		for (ItemStack stack : entity.getArmorSlots()) {
			if (stack != null) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
				if (enchantments.containsKey(CommonSetup.doubleJumpEnchantment)) {
					if (enchantments.get(CommonSetup.doubleJumpEnchantment) >= 1) {
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
				if (enchantments.containsKey(CommonSetup.slidingEnchantment)) {
					if (enchantments.get(CommonSetup.slidingEnchantment) >= 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isSliding(Entity entity, Vec motion) {
		if (entity.isInWater() || entity.isInLava()) {return false;}
		
		if (entity.isOnGround() && ClientSetup.key_slide.isDown()) {
			if (isWearingSlidingEnchant(entity)) {
				boolean was_sliding = false;
				int id = entity.getId();
				if (controllers.containsKey(id)) {
					GrappleController controller = controllers.get(id);
					if (controller instanceof AirfrictionController) {
						AirfrictionController afc = (AirfrictionController) controller;
						if (afc.wasSliding) {
							was_sliding = true;
						}
					}
				}
				double speed = motion.removeAlong(new Vec (0,1,0)).length();
				if (speed > GrappleConfig.getConf().enchantments.slide.sliding_end_min_speed && (was_sliding || speed > GrappleConfig.getConf().enchantments.slide.sliding_min_speed)) {
					return true;
				}
			}
		}
		
		return false;
	}


	public GrappleController createControl(int controllerId, int grapplehookEntityId, int playerId, Level world, Vec pos, BlockPos blockPos, GrappleCustomization custom) {
		GrapplehookEntity grapplehookEntity = null;
		Entity grapplehookEntityUncast = world.getEntity(grapplehookEntityId);
		if (grapplehookEntityUncast != null && grapplehookEntityUncast instanceof GrapplehookEntity) {
			grapplehookEntity = (GrapplehookEntity) grapplehookEntityUncast;
		}
		
		boolean multi = (custom != null) && (custom.doublehook);
		
		GrappleController currentcontroller = controllers.get(playerId);
		if (currentcontroller != null && !(multi && currentcontroller.custom != null && currentcontroller.custom.doublehook)) {
			currentcontroller.unattach();
		}
		
//		System.out.println(blockpos);
		
		GrappleController control = null;
		if (controllerId == GrapplemodUtils.GRAPPLEID) {
			if (!multi) {
				control = new GrappleController(grapplehookEntityId, playerId, world, pos, controllerId, custom);
			} else {
				control = controllers.get(playerId);
				boolean created = false;
				if (control != null && control.getClass().equals(GrappleController.class)) {
					GrappleController c = (GrappleController) control;
					if (control.custom.doublehook) {
						if (grapplehookEntity != null && grapplehookEntity instanceof GrapplehookEntity) {
							GrapplehookEntity multiHookEntity = grapplehookEntity;
							created = true;
							c.addHookEntity(multiHookEntity);
							return control;
						}
					}
				}
				if (!created) {
					control = new GrappleController(grapplehookEntityId, playerId, world, pos, controllerId, custom);
				}
			}
		} else if (controllerId == GrapplemodUtils.REPELID) {
			control = new ForcefieldController(grapplehookEntityId, playerId, world, pos, controllerId);
		} else if (controllerId == GrapplemodUtils.AIRID) {
			control = new AirfrictionController(grapplehookEntityId, playerId, world, pos, controllerId, custom);
		} else {
			return null;
		}
		
		if (control == null) {
			return null;
		}
		
		if (blockPos != null) {
			ClientControllerManager.controllerPos.put(blockPos, control);
		}

		registerController(playerId, control);
		
		Entity e = world.getEntity(playerId);
		if (e != null && e instanceof LocalPlayer) {
			LocalPlayer p = (LocalPlayer) e;
			control.receivePlayerMovementMessage(p.input.leftImpulse, p.input.forwardImpulse, p.input.jumping, p.input.shiftKeyDown);
		}
		
		return control;
	}

	public static void registerController(int entityId, GrappleController controller) {
		if (controllers.containsKey(entityId)) {
			controllers.get(entityId).unattach();
		}
		
		controllers.put(entityId, controller);
	}

	public static GrappleController unregisterController(int entityId) {
		if (controllers.containsKey(entityId)) {
			GrappleController controller = controllers.get(entityId);
			controllers.remove(entityId);
			
			BlockPos pos = null;
			for (BlockPos blockpos : controllerPos.keySet()) {
				GrappleController otherController = controllerPos.get(blockpos);
				if (otherController == controller) {
					pos = blockpos;
				}
			}
			if (pos != null) {
				controllerPos.remove(pos);
			}
			return controller;
		}
		return null;
	}

	public static void receiveGrappleDetach(int id) {
		GrappleController controller = controllers.get(id);
		if (controller != null) {
			controller.receiveGrappleDetach();
		}
	}
	
	public static void receiveGrappleDetachHook(int id, int hookid) {
		GrappleController controller = controllers.get(id);
		if (controller != null) {
			controller.receiveGrappleDetachHook(hookid);
		}
	}

	public static void receiveEnderLaunch(int id, double x, double y, double z) {
		GrappleController controller = controllers.get(id);
		if (controller != null) {
			controller.receiveEnderLaunch(x, y, z);
		} else {
			System.out.println("Couldn't find controller");
		}
	}

	public static class RocketSound extends AbstractTickableSoundInstance {
		GrappleController controller;
		boolean stopping = false;
		public float changespeed;
		protected RocketSound(GrappleController controller, SoundEvent p_i46532_1_, SoundSource p_i46532_2_) {
			super(p_i46532_1_, p_i46532_2_);
			this.looping = true;
			this.controller = controller;
			controller.rocket_key = true;
			controller.rocket_on = 1.0F;
			this.changespeed = GrappleConfig.getClientConf().sounds.rocket_sound_volume * 0.5F * 0.2F;
			this.volume = this.changespeed;
			this.delay = 0;
			this.attenuation = SoundInstance.Attenuation.NONE;
			this.relative = false;
		}

		@Override
		public void tick() {
			if (!controller.rocket_key || !controller.attached) {
				this.stopping = true;
			}
			float targetvolume = (float) controller.rocket_on * GrappleConfig.getClientConf().sounds.rocket_sound_volume * 0.5F;
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
				this.stop();
			}
			
			this.x = controller.entity.getX();
			this.y = controller.entity.getY();
			this.z = controller.entity.getZ();
		}
	}

	public void startRocket(Player player, GrappleCustomization custom) {
		if (!custom.rocket) return;
		
		GrappleController controller;
		if (!controllers.containsKey(player.getId())) {
			controller = this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.level, new Vec(0,0,0), null, custom);
		} else {
			controller = controllers.get(player.getId());
			if (controller.custom == null || !controller.custom.rocket) {
				if (controller.custom == null) {controller.custom = custom;}
				controller.custom.rocket = true;
				controller.custom.rocket_active_time = custom.rocket_active_time;
				controller.custom.rocket_force = custom.rocket_force;
				controller.custom.rocket_refuel_ratio = custom.rocket_refuel_ratio;
				this.updateRocketRegen(custom.rocket_active_time, custom.rocket_refuel_ratio);
			}
		}
		
		RocketSound sound = new RocketSound(controller, new SoundEvent(new ResourceLocation("grapplemod", "rocket")), SoundSource.PLAYERS);
		Minecraft.getInstance().getSoundManager().play(sound);
	}

	public static HashMap<BlockPos, GrappleController> controllerPos = new HashMap<BlockPos, GrappleController>();
	
	public static long prevRopeJumpTime = 0;
}
