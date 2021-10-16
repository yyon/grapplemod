package com.yyon.grapplinghook.client;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.controllers.AirfrictionController;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.controllers.ForcefieldController;
import com.yyon.grapplinghook.entities.grapplearrow.GrapplehookEntity;
import com.yyon.grapplinghook.items.KeypressItem;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.items.EnderStaffItem;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.GrapplemodUtils;
import com.yyon.grapplinghook.utils.Vec;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class ClientControllerManager {
	public static ClientControllerManager instance;

	public static HashMap<Integer, GrappleController> controllers = new HashMap<Integer, GrappleController>();

	public ClientControllerManager() {
		instance = this;
	}
	
	public HashMap<Integer, Long> enderlaunchtimer = new HashMap<Integer, Long>();
	
	public double rocketFuel = 1.0;
	public double rocketIncreaseTick = 0.0;
	public double rocketDecreaseTick = 0.0;
	
	public void onClientTick(PlayerEntity player) {
		if (this.iswallrunning(player, Vec.motionvec(player))) {
			if (!this.controllers.containsKey(player.getId())) {
				GrappleController controller = this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.level, new Vec(0,0,0), null, null);
				if (controller.getwalldirection() == null) {
					controller.unattach();
				}
			}
			
			if (this.controllers.containsKey(player.getId())) {
				tickssincelastonground = 0;
				alreadyuseddoublejump = false;
			}
		}
		
		this.checkdoublejump();
		
		this.checkslide(player);
		
		this.rocketFuel += this.rocketIncreaseTick;
		
		try {
			Collection<GrappleController> controllers = this.controllers.values();
			for (GrappleController controller : controllers) {
				controller.doClientTick();
			}
		} catch (ConcurrentModificationException e) {
			System.out.println("ConcurrentModificationException caught");
		}

		if (this.rocketFuel > 1) {this.rocketFuel = 1;}
		
		if (player.isOnGround()) {
			if (enderlaunchtimer.containsKey(player.getId())) {
				long timer = GrapplemodUtils.getTime(player.level) - enderlaunchtimer.get(player.getId());
				if (timer > 10) {
					this.resetlaunchertime(player.getId());
				}
			}
		}
	}

	public void checkslide(PlayerEntity player) {
		if (ClientSetup.key_slide.isDown() && !this.controllers.containsKey(player.getId()) && this.issliding(player, Vec.motionvec(player))) {
			this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.level, new Vec(0,0,0), null, null);
		}
	}

	public void launchplayer(PlayerEntity player) {
		long prevtime;
		if (enderlaunchtimer.containsKey(player.getId())) {
			prevtime = enderlaunchtimer.get(player.getId());
		} else {
			prevtime = 0;
		}
		long timer = GrapplemodUtils.getTime(player.level) - prevtime;
		if (timer > GrappleConfig.getconf().enderstaff.ender_staff_recharge) {
			if ((player.getItemInHand(Hand.MAIN_HAND)!=null && (player.getItemInHand(Hand.MAIN_HAND).getItem() instanceof EnderStaffItem || player.getItemInHand(Hand.MAIN_HAND).getItem() instanceof GrapplehookItem)) || (player.getItemInHand(Hand.OFF_HAND)!=null && (player.getItemInHand(Hand.OFF_HAND).getItem() instanceof EnderStaffItem || player.getItemInHand(Hand.OFF_HAND).getItem() instanceof GrapplehookItem))) {
				enderlaunchtimer.put(player.getId(), GrapplemodUtils.getTime(player.level));
				
	        	Vec facing = Vec.lookvec(player);
	        	
	        	GrappleCustomization custom = null;
	        	if (player.getItemInHand(Hand.MAIN_HAND).getItem() instanceof GrapplehookItem) {
	        		custom = ((GrapplehookItem) player.getItemInHand(Hand.MAIN_HAND).getItem()).getCustomization(player.getItemInHand(Hand.MAIN_HAND));
	        	} else if (player.getItemInHand(Hand.OFF_HAND).getItem() instanceof GrapplehookItem) {
	        		custom = ((GrapplehookItem) player.getItemInHand(Hand.OFF_HAND).getItem()).getCustomization(player.getItemInHand(Hand.OFF_HAND));
	        	}
	        	
				if (!this.controllers.containsKey(player.getId())) {
					player.setOnGround(false);
					this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.level, new Vec(0,0,0), null, custom);
				}
				facing.mult_ip(GrappleConfig.getconf().enderstaff.ender_staff_strength);
				this.receiveEnderLaunch(player.getId(), facing.x, facing.y, facing.z);
			}
		}
	}
	
	public void resetlaunchertime(int playerid) {
		if (enderlaunchtimer.containsKey(playerid)) {
			enderlaunchtimer.put(playerid, (long) 0);
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
	
	public boolean iswallrunning(Entity entity, Vec motion) {
		if (entity.horizontalCollision && !entity.isOnGround() && !entity.isCrouching()) {
			for (ItemStack stack : entity.getArmorSlots()) {
				if (stack != null) {
					Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
					if (enchantments.containsKey(CommonSetup.wallrunenchantment)) {
						if (enchantments.get(CommonSetup.wallrunenchantment) >= 1) {
							if (!ClientSetup.key_jumpanddetach.isDown() && !Minecraft.getInstance().options.keyJump.isDown()) {
								BlockRayTraceResult raytraceresult = GrapplemodUtils.rayTraceBlocks(entity.level, Vec.positionvec(entity), Vec.positionvec(entity).add(new Vec(0, -1, 0)));
								if (raytraceresult == null) {
									double current_speed = Math.sqrt(Math.pow(motion.x, 2) + Math.pow(motion.z,  2));
									if (current_speed >= GrappleConfig.getconf().enchantments.wallrun.wallrun_min_speed) {
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
						if (!this.controllers.containsKey(player.getId()) || this.controllers.get(player.getId()) instanceof AirfrictionController) {
							if (!this.controllers.containsKey(player.getId())) {
								this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.level, new Vec(0,0,0), null, null);
							}
							GrappleController controller = this.controllers.get(player.getId());
							if (controller instanceof AirfrictionController) {
								alreadyuseddoublejump = true;
								controller.doublejump();
							}
							ClientProxyInterface.proxy.playDoubleJumpSound(controller.entity);
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
				if (enchantments.containsKey(CommonSetup.doublejumpenchantment)) {
					if (enchantments.get(CommonSetup.doublejumpenchantment) >= 1) {
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
				if (enchantments.containsKey(CommonSetup.slidingenchantment)) {
					if (enchantments.get(CommonSetup.slidingenchantment) >= 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean issliding(Entity entity, Vec motion) {
		if (entity.isInWater()) {return false;}
		
		if (entity.isOnGround() && ClientSetup.key_slide.isDown()) {
			if (isWearingSlidingEnchant(entity)) {
				boolean was_sliding = false;
				int id = entity.getId();
				if (this.controllers.containsKey(id)) {
					GrappleController controller = this.controllers.get(id);
					if (controller instanceof AirfrictionController) {
						AirfrictionController afc = (AirfrictionController) controller;
						if (afc.was_sliding) {
							was_sliding = true;
						}
					}
				}
				double speed = motion.removealong(new Vec (0,1,0)).length();
				if (speed > GrappleConfig.getconf().enchantments.slide.sliding_end_min_speed && (was_sliding || speed > GrappleConfig.getconf().enchantments.slide.sliding_min_speed)) {
					return true;
				}
			}
		}
		
		return false;
	}


	public GrappleController createControl(int id, int arrowid, int entityid, World world, Vec pos, BlockPos blockpos, GrappleCustomization custom) {
		GrapplehookEntity arrow = null;
		Entity arrowentity = world.getEntity(arrowid);
		if (arrowentity != null && arrowentity instanceof GrapplehookEntity) {
			arrow = (GrapplehookEntity) arrowentity;
		}
		
		boolean multi = (custom != null) && (custom.doublehook);
		
		GrappleController currentcontroller = this.controllers.get(entityid);
		if (currentcontroller != null && !(multi && currentcontroller.custom != null && currentcontroller.custom.doublehook)) {
			currentcontroller.unattach();
		}
		
//		System.out.println(blockpos);
		
		GrappleController control = null;
		if (id == GrapplemodUtils.GRAPPLEID) {
			if (!multi) {
				control = new GrappleController(arrowid, entityid, world, pos, id, custom);
			} else {
				control = this.controllers.get(entityid);
				boolean created = false;
				if (control != null && control.getClass().equals(GrappleController.class)) {
					GrappleController c = (GrappleController) control;
					if (control.custom.doublehook) {
						if (arrow != null && arrow instanceof GrapplehookEntity) {
							GrapplehookEntity multiarrow = (GrapplehookEntity) arrowentity;
							created = true;
							c.addArrow(multiarrow);
							return control;
						}
					}
				}
				if (!created) {
					control = new GrappleController(arrowid, entityid, world, pos, id, custom);
				}
			}
		} else if (id == GrapplemodUtils.REPELID) {
			control = new ForcefieldController(arrowid, entityid, world, pos, id);
		} else if (id == GrapplemodUtils.AIRID) {
			control = new AirfrictionController(arrowid, entityid, world, pos, id, custom);
		} else {
			return null;
		}
		
		if (control == null) {
			return null;
		}
		
		if (blockpos != null) {
			ClientControllerManager.controllerpos.put(blockpos, control);
		}

		registerController(entityid, control);
		
		Entity e = world.getEntity(entityid);
		if (e != null && e instanceof ClientPlayerEntity) {
			ClientPlayerEntity p = (ClientPlayerEntity) e;
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
			for (BlockPos blockpos : controllerpos.keySet()) {
				GrappleController otherController = controllerpos.get(blockpos);
				if (otherController == controller) {
					pos = blockpos;
				}
			}
			if (pos != null) {
				controllerpos.remove(pos);
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

	public void startrocket(PlayerEntity player, GrappleCustomization custom) {
		if (!custom.rocket) return;
		
		if (!controllers.containsKey(player.getId())) {
			this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.level, new Vec(0,0,0), null, custom);
		} else {
			GrappleController controller = controllers.get(player.getId());
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

	public static HashMap<BlockPos, GrappleController> controllerpos = new HashMap<BlockPos, GrappleController>();
	
	public static long prev_rope_jump_time = 0; // client side
}
