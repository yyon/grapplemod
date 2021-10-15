package com.yyon.grapplinghook;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

//@Config(modid="grapplemod", name="grappling_hook", category="")
@Config(name = "grapplemod")
public class GrappleConfig implements ConfigData {
	public static class Config {
		@ConfigEntry.Gui.CollapsibleObject
		public GrapplingHook grapplinghook = new GrapplingHook();
		public static class GrapplingHook {
			@ConfigEntry.Gui.CollapsibleObject
			public Custom custom = new Custom();
			public static class Custom {
				public static class DoubleCustomizationOption {
					public double default_value;
					public int enabled;
					public double max;
					public double max_upgraded;
					public double min;
					public double min_upgraded;
					
					public DoubleCustomizationOption(double default_value, int enabled, double max, double max_upgraded) {
						this.default_value = default_value; this.enabled = enabled; this.max = max; this.max_upgraded = max_upgraded;
						this.min = 0; this.min_upgraded = 0;
					}
					
					public DoubleCustomizationOption(double default_value, int enabled, double max, double max_upgraded, double min, double min_upgraded) {
						this(default_value, enabled, max, max_upgraded);
						this.min = min; this.min_upgraded = min_upgraded;
					}
				}
				public static class BooleanCustomizationOption {
					public boolean default_value;
					public int enabled;
					
					public BooleanCustomizationOption(boolean default_value, int enabled) {
						this.default_value = default_value; this.enabled = enabled;
					}
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				public Rope rope = new Rope();
				public static class Rope {
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption maxlen = new DoubleCustomizationOption(30, 0, 60, 200);
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption phaserope = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption climbkey = new BooleanCustomizationOption(true, 0);
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption sticky = new BooleanCustomizationOption(false, 0);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				public HookThrower hookthrower = new HookThrower();
				public static class HookThrower {
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption hookgravity = new DoubleCustomizationOption(1F, 0, 100, 100, 1, 0);
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption throwspeed = new DoubleCustomizationOption(2F, 0, 5, 20);
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption reelin = new BooleanCustomizationOption(true, 0);
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption verticalthrowangle = new DoubleCustomizationOption(0F, 0, 45, 90);
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption sneakingverticalthrowangle = new DoubleCustomizationOption(0F, 0, 45, 90);
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption detachonkeyrelease = new BooleanCustomizationOption(false, 0);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				public Motor motor = new Motor();
				public static class Motor {
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption motor = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption motormaxspeed = new DoubleCustomizationOption(4, 0, 4, 10);
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption motoracceleration = new DoubleCustomizationOption(0.2, 0, 0.2, 1);
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption motorwhencrouching = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption motorwhennotcrouching = new BooleanCustomizationOption(true, 0);
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption smartmotor = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption motordampener = new BooleanCustomizationOption(false, 1);
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption pullbackwards = new BooleanCustomizationOption(true, 0);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				public Swing swing = new Swing();
				public static class Swing {
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption playermovementmult = new DoubleCustomizationOption(1, 0, 2, 5);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				public EnderStaff enderstaff = new EnderStaff();
				public static class EnderStaff {
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption enderstaff = new BooleanCustomizationOption(false, 0);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				public Forcefield forcefield = new Forcefield();
				public static class Forcefield {
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption repel = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption repelforce = new DoubleCustomizationOption(1, 0, 1, 5);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				public Magnet magnet = new Magnet();
				public static class Magnet {
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption attract = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption attractradius = new DoubleCustomizationOption(3, 0, 3, 10);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				public DoubleHook doublehook = new DoubleHook();
				public static class DoubleHook {
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption doublehook = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption smartdoublemotor = new BooleanCustomizationOption(true, 0);
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption angle = new DoubleCustomizationOption(20, 0, 45, 90);
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption sneakingangle = new DoubleCustomizationOption(10, 0, 45, 90);
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption oneropepull = new BooleanCustomizationOption(false, 0);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				public Rocket rocket = new Rocket();
				public static class Rocket {
					@ConfigEntry.Gui.CollapsibleObject
					public BooleanCustomizationOption rocketenabled = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption rocket_force = new DoubleCustomizationOption(1, 0, 1, 5);
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption rocket_active_time = new DoubleCustomizationOption(0.5, 0, 0.5, 20);
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption rocket_refuel_ratio = new DoubleCustomizationOption(15, 0, 30, 30, 15, 1);
					@ConfigEntry.Gui.CollapsibleObject
					public DoubleCustomizationOption rocket_vertical_angle = new DoubleCustomizationOption(0, 0, 90, 90);
				}
			}
			
			
			@ConfigEntry.Gui.CollapsibleObject
			public Blocks blocks = new Blocks();
			public static class Blocks {
				public String grapplingBlocks = "any";
				public String grapplingNonBlocks = "none";
				public String grappleBreakBlocks = "none";
			}
			
