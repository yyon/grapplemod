package com.yyon.grapplinghook.utils;

import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.GrappleMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class GrappleCustomization {
	public static final String[] BOOLEAN_IDS = new String[] {"phaserope", "motor", "motorwhencrouching", "motorwhennotcrouching", "smartmotor", "enderstaff", "repel", "attract", "doublehook", "smartdoublemotor", "motordampener", "reelin", "pullbackwards", "oneropepull", "sticky", "detachonkeyrelease", "rocket"};
	public static final String[] DOUBLE_IDS = new String[] {"maxlen", "hookgravity", "throwspeed", "motormaxspeed", "motoracceleration", "playermovementmult", "repelforce", "attractradius", "angle", "sneakingangle", "verticalthrowangle", "sneakingverticalthrowangle", "rocket_force", "rocket_active_time", "rocket_refuel_ratio", "rocket_vertical_angle"};
	
	// rope
	public double maxlen;
	public boolean phaserope;
	public boolean sticky;

	// hook thrower
	public double hookgravity;
	public double throwspeed;
	public boolean reelin;
	public double verticalthrowangle;
	public double sneakingverticalthrowangle;
	public boolean detachonkeyrelease;

	// motor
	public boolean motor;
	public double motormaxspeed;
	public double motoracceleration;
	public boolean motorwhencrouching;
	public boolean motorwhennotcrouching;
	public boolean smartmotor;
	public boolean motordampener;
	public boolean pullbackwards;
	
	// swing speed
	public double playermovementmult;

	// ender staff
	public boolean enderstaff;

	// forcefield
	public boolean repel;
	public double repelforce;
	
	// hook magnet
	public boolean attract;
	public double attractradius;
	
	// double hook
	public boolean doublehook;
	public boolean smartdoublemotor;
	public double angle;
	public double sneakingangle;
	public boolean oneropepull;
	
	// rocket
	public boolean rocket;
	public double rocket_force;
	public double rocket_active_time;
	public double rocket_refuel_ratio;
	public double rocket_vertical_angle;
	
	public enum UpgradeCategory {
		ROPE ("rope"), 
		THROW ("throw"), 
		MOTOR ("motor"), 
		SWING ("swing"), 
		STAFF ("staff"), 
		FORCEFIELD ("forcefield"), 
		MAGNET ("magnet"), 
		DOUBLE ("double"),
		LIMITS ("limits"),
		ROCKET ("rocket");
		
		private final String nameUnlocalized;
		UpgradeCategory(String name) {
			this.nameUnlocalized = name;
		}
		
		public String getName() {
			if (ClientProxyInterface.proxy != null) {
				return ClientProxyInterface.proxy.localize("grapplemod.upgradecategories." + this.nameUnlocalized);
			} else {
				return nameUnlocalized;
			}
		}
		
		public static UpgradeCategory fromInt(int i) {
			return UpgradeCategory.values()[i];
		}
		public int toInt() {
			for (int i = 0; i < size(); i++) {
				if (UpgradeCategory.values()[i] == this) {
					return i;
				}
			}
			return -1;
		}
		public static int size() {
			return UpgradeCategory.values().length;
		}
		public Item getItem() {
			if (this == UpgradeCategory.ROPE) {
				return CommonSetup.ropeUpgradeItem.get();
			} else if (this == UpgradeCategory.THROW) {
				return CommonSetup.throwUpgradeItem.get();
			} else if (this == UpgradeCategory.MOTOR) {
				return CommonSetup.motorUpgradeItem.get();
			} else if (this == UpgradeCategory.SWING) {
				return CommonSetup.swingUpgradeItem.get();
			} else if (this == UpgradeCategory.STAFF) {
				return CommonSetup.staffUpgradeItem.get();
			} else if (this == UpgradeCategory.FORCEFIELD) {
				return CommonSetup.forcefieldUpgradeItem.get();
			} else if (this == UpgradeCategory.MAGNET) {
				return CommonSetup.magnetUpgradeItem.get();
			} else if (this == UpgradeCategory.DOUBLE) {
				return CommonSetup.doubleUpgradeItem.get();
			} else if (this == UpgradeCategory.LIMITS) {
				return CommonSetup.limitsUpgradeItem.get();
			} else if (this == UpgradeCategory.ROCKET) {
				return CommonSetup.rocketUpgradeItem.get();
			}
			return null;
		}
	}
	
	public GrappleCustomization() {
		this.setDefaults();
	}
	
	public void setDefaults() {
		for (String option : BOOLEAN_IDS) {
			GrappleConfig.Config.GrapplingHook.Custom.BooleanCustomizationOption optionconfig = getBooleanConfig(option);
			this.setBoolean(option, optionconfig.default_value);
		}
		for (String option : DOUBLE_IDS) {
			GrappleConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption optionconfig = getDoubleConfig(option);
			this.setDouble(option, optionconfig.default_value);
		}
	}
	
	public GrappleConfig.Config.GrapplingHook.Custom.BooleanCustomizationOption getBooleanConfig(String option) {
		return switch (option) {
			case "phaserope" -> GrappleConfig.getConf().grapplinghook.custom.rope.phaserope;
			case "motor" -> GrappleConfig.getConf().grapplinghook.custom.motor.motor;
			case "motorwhencrouching" -> GrappleConfig.getConf().grapplinghook.custom.motor.motorwhencrouching;
			case "motorwhennotcrouching" -> GrappleConfig.getConf().grapplinghook.custom.motor.motorwhennotcrouching;
			case "smartmotor" -> GrappleConfig.getConf().grapplinghook.custom.motor.smartmotor;
			case "enderstaff" -> GrappleConfig.getConf().grapplinghook.custom.enderstaff.enderstaff;
			case "repel" -> GrappleConfig.getConf().grapplinghook.custom.forcefield.repel;
			case "attract" -> GrappleConfig.getConf().grapplinghook.custom.magnet.attract;
			case "doublehook" -> GrappleConfig.getConf().grapplinghook.custom.doublehook.doublehook;
			case "smartdoublemotor" -> GrappleConfig.getConf().grapplinghook.custom.doublehook.smartdoublemotor;
			case "motordampener" -> GrappleConfig.getConf().grapplinghook.custom.motor.motordampener;
			case "reelin" -> GrappleConfig.getConf().grapplinghook.custom.hookthrower.reelin;
			case "pullbackwards" -> GrappleConfig.getConf().grapplinghook.custom.motor.pullbackwards;
			case "oneropepull" -> GrappleConfig.getConf().grapplinghook.custom.doublehook.oneropepull;
			case "sticky" -> GrappleConfig.getConf().grapplinghook.custom.rope.sticky;
			case "detachonkeyrelease" -> GrappleConfig.getConf().grapplinghook.custom.hookthrower.detachonkeyrelease;
			case "rocket" -> GrappleConfig.getConf().grapplinghook.custom.rocket.rocketenabled;
			default -> null;
		};
	}

	public GrappleConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption getDoubleConfig(String option) {
		return switch (option) {
			case "maxlen" -> GrappleConfig.getConf().grapplinghook.custom.rope.maxlen;
			case "hookgravity" -> GrappleConfig.getConf().grapplinghook.custom.hookthrower.hookgravity;
			case "throwspeed" -> GrappleConfig.getConf().grapplinghook.custom.hookthrower.throwspeed;
			case "motormaxspeed" -> GrappleConfig.getConf().grapplinghook.custom.motor.motormaxspeed;
			case "motoracceleration" -> GrappleConfig.getConf().grapplinghook.custom.motor.motoracceleration;
			case "playermovementmult" -> GrappleConfig.getConf().grapplinghook.custom.swing.playermovementmult;
			case "repelforce" -> GrappleConfig.getConf().grapplinghook.custom.forcefield.repelforce;
			case "attractradius" -> GrappleConfig.getConf().grapplinghook.custom.magnet.attractradius;
			case "angle" -> GrappleConfig.getConf().grapplinghook.custom.doublehook.angle;
			case "sneakingangle" -> GrappleConfig.getConf().grapplinghook.custom.doublehook.sneakingangle;
			case "verticalthrowangle" -> GrappleConfig.getConf().grapplinghook.custom.hookthrower.verticalthrowangle;
			case "sneakingverticalthrowangle" ->
					GrappleConfig.getConf().grapplinghook.custom.hookthrower.sneakingverticalthrowangle;
			case "rocket_force" -> GrappleConfig.getConf().grapplinghook.custom.rocket.rocket_force;
			case "rocket_active_time" -> GrappleConfig.getConf().grapplinghook.custom.rocket.rocket_active_time;
			case "rocket_refuel_ratio" -> GrappleConfig.getConf().grapplinghook.custom.rocket.rocket_refuel_ratio;
			case "rocket_vertical_angle" -> GrappleConfig.getConf().grapplinghook.custom.rocket.rocket_vertical_angle;
			default -> null;
		};
	}

	public CompoundTag writeNBT() {
		CompoundTag compound = new CompoundTag();
		for (String option : BOOLEAN_IDS) {
			compound.putBoolean(option, this.getBoolean(option));
		}
		for (String option : DOUBLE_IDS) {
			compound.putDouble(option, this.getDouble(option));
		}
		compound.putLong("crc32", this.getChecksum());
		return compound;
	}
	
	public void loadNBT(CompoundTag compound) {
		for (String option : BOOLEAN_IDS) {
			if (compound.contains(option)) {
				this.setBoolean(option, compound.getBoolean(option));
			}
		}
		for (String option : DOUBLE_IDS) {
			if (compound.contains(option)) {
				this.setDouble(option, compound.getDouble(option));
			}
		}
		if (compound.contains("crc32")) {
			long recordedChecksum = compound.getLong("crc32");
			if (this.getChecksum() != recordedChecksum) {
				GrappleMod.LOGGER.error("Error checksum reading from NBT");
				this.setDefaults();
			}
		}
	}
	
	public void setBoolean(String option, boolean bool) {
		if (option.equals("phaserope")) {this.phaserope = bool;}
		else if (option.equals("motor")) {this.motor = bool;}
		else if (option.equals("motorwhencrouching")) {this.motorwhencrouching = bool;}
		else if (option.equals("motorwhennotcrouching")) {this.motorwhennotcrouching = bool;}
		else if (option.equals("smartmotor")) {this.smartmotor = bool;}
		else if (option.equals("enderstaff")) {this.enderstaff = bool;}
		else if (option.equals("repel")) {this.repel = bool;}
		else if (option.equals("attract")) {this.attract = bool;}
		else if (option.equals("doublehook")) {this.doublehook = bool;}
		else if (option.equals("smartdoublemotor")) {this.smartdoublemotor = bool;}
		else if (option.equals("motordampener")) {this.motordampener = bool;}
		else if (option.equals("reelin")) {this.reelin = bool;}
		else if (option.equals("pullbackwards")) {this.pullbackwards = bool;}
		else if (option.equals("oneropepull")) {this.oneropepull = bool;}
		else if (option.equals("sticky")) {this.sticky = bool;}
		else if (option.equals("detachonkeyrelease")) {this.detachonkeyrelease = bool;}
		else if (option.equals("rocket")) {this.rocket = bool;}
		else {System.out.println("Option doesn't exist: " + option);}
	}
	
	public boolean getBoolean(String option) {
		return switch (option) {
			case "phaserope" -> this.phaserope;
			case "motor" -> this.motor;
			case "motorwhencrouching" -> this.motorwhencrouching;
			case "motorwhennotcrouching" -> this.motorwhennotcrouching;
			case "smartmotor" -> this.smartmotor;
			case "enderstaff" -> this.enderstaff;
			case "repel" -> this.repel;
			case "attract" -> this.attract;
			case "doublehook" -> this.doublehook;
			case "smartdoublemotor" -> this.smartdoublemotor;
			case "motordampener" -> this.motordampener;
			case "reelin" -> this.reelin;
			case "pullbackwards" -> this.pullbackwards;
			case "oneropepull" -> this.oneropepull;
			case "sticky" -> this.sticky;
			case "detachonkeyrelease" -> this.detachonkeyrelease;
			case "rocket" -> this.rocket;
			default -> {
				System.out.println("Option doesn't exist: " + option);
				yield false;
			}
		};
	}
	
	public void setDouble(String option, double d) {
		switch (option) {
			case "maxlen" -> this.maxlen = d;
			case "hookgravity" -> this.hookgravity = d;
			case "throwspeed" -> this.throwspeed = d;
			case "motormaxspeed" -> this.motormaxspeed = d;
			case "motoracceleration" -> this.motoracceleration = d;
			case "playermovementmult" -> this.playermovementmult = d;
			case "repelforce" -> this.repelforce = d;
			case "attractradius" -> this.attractradius = d;
			case "angle" -> this.angle = d;
			case "sneakingangle" -> this.sneakingangle = d;
			case "verticalthrowangle" -> this.verticalthrowangle = d;
			case "sneakingverticalthrowangle" -> this.sneakingverticalthrowangle = d;
			case "rocket_force" -> this.rocket_force = d;
			case "rocket_active_time" -> this.rocket_active_time = d;
			case "rocket_refuel_ratio" -> this.rocket_refuel_ratio = d;
			case "rocket_vertical_angle" -> this.rocket_vertical_angle = d;
			default -> System.out.println("Option doesn't exist: " + option);
		}
	}
	
	public double getDouble(String option) {
		return switch (option) {
			case "maxlen" -> this.maxlen;
			case "hookgravity" -> this.hookgravity;
			case "throwspeed" -> this.throwspeed;
			case "motormaxspeed" -> this.motormaxspeed;
			case "motoracceleration" -> this.motoracceleration;
			case "playermovementmult" -> this.playermovementmult;
			case "repelforce" -> this.repelforce;
			case "attractradius" -> this.attractradius;
			case "angle" -> this.angle;
			case "sneakingangle" -> this.sneakingangle;
			case "verticalthrowangle" -> this.verticalthrowangle;
			case "sneakingverticalthrowangle" -> this.sneakingverticalthrowangle;
			case "rocket_force" -> this.rocket_force;
			case "rocket_active_time" -> this.rocket_active_time;
			case "rocket_refuel_ratio" -> this.rocket_refuel_ratio;
			case "rocket_vertical_angle" -> this.rocket_vertical_angle;
			default -> {
				System.out.println("Option doesn't exist: " + option);
				yield 0;
			}
		};
	}
	
	public long getChecksum() {
		Checksum checker = new CRC32();
		for (String option : BOOLEAN_IDS) {
			checker.update(this.getBoolean(option) ? 1 : 0);
		}

		for (String option : DOUBLE_IDS) {
			// https://stackoverflow.com/questions/13071777/convert-double-to-byte-array
			checker.update(ByteBuffer.allocate(8).putDouble(this.getDouble(option)).array());
		}

		checker.update(54902349);
		return checker.getValue();
	}
	
	public void writeToBuf(ByteBuf buf) {
		for (String option : BOOLEAN_IDS) {
			buf.writeBoolean(this.getBoolean(option));
		}
		for (String option : DOUBLE_IDS) {
			buf.writeDouble(this.getDouble(option));
		}
		buf.writeLong(this.getChecksum());
	}
	
	public void readFromBuf(ByteBuf buf) {
		for (String option : BOOLEAN_IDS) {
			this.setBoolean(option, buf.readBoolean());
		}
		for (String option : DOUBLE_IDS) {
			this.setDouble(option, buf.readDouble());
		}
		long recordedChecksum = buf.readLong();
		if (this.getChecksum() != recordedChecksum) {
			GrappleMod.LOGGER.error("Error checksum reading from buffer");
			this.setDefaults();
		}
	}

	public String getName(String option) {
		return "grapplecustomization." + option;
	}
	
	public String getDescription(String option) {
		return "grapplecustomization." + option + ".desc";
	}
	
	public boolean isOptionValid(String option) {
		if (option == "motormaxspeed" || option == "motoracceleration" || option == "motorwhencrouching" || option == "motorwhennotcrouching" || option == "smartmotor" || option == "motordampener" || option == "pullbackwards") {
			return this.motor;
		}
		
		if (option == "sticky") {
			return !this.phaserope;
		}
		
		else if (option == "sneakingangle") {
			return this.doublehook && !this.reelin;
		}
		
		else if (option == "repelforce") {
			return this.repel;
		}
		
		else if (option == "attractradius") {
			return this.attract;
		}
		
		else if (option == "angle") {
			return this.doublehook;
		}
		
		else if (option == "smartdoublemotor" || option == "oneropepull") {
			return this.doublehook && this.motor;
		}
		
		else if (option == "rocket_active_time" || option == "rocket_refuel_ratio" || option == "rocket_force" || option == "rocket_vertical_angle") {
			return this.rocket;
		}
		
		return true;
	}
	
	public double getMax(String option, int upgrade) {
		GrappleConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption configoption = this.getDoubleConfig(option);
		return upgrade == 1 ? configoption.max_upgraded : configoption.max;
	}
	
	public double getMin(String option, int upgrade) {
		GrappleConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption configoption = this.getDoubleConfig(option);
		return upgrade == 1 ? configoption.min_upgraded : configoption.min;
	}
	
	public int optionEnabled(String option) {
		GrappleConfig.Config.GrapplingHook.Custom.BooleanCustomizationOption configoption = this.getBooleanConfig(option);
		if (configoption != null) {
			return configoption.enabled;
		}
		return this.getDoubleConfig(option).enabled;
	}
	
	public boolean equals(GrappleCustomization other) {
		for (String option : BOOLEAN_IDS) {
			if (this.getBoolean(option) != other.getBoolean(option)) {
				return false;
			}
		}
		for (String option : DOUBLE_IDS) {
			if (this.getDouble(option) != other.getDouble(option)) {
				return false;
			}
		}
		return true;
	}
	
	public static GrappleCustomization DEFAULT = new GrappleCustomization();
}
