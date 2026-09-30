package com.yyon.grapplemod.config;

public class GrappleConfig {
	public static class Config {
		@Comment("Options for grappling hooks")
		public GrapplingHook grapplinghook = new GrapplingHook();
		public static class GrapplingHook {
			@Comment("Customization: Default values (when creating a new hook) and available values in the grappling hook modifier")
			public Custom custom = new Custom();
			public static class Custom {
				public static class DoubleCustomizationOption {
					@Comment("Value when creating a new non-modified grappling hook")
					public double default_value;
					@Comment("Is this value changeable in the grappling hook modifier? 0 = always enabled, 1 = enabled after the limits upgrade, 2 = always disabled")
					@Range(min = 0, max = 2)
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
					@Range(min = 0, max = 2)
					public int enabled;

					public BooleanCustomizationOption(boolean default_value, int enabled) {
						this.default_value = default_value; this.enabled = enabled;
					}
				}

				@Comment("Options in the Rope upgrade")
				public Rope rope = new Rope();
				public static class Rope {
					@Comment("The length of the rope")
					public DoubleCustomizationOption maxlen = new DoubleCustomizationOption(30, 0, 60, 200);
					@Comment("Allows rope to phase through blocks")
					public BooleanCustomizationOption phaserope = new BooleanCustomizationOption(false, 0);
					@Comment("If the rope bends around a block as it is being thrown, attach at bend")
					public BooleanCustomizationOption sticky = new BooleanCustomizationOption(false, 0);
				}

				@Comment("Options in the Hookthrower upgrade")
				public HookThrower hookthrower = new HookThrower();
				public static class HookThrower {
					@Comment("Gravity on hook when thrown")
					public DoubleCustomizationOption hookgravity = new DoubleCustomizationOption(1F, 0, 100, 100, 1, 0);
					@Comment("Speed of hook when thrown")
					public DoubleCustomizationOption throwspeed = new DoubleCustomizationOption(2F, 0, 5, 20);
					@Comment("Before the hook is attached, crouching will stop the hook from moving farther and slowly reel it in")
					public BooleanCustomizationOption reelin = new BooleanCustomizationOption(true, 0);
					@Comment("Throws the grappling hook above the crosshairs by this angle")
					public DoubleCustomizationOption verticalthrowangle = new DoubleCustomizationOption(0F, 0, 45, 90);
					@Comment("Throws the grappling hook above the crosshairs by this angle when crouching")
					public DoubleCustomizationOption sneakingverticalthrowangle = new DoubleCustomizationOption(0F, 0, 45, 90);
					@Comment("When enabled, hook is only out while button/key is held down. Instead of clicking / pressing key again to detach the hook, release the button/key to detach hook.")
					public BooleanCustomizationOption detachonkeyrelease = new BooleanCustomizationOption(false, 0);
				}

				@Comment("Options in the Motor upgrade")
				public Motor motor = new Motor();
				public static class Motor {
					@Comment("Pulls player towards hook")
					public BooleanCustomizationOption motor = new BooleanCustomizationOption(false, 0);
					@Comment("Maximum speed of motor")
					public DoubleCustomizationOption motormaxspeed = new DoubleCustomizationOption(4, 0, 4, 10);
					@Comment("Acceleration of motor")
					public DoubleCustomizationOption motoracceleration = new DoubleCustomizationOption(0.2, 0, 0.2, 1);
					@Comment("Motor is active when On/Off button (default: shift) is being held")
					public BooleanCustomizationOption motorwhencrouching = new BooleanCustomizationOption(false, 0);
					@Comment("Motor is active when On/Off button (default: shift) is not being held")
					public BooleanCustomizationOption motorwhennotcrouching = new BooleanCustomizationOption(true, 0);
					@Comment("Adjusts motor speed so that player moves towards crosshairs (up/down)")
					public BooleanCustomizationOption smartmotor = new BooleanCustomizationOption(false, 0);
					@Comment("Reduces motion perpendicular to the rope so that the rope pulls straighter")
					public BooleanCustomizationOption motordampener = new BooleanCustomizationOption(false, 1);
					@Comment("Motor pulls even if you are facing the other way")
					public BooleanCustomizationOption pullbackwards = new BooleanCustomizationOption(true, 0);
				}

