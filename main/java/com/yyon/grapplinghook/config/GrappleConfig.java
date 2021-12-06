package com.yyon.grapplinghook.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

//@Config(modid="grapplemod", name="grappling_hook", category="")
@Config(name = "grapplemod")
public class GrappleConfig implements ConfigData {
	public static class Config {
		@ConfigEntry.Gui.CollapsibleObject
		@Tooltip
		public GrapplingHook grapplinghook = new GrapplingHook();
		public static class GrapplingHook {
			@ConfigEntry.Gui.CollapsibleObject
			@Tooltip
			public Custom custom = new Custom();
			public static class Custom {
				public static class DoubleCustomizationOption {
					@Comment("Value when creating a new non-modified grappling hook")
					public double default_value;
					@Comment("Is this value changeable in the grappling hook modifier? 0 = always enabled, 1 = enabled after the limits upgrade, 2 = always disabled")
					@BoundedDiscrete(max=2, min=0)
					public int enabled;
					@Comment("Maximum value in the grappling hook modifier (before limits upgrade)")
					public double max;
					@Comment("Maximum value in the grappling hook modifier (after limits upgrade)")
					public double max_upgraded;
					@Comment("Minimum value in the grappling hook modifier (before limits upgrade)")
					public double min;
					@Comment("Minimum value in the grappling hook modifier (after limits upgrade)")
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
					@Comment("Value when creating a new non-modified grappling hook")
					public boolean default_value;
					@Comment("Is this value changeable in the grappling hook modifier? 0 = always enabled, 1 = enabled after the limits upgrade, 2 = always disabled")
					@BoundedDiscrete(max=2, min=0)
					public int enabled;
					
					public BooleanCustomizationOption(boolean default_value, int enabled) {
						this.default_value = default_value; this.enabled = enabled;
					}
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				@Tooltip
				public Rope rope = new Rope();
				public static class Rope {
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption maxlen = new DoubleCustomizationOption(30, 0, 60, 200);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption phaserope = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption sticky = new BooleanCustomizationOption(false, 0);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				@Tooltip
				public HookThrower hookthrower = new HookThrower();
				public static class HookThrower {
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption hookgravity = new DoubleCustomizationOption(1F, 0, 100, 100, 1, 0);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption throwspeed = new DoubleCustomizationOption(2F, 0, 5, 20);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption reelin = new BooleanCustomizationOption(true, 0);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption verticalthrowangle = new DoubleCustomizationOption(0F, 0, 45, 90);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption sneakingverticalthrowangle = new DoubleCustomizationOption(0F, 0, 45, 90);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption detachonkeyrelease = new BooleanCustomizationOption(false, 0);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				@Tooltip
				public Motor motor = new Motor();
				public static class Motor {
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption motor = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption motormaxspeed = new DoubleCustomizationOption(4, 0, 4, 10);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption motoracceleration = new DoubleCustomizationOption(0.2, 0, 0.2, 1);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption motorwhencrouching = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption motorwhennotcrouching = new BooleanCustomizationOption(true, 0);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption smartmotor = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption motordampener = new BooleanCustomizationOption(false, 1);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption pullbackwards = new BooleanCustomizationOption(true, 0);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				@Tooltip
				public Swing swing = new Swing();
				public static class Swing {
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption playermovementmult = new DoubleCustomizationOption(1, 0, 2, 5);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				@Tooltip
				public EnderStaff enderstaff = new EnderStaff();
				public static class EnderStaff {
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption enderstaff = new BooleanCustomizationOption(false, 0);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				@Tooltip
				public Forcefield forcefield = new Forcefield();
				public static class Forcefield {
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption repel = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption repelforce = new DoubleCustomizationOption(1, 0, 1, 5);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				@Tooltip
				public Magnet magnet = new Magnet();
				public static class Magnet {
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption attract = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption attractradius = new DoubleCustomizationOption(3, 0, 3, 10);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				@Tooltip
				public DoubleHook doublehook = new DoubleHook();
				public static class DoubleHook {
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption doublehook = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption smartdoublemotor = new BooleanCustomizationOption(true, 0);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption angle = new DoubleCustomizationOption(20, 0, 45, 90);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption sneakingangle = new DoubleCustomizationOption(10, 0, 45, 90);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption oneropepull = new BooleanCustomizationOption(false, 0);
				}
				
				@ConfigEntry.Gui.CollapsibleObject
				@Tooltip
				public Rocket rocket = new Rocket();
				public static class Rocket {
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public BooleanCustomizationOption rocketenabled = new BooleanCustomizationOption(false, 0);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption rocket_force = new DoubleCustomizationOption(1, 0, 1, 5);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption rocket_active_time = new DoubleCustomizationOption(0.5, 0, 0.5, 20);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption rocket_refuel_ratio = new DoubleCustomizationOption(15, 0, 30, 30, 15, 1);
					@ConfigEntry.Gui.CollapsibleObject
					@Tooltip
					public DoubleCustomizationOption rocket_vertical_angle = new DoubleCustomizationOption(0, 0, 90, 90);
				}
			}
			
			
			@ConfigEntry.Gui.CollapsibleObject
			@Tooltip
			public Blocks blocks = new Blocks();
			public static class Blocks {
				@Tooltip
				public String grapplingBlocks = "any";
				@Tooltip
				public String grapplingNonBlocks = "none";
				@Tooltip
				public String grappleBreakBlocks = "none";
			}
			