			@ConfigEntry.Gui.CollapsibleObject
			public Other other = new Other();
			public static class Other {
				public boolean hookaffectsentities = true;
				public double rope_snap_buffer = 5;
				public int default_durability = 500;
				public double rope_jump_power = 1;
				public boolean rope_jump_at_angle = false;
				public double rope_jump_cooldown_s = 0;
				public double climb_speed = 0.3;
			}
		}
		
		@ConfigEntry.Gui.CollapsibleObject
		public LongFallBoots longfallboots = new LongFallBoots();
		public static class LongFallBoots {
			public boolean longfallbootsrecipe = true;
		}
		
		@ConfigEntry.Gui.CollapsibleObject
		public EnderStaff enderstaff = new EnderStaff();
		public static class EnderStaff {
			public double ender_staff_strength = 1.5;
			public int ender_staff_recharge = 100;
		}
		
		@ConfigEntry.Gui.CollapsibleObject
		public Enchantments enchantments = new Enchantments();
		public static class Enchantments {
			@ConfigEntry.Gui.CollapsibleObject
			public Wallrun wallrun = new Wallrun();
			public static class Wallrun {
				public double wall_jump_up = 0.7;
				public double wall_jump_side = 0.4;
				public double max_wallrun_time = 3;
				public double wallrun_speed = 0.1;
				public double wallrun_max_speed = 0.7;
				public double wallrun_drag = 0.05;
				public double wallrun_min_speed = 0;
				public int enchant_rarity_wallrun = 0;
			}
			
			@ConfigEntry.Gui.CollapsibleObject
			public DoubleJump doublejump = new DoubleJump();
			public static class DoubleJump {
				public double doublejumpforce = 0.8;
				public boolean doublejump_relative_to_falling = false;
				public double dont_doublejump_if_falling_faster_than = 99999999.0;
				public int enchant_rarity_double_jump = 0;
			}
			
			@ConfigEntry.Gui.CollapsibleObject
			public Slide slide = new Slide();
			public static class Slide {
				public double slidingjumpforce =  0.6;
				public double sliding_friction = 1 / 150F;
				public double sliding_min_speed = 0.15;
				public double sliding_end_min_speed = 0.01;
				public int enchant_rarity_sliding = 0;
			}
		}
		
		@ConfigEntry.Gui.CollapsibleObject
		public Other other = new Other();
		public static class Other {
			public boolean override_allowflight = true;
			public double airstrafe_max_speed = 0.7;
			public double airstrafe_acceleration = 0.015;
			public boolean dont_override_movement_in_air = false;
		}
	}
	
    @ConfigEntry.Gui.CollapsibleObject
    public Config options = new Config(); // local options
	
    @ConfigEntry.Gui.Excluded
	private static Config server_options = null;
	
	public static class ClientConfig {
		@ConfigEntry.Gui.CollapsibleObject
		public Camera camera = new Camera();
		public static class Camera {
			public float wallrun_camera_tilt_degrees = 5;
			public float wallrun_camera_animation_s = 0.5f;
		}
		
		@ConfigEntry.Gui.CollapsibleObject
		public Sounds sounds = new Sounds();
		public static class Sounds {
			public double wallrun_sound_effect_time_s = 0.35;
			public float wallrun_sound_volume = 1.0F;
			public float doublejump_sound_volume = 1.0F;
			public float slide_sound_volume = 1.0F;
			public float wallrunjump_sound_volume = 1.0F;
		}
	}
	
    @ConfigEntry.Gui.CollapsibleObject
	public ClientConfig client_options = new ClientConfig(); // client-only options, don't need to sync with server

	public static Config getconf() {
		if (server_options == null) {
			return AutoConfig.getConfigHolder(GrappleConfig.class).getConfig().options;
		} else {
			return server_options;
		}
	}
	
	public static void setserveroptions(Config newserveroptions) {
		server_options = newserveroptions;
	}
	
	public static ClientConfig getclientconf() {
		return AutoConfig.getConfigHolder(GrappleConfig.class).getConfig().client_options;
	}
}