				@Comment("Options in the Swing upgrade")
				public Swing swing = new Swing();
				public static class Swing {
					@Comment("Acceleration of player when using movement keys while swinging")
					public DoubleCustomizationOption playermovementmult = new DoubleCustomizationOption(1, 0, 2, 5);
				}

				@Comment("Options in the Enderstaff upgrade")
				public EnderStaff enderstaff = new EnderStaff();
				public static class EnderStaff {
					@Comment("Left click launches player forwards")
					public BooleanCustomizationOption enderstaff = new BooleanCustomizationOption(false, 0);
				}

				@Comment("Options in the Forcefield upgrade")
				public Forcefield forcefield = new Forcefield();
				public static class Forcefield {
					@Comment("Player is repelled from nearby blocks when swinging")
					public BooleanCustomizationOption repel = new BooleanCustomizationOption(false, 0);
					@Comment("Force nearby blocks exert on the player")
					public DoubleCustomizationOption repelforce = new DoubleCustomizationOption(1, 0, 1, 5);
				}

				@Comment("Options in the Magnet upgrade")
				public Magnet magnet = new Magnet();
				public static class Magnet {
					@Comment("Hook is attracted to nearby blocks when thrown")
					public BooleanCustomizationOption attract = new BooleanCustomizationOption(false, 0);
					@Comment("Radius of attraction")
					public DoubleCustomizationOption attractradius = new DoubleCustomizationOption(3, 0, 3, 10);
				}

				@Comment("Options in the Doublehook upgrade")
				public DoubleHook doublehook = new DoubleHook();
				public static class DoubleHook {
					@Comment("Two hooks are thrown at once")
					public BooleanCustomizationOption doublehook = new BooleanCustomizationOption(false, 0);
					@Comment("Adjusts motor speed so that player moves towards crosshairs (left/right) when used with motor")
					public BooleanCustomizationOption smartdoublemotor = new BooleanCustomizationOption(true, 0);
					@Comment("Angle that each hook is thrown from center")
					public DoubleCustomizationOption angle = new DoubleCustomizationOption(20, 0, 45, 90);
					@Comment("Angle that each hook is thrown from center when crouching (don't have 'crouch to reel in' enabled if you want to use this)")
					public DoubleCustomizationOption sneakingangle = new DoubleCustomizationOption(10, 0, 45, 90);
					@Comment("When motor is enabled and only one hook is attached, activate the motor (if disabled, wait until both hooks are attached before pulling)")
					public BooleanCustomizationOption oneropepull = new BooleanCustomizationOption(false, 0);
				}

				@Comment("Options in the Rocket upgrade")
				public Rocket rocket = new Rocket();
				public static class Rocket {
					@Comment("Propels the player forward while a button is held")
					public BooleanCustomizationOption rocketenabled = new BooleanCustomizationOption(false, 0);
					@Comment("How fast the rocket propels the player")
					public DoubleCustomizationOption rocket_force = new DoubleCustomizationOption(1, 0, 1, 5);
					@Comment("The time that the rocket can be used until it has to refuel (fuel auto-regenerates when it is not being used)")
					public DoubleCustomizationOption rocket_active_time = new DoubleCustomizationOption(0.5, 0, 0.5, 20);
					@Comment("The ratio of the time that the rocket takes to regenerate fuel, compared to the time the rocket is used. (e.g. 2.0 means a unit of fuel that takes 1 second to use up will take 2 seconds to regenerate. Lower is better. )")
					public DoubleCustomizationOption rocket_refuel_ratio = new DoubleCustomizationOption(15, 0, 30, 30, 15, 1);
					@Comment("The angle upwards that the rocket force is applied")
					public DoubleCustomizationOption rocket_vertical_angle = new DoubleCustomizationOption(0, 0, 90, 90);
				}
			}