			@ConfigEntry.Gui.CollapsibleObject
			@Tooltip
			public Other other = new Other();
			public static class Other {
				@Tooltip
				public boolean hookaffectsentities = true;
				@Tooltip
				public double rope_snap_buffer = 5;
				@Tooltip
				public int default_durability = 500;
				@Tooltip
				public double rope_jump_power = 1;
				@Tooltip
				public boolean rope_jump_at_angle = false;
				@Tooltip
				public double rope_jump_cooldown_s = 0;
				@Tooltip
				public double climb_speed = 0.3;
			}
		}
		
		@ConfigEntry.Gui.CollapsibleObject
		@Tooltip
		public LongFallBoots longfallboots = new LongFallBoots();
		public static class LongFallBoots {
			@Tooltip
			public boolean longfallbootsrecipe = true;
		}
		
		@ConfigEntry.Gui.CollapsibleObject
		@Tooltip
		public EnderStaff enderstaff = new EnderStaff();
		public static class EnderStaff {
			@Tooltip
			public double ender_staff_strength = 1.5;
			@Tooltip
			public int ender_staff_recharge = 100;
		}
		
		@ConfigEntry.Gui.CollapsibleObject
		@Tooltip
		public Enchantments enchantments = new Enchantments();
		public static class Enchantments {
			@ConfigEntry.Gui.CollapsibleObject
			@Tooltip
			public Wallrun wallrun = new Wallrun();
			public static class Wallrun {
				@Tooltip
				public double wall_jump_up = 0.7;
				@Tooltip
				public double wall_jump_side = 0.4;
				@Tooltip
				public double max_wallrun_time = 3;
				@Tooltip
				public double wallrun_speed = 0.1;
				@Tooltip
				public double wallrun_max_speed = 0.7;
				@Tooltip
				public double wallrun_drag = 0.01;
				@Tooltip
				public double wallrun_min_speed = 0;
				@BoundedDiscrete(max=3, min=0)
				@Tooltip
				public int enchant_rarity_wallrun = 0;
			}
			
			@ConfigEntry.Gui.CollapsibleObject
			@Tooltip
			public DoubleJump doublejump = new DoubleJump();
			public static class DoubleJump {
				@Tooltip
				public double doublejumpforce = 0.8;
				@Tooltip
				public boolean doublejump_relative_to_falling = false;
				@Tooltip
				public double dont_doublejump_if_falling_faster_than = 99999999.0;
				@BoundedDiscrete(max=3, min=0)
				@Tooltip
				public int enchant_rarity_double_jump = 0;
			}
			
			@ConfigEntry.Gui.CollapsibleObject
			@Tooltip
			public Slide slide = new Slide();
			public static class Slide {
				@Tooltip
				public double slidingjumpforce =  0.6;
				@Tooltip
				public double sliding_friction = 1 / 150F;
				@Tooltip
				public double sliding_min_speed = 0.15;
				@Tooltip
				public double sliding_end_min_speed = 0.01;
				@BoundedDiscrete(max=3, min=0)
				@Tooltip
				public int enchant_rarity_sliding = 0;
			}
		}
		
		@ConfigEntry.Gui.CollapsibleObject
		@Tooltip
		public Other other = new Other();
		public static class Other {
			@Tooltip
			public boolean override_allowflight = true;
			@Tooltip
			public double airstrafe_max_speed = 0.7;
			@Tooltip
			public double airstrafe_acceleration = 0.015;
			@Tooltip
			public boolean dont_override_movement_in_air = false;
		}
	}
	
    @ConfigEntry.Gui.CollapsibleObject
    @Tooltip
	public Config options = new Config(); // local options
	
    @ConfigEntry.Gui.Excluded
	private static Config serverOptions = null;
	
	public static class ClientConfig {
		@ConfigEntry.Gui.CollapsibleObject
		@Tooltip
		public Camera camera = new Camera();
		public static class Camera {
			@Tooltip
			public float wallrun_camera_tilt_degrees = 10;
			@Tooltip
			public float wallrun_camera_animation_s = 0.5f;
		}
		
		@ConfigEntry.Gui.CollapsibleObject
		@Tooltip
		public Sounds sounds = new Sounds();
		public static class Sounds {
			@Tooltip
			public double wallrun_sound_effect_time_s = 0.35;
			@Tooltip
			public float wallrun_sound_volume = 1.0F;
			@Tooltip
			public float doublejump_sound_volume = 1.0F;
			@Tooltip
			public float slide_sound_volume = 1.0F;
			@Tooltip
			public float wallrunjump_sound_volume = 1.0F;
			@Tooltip
			public float rocket_sound_volume = 1.0F;
			@Tooltip
			public float enderstaff_sound_volume = 1.0F;
		}
	}
	
    @ConfigEntry.Gui.CollapsibleObject
	@Tooltip
	public ClientConfig clientOptions = new ClientConfig(); // client-only options, don't need to sync with server

	public static Config getConf() {
		if (serverOptions == null) {
			return AutoConfig.getConfigHolder(GrappleConfig.class).getConfig().options;
		} else {
			return serverOptions;
		}
	}
	
	public static void setServerOptions(Config newserveroptions) {
		serverOptions = newserveroptions;
	}
	
	public static ClientConfig getClientConf() {
		return AutoConfig.getConfigHolder(GrappleConfig.class).getConfig().clientOptions;
	}
}
