package com.yyon.grapplinghook;

import net.minecraftforge.common.config.Config;

@Config(modid="grapplemod", name="grappling_hook", category="")
public class GrappleConfig {
	public static class Config {
		// rope
		public double default_maxlen = 30;
		public boolean default_phaserope = false;
		public boolean default_climbkey = true;
		public boolean default_sticky = false;
		// hook thrower
		public double default_hookgravity = 1F;
		public double default_throwspeed = 2F;
		public boolean default_reelin = true;
		public double default_verticalthrowangle = 0F;
		public double default_sneakingverticalthrowangle = 0F;
		public boolean default_detachonkeyrelease = false;
		// motor
		public boolean default_motor = false;
		public double default_motormaxspeed = 4;
		public double default_motoracceleration = 0.2;
		public boolean default_motorwhencrouching = false;
		public boolean default_motorwhennotcrouching = true;
		public boolean default_smartmotor = false;
		public boolean default_motordampener = false;
		public boolean default_pullbackwards = true;
		// swing speed
		public double default_playermovementmult = 1;
		// ender staff
		public boolean default_enderstaff = false;
		// forcefield
		public boolean default_repel = false;
		public double default_repelforce = 1;
		// hook magnet
		public boolean default_attract = false;
		public double default_attractradius = 3;
		// double hook
		public boolean default_doublehook = false;
		public boolean default_smartdoublemotor = true;
		public double default_angle = 20;
		public double default_sneakingangle = 10;
		public boolean default_oneropepull = false;
		// rocket
		public boolean default_rocketenabled = false;
		public double default_rocket_force = 1;
		public double default_rocket_active_time = 0.5;
		public double default_rocket_refuel_ratio = 15;
		public double default_rocket_vertical_angle = 0;
		

		// upgraded values for alternativegrapple items
		public double upgraded_throwspeed = 3.5;
		public double upgraded_maxlen = 60;

		// rope
		public double max_maxlen = 60;
		// hook thrower
		public double max_hookgravity = 100;
		public double max_throwspeed = 5;
		public double max_verticalthrowangle = 45;
		public double max_sneakingverticalthrowangle = 45;
		// motor
		public double max_motormaxspeed = 4;
		public double max_motoracceleration = 0.2;
		// swing speed
		public double max_playermovementmult = 2;
		// forcefield
		public double max_repelforce = 1;
		// hook magnet
		public double max_attractradius = 3;
		// double hook
		public double max_angle = 45;
		public double max_sneakingangle = 45;
		
		
		// rope
		public double max_upgrade_maxlen = 200;
		// hook thrower
		public double max_upgrade_hookgravity = 100;
		public double max_upgrade_throwspeed = 20;
		public double max_upgrade_verticalthrowangle = 90;
		public double max_upgrade_sneakingverticalthrowangle = 90;
		// motorversion
		public double max_upgrade_motormaxspeed = 10;
		public double max_upgrade_motoracceleration = 1;
		// swing speed
		public double max_upgrade_playermovementmult = 5;
		// forcefield
		public double max_upgrade_repelforce = 5;
		// hook magnet
		public double max_upgrade_attractradius = 10;
		// double hook
		public double max_upgrade_angle = 90;
		public double max_upgrade_sneakingangle = 90;
		// rocket
		public double max_rocket_active_time = 0.5;
		public double max_upgrade_rocket_active_time = 20;
		public double max_rocket_force = 1;
		public double max_upgrade_rocket_force = 5;

		public double min_rocket_refuel_ratio = 15;
		public double min_upgrade_rocket_refuel_ratio = 1;
		public double max_rocket_refuel_ratio = 30;
		public double max_upgrade_rocket_refuel_ratio = 30;

		public double min_hookgravity = 1;
		public double min_upgrade_hookgravity = 0;
		
		public double max_upgrade_rocket_vertical_angle = 90;
		public double max_rocket_vertical_angle = 90;
		
		public String grapplingBlocks = "any";
		public String grapplingNonBlocks = "none";
		
		// rope
		public int enable_maxlen = 0;
		public int enable_phaserope = 0;
		public int enable_climbkey = 0;
		public int enable_sticky = 0;
		// hook thrower
		public int enable_hookgravity = 0;
		public int enable_throwspeed = 0;
		public int enable_reelin = 0;
		public int enable_verticalthrowangle = 0;
		public int enable_sneakingverticalthrowangle = 0;
		public int enable_detachonkeyrelease = 0;
		// motor
		public int enable_motor = 0;
		public int enable_motormaxspeed = 0;
		public int enable_motoracceleration = 0;
		public int enable_motorwhencrouching = 0;
		public int enable_motorwhennotcrouching = 0;
		public int enable_smartmotor = 0;
		public int enable_motordampener = 1;
		public int enable_pullbackwards = 0;
		// swing speed
		public int enable_playermovementmult = 0;
		// ender staff
		public int enable_enderstaff = 0;
		// forcefield
		public int enable_repel = 0;
		public int enable_repelforce = 0;
		// hook magnet
		public int enable_attract = 0;
		public int enable_attractradius = 0;
		// double hook
		public int enable_doublehook = 0;
		public int enable_smartdoublemotor = 0;
		public int enable_angle = 0;
		public int enable_sneakingangle = 0;
		public int enable_oneropepull = 0;
		// rocket
		public int enable_rocket = 0;
		public int enable_rocket_force = 0;
		public int enable_rocket_active_time = 0;
		public int enable_rocket_refuel_ratio = 0;
		public int enable_rocket_vertical_angle = 0;

		
		public boolean longfallbootsrecipe = true;
		
		public boolean hookaffectsentities = true;

		// ender staff
		public double ender_staff_strength = 1.5;
		public int ender_staff_recharge = 100;
		
		public double wall_jump_up = 0.7;
		public double wall_jump_side = 0.4;
		
		public double max_wallrun_time = 3;
		
		public double doublejumpforce = 0.8;
		public double slidingjumpforce =  0.6;
		public double wallrun_speed = 0.1;
		public double wallrun_max_speed = 0.7;
		public double wallrun_drag = 0.05;
		public double wallrun_min_speed = 0;
		public double sliding_friction = 1 / 150F;
		public boolean override_allowflight = true;
		
		public double airstrafe_max_speed = 0.7;
		
		public double rope_snap_buffer = 5;
		public int default_durability = 500;
		public double rope_jump_power = 1;
		public boolean rope_jump_at_angle = false;
		public double sliding_min_speed = 0.15;
		public boolean doublejump_relative_to_falling = false;
		
		public boolean dont_override_movement_in_air = false;
		
		public double dont_doublejump_if_falling_faster_than = 999999999.0;
		public double rope_jump_cooldown_s = 0;
		public double climb_speed = 0.3;
		
		public int enchant_rarity_double_jump = 0;
		public int enchant_rarity_sliding = 0;
		public int enchant_rarity_wallrun = 0;
		public float wallrun_camera_tilt_degrees = 5;
		public float wallrun_camera_animation_s = 0.5f;
		public double airstrafe_acceleration = 0.015;
	}
	
	public static Config options = new Config(); // local options
	
	private static Config server_options = null;

	public static Config getconf() {
		if (server_options == null) {
			return options;
		} else {
			return server_options;
		}
	}
	
	public static void setserveroptions(Config newserveroptions) {
		server_options = newserveroptions;
	}
}