			@Comment("If you want the hook to interact with different blocks differently")
			public Blocks blocks = new Blocks();
			public static class Blocks {
				@Comment("A list of (comma-separated, ID name) blocks that the grappling hook can attach to (aka whitelist)")
				public String grapplingBlocks = "any";
				@Comment("A list of (comma-separated, ID name) blocks that the grappling hook can't attach to (aka blacklist)")
				public String grapplingNonBlocks = "none";
				@Comment("A list of (comma-separated, ID name) blocks that the grappling hook will break if it hits (e.g. glass)")
				public String grappleBreakBlocks = "none";
			}

			@Comment("More config options for grappling hooks")
			public Other other = new Other();
			public static class Other {
				@Comment("If a hook hits a mob, yank it towards the player")
				public boolean hookaffectsentities = true;
				@Comment("If the player somehow manages to stretch the rope past its maximum length + this value (e.g. by teleporting), the rope snaps")
				public double rope_snap_buffer = 5;
				@Comment("Durability for the grappling hook item")
				@Startup
				public int default_durability = 500;
				@Comment("When jumping from a rope, apply at most this speed (blocks/tick)")
				public double rope_jump_power = 1;
				@Comment("When jumping from a rope, jump in the angle of the rope (otherwise, jump up)")
				public boolean rope_jump_at_angle = false;
				@Comment("Don't allow jumping from a rope if player just jumped from another rope in less than this time ago (time in seconds).")
				public double rope_jump_cooldown_s = 0;
				@Comment("How fast the player can climb a rope (blocks/tick)")
				public double climb_speed = 0.3;
			}
		}

		@Comment("Options for long fall boots")
		public LongFallBoots longfallboots = new LongFallBoots();
		public static class LongFallBoots {
			@Comment("Allow player to make long fall boots by right-clicking a grappling hook modifier block with feather falling IV diamond boots")
			public boolean longfallbootsrecipe = true;
		}

		@Comment("Options for the ender staff")
		public EnderStaff enderstaff = new EnderStaff();
		public static class EnderStaff {
			@Comment("Speed for ender staff (blocks/tick)")
			public double ender_staff_strength = 1.5;
			@Comment("Time it takes to rechange the ender staff (ticks)")
			public int ender_staff_recharge = 100;
		}

		@Comment("Options for enchantments")
		public Enchantments enchantments = new Enchantments();
		public static class Enchantments {
			@Comment("Options for wallrun enchantment")
			public Wallrun wallrun = new Wallrun();
			public static class Wallrun {
				@Comment("When jumping off a wall, jump at speed upwards (blocks/tick)")
				public double wall_jump_up = 0.7;
				@Comment("When jumping off a wall, jump at speed sideways (blocks/tick)")
				public double wall_jump_side = 0.4;
				@Comment("Maximum time a player can spend wallrunning (seconds)")
				public double max_wallrun_time = 3;
				@Comment("Acceleration for wallrunning (blocks/tick^2)")
				public double wallrun_speed = 0.1;
				@Comment("Mamimum speed for wallrunning (blocks/tick)")
				public double wallrun_max_speed = 0.7;
				@Comment("Drag force for wallrunning (blocks/tick^2)")
				public double wallrun_drag = 0.01;
				@Comment("Mimimum speed required for wallrunning (blocks/tick)")
				public double wallrun_min_speed = 0;
			}

			@Comment("Options for double jump enchantment")
			public DoubleJump doublejump = new DoubleJump();
			public static class DoubleJump {
				@Comment("Jump at this speed (blocks/tick)")
				public double doublejumpforce = 0.8;
				@Comment("Keep the player's vertical momentum when double jumping")
				public boolean doublejump_relative_to_falling = false;
				@Comment("If the player is falling faster than this speed, don't allow double jumps (blocks/tick)")
				public double dont_doublejump_if_falling_faster_than = 99999999.0;
			}

