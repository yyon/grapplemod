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

		// upgraded values for alternativegrapple items
		public double upgraded_throwspeed = 3.5;
		public double upgraded_maxlen = 40;

		// rope
		public double max_maxlen = 60;
		// hook thrower
		public double max_hookgravity = 20;
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
		public double max_upgrade_hookgravity = 20;
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

		public double min_hookgravity = 1;
		public double min_upgrade_hookgravity = 0;
		
		public String grapplingBlocks = "any";
		public String grapplingNonBlocks = "none";
		
		// rope
		public int enable_maxlen = 0;
		public int enable_phaserope = 0;
		// hook thrower
		public int enable_hookgravity = 0;
		public int enable_throwspeed = 0;
		public int enable_reelin = 0;
		public int enable_verticalthrowangle = 0;
		public int enable_sneakingverticalthrowangle = 0;
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
		
		public boolean longfallbootsrecipe = true;

		// ender staff
		public double ender_staff_strength = 1.5;
		public int ender_staff_recharge = 100;
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