			@Comment("Options for sliding enchantment")
			public Slide slide = new Slide();
			public static class Slide {
				@Comment("Jump from a slide at this speed (blocks/tick)")
				public double slidingjumpforce =  0.6;
				@Comment("Drag force for wallrunning (blocks/tick^2)")
				public double sliding_friction = 1 / 150F;
				@Comment("Mimumn speed required to start sliding")
				public double sliding_min_speed = 0.15;
				@Comment("Minimum speed required to keep sliding")
				public double sliding_end_min_speed = 0.01;
			}
		}

		@Comment("Misc. options")
		public Other other = new Other();
		public static class Other {
			@Comment("If true, automatically set allow-flight=true on servers (to prevent grappling hook from occasionally being misidentified as flying)")
			public boolean override_allowflight = true;
			@Comment("Maximum movement speed using the movement keys while in midair (blocks/tick) (after this mod has taken over movement (from a grappling hook / wallrun / ender staff / etc.)")
			public double airstrafe_max_speed = 0.7;
			@Comment("Acceleration using movement keys while in midair (blocks/tick^2) (after this mod has taken over movement (from a grappling hook / wallrun / ender staff / etc.)")
			public double airstrafe_acceleration = 0.015;
			@Comment("Disable this mod taking over movement while in midair (after this mod has taken over movement (from a grappling hook / wallrun / ender staff / etc.) (Note: minecraft has very high air friction)")
			public boolean dont_override_movement_in_air = false;
		}
	}

	public static class ClientConfig {
		@Comment("Camera settings")
		public Camera camera = new Camera();
		public static class Camera {
			@Comment("Tilt the camera while wallrunning (degrees)")
			public float wallrun_camera_tilt_degrees = 10;
			@Comment("Time it takes to tilt the camera when starting to wallrun (seconds)")
			public float wallrun_camera_animation_s = 0.5f;
		}

		@Comment("Sound effect settings")
		public Sounds sounds = new Sounds();
		public static class Sounds {
			@Comment("Play wallrun footsteps at most every this many seconds")
			public double wallrun_sound_effect_time_s = 0.35;
			@Comment("Wallrunning footsteps volume (1=default)")
			public float wallrun_sound_volume = 1.0F;
			@Comment("Double jump volume (1=default)")
			public float doublejump_sound_volume = 1.0F;
			@Comment("Sliding volume (1=default)")
			public float slide_sound_volume = 1.0F;
			@Comment("Jump from wallrun volume (1=default)")
			public float wallrunjump_sound_volume = 1.0F;
			@Comment("Rocket volume (1=default)")
			public float rocket_sound_volume = 1.0F;
			@Comment("Ender staff volume (1=default)")
			public float enderstaff_sound_volume = 1.0F;
		}
	}

	private static final Config options = new Config();
	private static final ClientConfig clientOptions = new ClientConfig();

	public static final GrappleConfigSpec STARTUP = new GrappleConfigSpec(options, "options", GrappleConfigSpec.Mode.STARTUP);
	public static final GrappleConfigSpec SERVER = new GrappleConfigSpec(options, "options", GrappleConfigSpec.Mode.SERVER);
	public static final GrappleConfigSpec CLIENT = new GrappleConfigSpec(clientOptions, "clientOptions", GrappleConfigSpec.Mode.ALL);

	public static GrappleConfigSpec getSpec(Object spec) {
		for (GrappleConfigSpec configSpec : new GrappleConfigSpec[] {STARTUP, SERVER, CLIENT}) {
			if (configSpec.getSpec() == spec) {
				return configSpec;
			}
		}
		return null;
	}

	public static Config getConf() {
		return options;
	}

	public static ClientConfig getClientConf() {
		return clientOptions;
	}
}
