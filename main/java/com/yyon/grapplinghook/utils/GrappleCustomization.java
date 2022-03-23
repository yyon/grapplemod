package com.yyon.grapplinghook.utils;

import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

public class GrappleCustomization {
	public static final String[] booleanoptions = new String[] {"phaserope", "motor", "motorwhencrouching", "motorwhennotcrouching", "smartmotor", "enderstaff", "repel", "attract", "doublehook", "smartdoublemotor", "motordampener", "reelin", "pullbackwards", "oneropepull", "sticky", "detachonkeyrelease", "rocket"};
	public static final String[] doubleoptions = new String[] {"maxlen", "hookgravity", "throwspeed", "motormaxspeed", "motoracceleration", "playermovementmult", "repelforce", "attractradius", "angle", "sneakingangle", "verticalthrowangle", "sneakingverticalthrowangle", "rocket_force", "rocket_active_time", "rocket_refuel_ratio", "rocket_vertical_angle"};
	
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
	
	public enum upgradeCategories {
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
		
		private String nameUnlocalized;
		private upgradeCategories(String name) {
			this.nameUnlocalized = name;
		}
		
		public String getName() {
			if (ClientProxyInterface.proxy != null) {
				return ClientProxyInterface.proxy.localize("grapplemod.upgradecategories." + this.nameUnlocalized);
			} else {
				return nameUnlocalized;
			}
		}
		
		public static upgradeCategories fromInt(int i) {
			return upgradeCategories.values()[i];
		}
		public int toInt() {
			for (int i = 0; i < size(); i++) {
				if (upgradeCategories.values()[i] == this) {
					return i;
				}
			}
			return -1;
		}
		public static int size() {
			return upgradeCategories.values().length;
		}
		public Item getItem() {
			if (this == upgradeCategories.ROPE) {
				return CommonSetup.ropeUpgradeItem;
			} else if (this == upgradeCategories.THROW) {
				return CommonSetup.throwUpgradeItem;
			} else if (this == upgradeCategories.MOTOR) {
				return CommonSetup.motorUpgradeItem;
			} else if (this == upgradeCategories.SWING) {
				return CommonSetup.swingUpgradeItem;
			} else if (this == upgradeCategories.STAFF) {
				return CommonSetup.staffUpgradeItem;
			} else if (this == upgradeCategories.FORCEFIELD) {
				return CommonSetup.forcefieldUpgradeItem;
			} else if (this == upgradeCategories.MAGNET) {
				return CommonSetup.magnetUpgradeItem;
			} else if (this == upgradeCategories.DOUBLE) {
				return CommonSetup.doubleUpgradeItem;
			} else if (this == upgradeCategories.LIMITS) {
				return CommonSetup.limitsUpgradeItem;
			} else if (this == upgradeCategories.ROCKET) {
				return CommonSetup.rocketUpgradeItem;
			}
			return null;
		}
	};
	
	public GrappleCustomization() {
		for (String option : booleanoptions) {
			GrappleConfig.Config.GrapplingHook.Custom.BooleanCustomizationOption optionconfig = getBooleanConfig(option);
			this.setBoolean(option, optionconfig.default_value);
		}
		for (String option : doubleoptions) {
			GrappleConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption optionconfig = getDoubleConfig(option);
			this.setDouble(option, optionconfig.default_value);
		}
	}
	
	public GrappleConfig.Config.GrapplingHook.Custom.BooleanCustomizationOption getBooleanConfig(String option) {
		if (option.equals("phaserope")) {return GrappleConfig.getConf().grapplinghook.custom.rope.phaserope;}
		else if (option.equals("motor")) {return GrappleConfig.getConf().grapplinghook.custom.motor.motor;}
		else if (option.equals("motorwhencrouching")) {return GrappleConfig.getConf().grapplinghook.custom.motor.motorwhencrouching;}
		else if (option.equals("motorwhennotcrouching")) {return GrappleConfig.getConf().grapplinghook.custom.motor.motorwhennotcrouching;}
		else if (option.equals("smartmotor")) {return GrappleConfig.getConf().grapplinghook.custom.motor.smartmotor;}
		else if (option.equals("enderstaff")) {return GrappleConfig.getConf().grapplinghook.custom.enderstaff.enderstaff;}
		else if (option.equals("repel")) {return GrappleConfig.getConf().grapplinghook.custom.forcefield.repel;}
		else if (option.equals("attract")) {return GrappleConfig.getConf().grapplinghook.custom.magnet.attract;}
		else if (option.equals("doublehook")) {return GrappleConfig.getConf().grapplinghook.custom.doublehook.doublehook;}
		else if (option.equals("smartdoublemotor")) {return GrappleConfig.getConf().grapplinghook.custom.doublehook.smartdoublemotor;}
		else if (option.equals("motordampener")) {return GrappleConfig.getConf().grapplinghook.custom.motor.motordampener;}
		else if (option.equals("reelin")) {return GrappleConfig.getConf().grapplinghook.custom.hookthrower.reelin;}
		else if (option.equals("pullbackwards")) {return GrappleConfig.getConf().grapplinghook.custom.motor.pullbackwards;}
		else if (option.equals("oneropepull")) {return GrappleConfig.getConf().grapplinghook.custom.doublehook.oneropepull;}
		else if (option.equals("sticky")) {return GrappleConfig.getConf().grapplinghook.custom.rope.sticky;}
		else if (option.equals("detachonkeyrelease")) {return GrappleConfig.getConf().grapplinghook.custom.hookthrower.detachonkeyrelease;}
		else if (option.equals("rocket")) {return GrappleConfig.getConf().grapplinghook.custom.rocket.rocketenabled;}
		return null;
	}

	public GrappleConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption getDoubleConfig(String option) {
		if (option.equals("maxlen")) {return GrappleConfig.getConf().grapplinghook.custom.rope.maxlen;}
		else if (option.equals("hookgravity")) {return GrappleConfig.getConf().grapplinghook.custom.hookthrower.hookgravity;}
		else if (option.equals("throwspeed")) {return GrappleConfig.getConf().grapplinghook.custom.hookthrower.throwspeed;}
		else if (option.equals("motormaxspeed")) {return GrappleConfig.getConf().grapplinghook.custom.motor.motormaxspeed;}
		else if (option.equals("motoracceleration")) {return GrappleConfig.getConf().grapplinghook.custom.motor.motoracceleration;}
		else if (option.equals("playermovementmult")) {return GrappleConfig.getConf().grapplinghook.custom.swing.playermovementmult;}
		else if (option.equals("repelforce")) {return GrappleConfig.getConf().grapplinghook.custom.forcefield.repelforce;}
		else if (option.equals("attractradius")) {return GrappleConfig.getConf().grapplinghook.custom.magnet.attractradius;}
		else if (option.equals("angle")) {return GrappleConfig.getConf().grapplinghook.custom.doublehook.angle;}
		else if (option.equals("sneakingangle")) {return GrappleConfig.getConf().grapplinghook.custom.doublehook.sneakingangle;}
		else if (option.equals("verticalthrowangle")) {return GrappleConfig.getConf().grapplinghook.custom.hookthrower.verticalthrowangle;}
		else if (option.equals("sneakingverticalthrowangle")) {return GrappleConfig.getConf().grapplinghook.custom.hookthrower.sneakingverticalthrowangle;}
		else if (option.equals("rocket_force")) {return GrappleConfig.getConf().grapplinghook.custom.rocket.rocket_force;}
		else if (option.equals("rocket_active_time")) {return GrappleConfig.getConf().grapplinghook.custom.rocket.rocket_active_time;}
		else if (option.equals("rocket_refuel_ratio")) {return GrappleConfig.getConf().grapplinghook.custom.rocket.rocket_refuel_ratio;}
		else if (option.equals("rocket_vertical_angle")) {return GrappleConfig.getConf().grapplinghook.custom.rocket.rocket_vertical_angle;}
		return null;
	}

	public CompoundTag writeNBT() {
		CompoundTag compound = new CompoundTag();
		for (String option : booleanoptions) {
			compound.putBoolean(option, this.getBoolean(option));
		}
		for (String option : doubleoptions) {
			compound.putDouble(option, this.getDouble(option));
		}
		return compound;
	}
	
	public void loadNBT(CompoundTag compound) {
		for (String option : booleanoptions) {
			if (compound.contains(option)) {
				this.setBoolean(option, compound.getBoolean(option));
			}
		}
		for (String option : doubleoptions) {
			if (compound.contains(option)) {
				this.setDouble(option, compound.getDouble(option));
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
		if (option.equals("phaserope")) {return this.phaserope;}
		else if (option.equals("motor")) {return this.motor;}
		else if (option.equals("motorwhencrouching")) {return this.motorwhencrouching;}
		else if (option.equals("motorwhennotcrouching")) {return this.motorwhennotcrouching;}
		else if (option.equals("smartmotor")) {return this.smartmotor;}
		else if (option.equals("enderstaff")) {return this.enderstaff;}
		else if (option.equals("repel")) {return this.repel;}
		else if (option.equals("attract")) {return this.attract;}
		else if (option.equals("doublehook")) {return this.doublehook;}
		else if (option.equals("smartdoublemotor")) {return this.smartdoublemotor;}
		else if (option.equals("motordampener")) {return this.motordampener;}
		else if (option.equals("reelin")) {return this.reelin;}
		else if (option.equals("pullbackwards")) {return this.pullbackwards;}
		else if (option.equals("oneropepull")) {return this.oneropepull;}
		else if (option.equals("sticky")) {return this.sticky;}
		else if (option.equals("detachonkeyrelease")) {return this.detachonkeyrelease;}
		else if (option.equals("rocket")) {return this.rocket;}
		System.out.println("Option doesn't exist: " + option);
		return false;
	}
	
	public void setDouble(String option, double d) {
		if (option.equals("maxlen")) {this.maxlen = d;}
		else if (option.equals("hookgravity")) {this.hookgravity = d;}
		else if (option.equals("throwspeed")) {this.throwspeed = d;}
		else if (option.equals("motormaxspeed")) {this.motormaxspeed = d;}
		else if (option.equals("motoracceleration")) {this.motoracceleration = d;}
		else if (option.equals("playermovementmult")) {this.playermovementmult = d;}
		else if (option.equals("repelforce")) {this.repelforce = d;}
		else if (option.equals("attractradius")) {this.attractradius = d;}
		else if (option.equals("angle")) {this.angle = d;}
		else if (option.equals("sneakingangle")) {this.sneakingangle = d;}
		else if (option.equals("verticalthrowangle")) {this.verticalthrowangle = d;}
		else if (option.equals("sneakingverticalthrowangle")) {this.sneakingverticalthrowangle = d;}
		else if (option.equals("rocket_force")) {this.rocket_force = d;}
		else if (option.equals("rocket_active_time")) {this.rocket_active_time = d;}
		else if (option.equals("rocket_refuel_ratio")) {this.rocket_refuel_ratio = d;}
		else if (option.equals("rocket_vertical_angle")) {this.rocket_vertical_angle = d;}
		else {System.out.println("Option doesn't exist: " + option);}
	}
	
	public double getDouble(String option) {
		if (option.equals("maxlen")) {return maxlen;}
		else if (option.equals("hookgravity")) {return hookgravity;}
		else if (option.equals("throwspeed")) {return throwspeed;}
		else if (option.equals("motormaxspeed")) {return motormaxspeed;}
		else if (option.equals("motoracceleration")) {return motoracceleration;}
		else if (option.equals("playermovementmult")) {return playermovementmult;}
		else if (option.equals("repelforce")) {return repelforce;}
		else if (option.equals("attractradius")) {return attractradius;}
		else if (option.equals("angle")) {return angle;}
		else if (option.equals("sneakingangle")) {return sneakingangle;}
		else if (option.equals("verticalthrowangle")) {return verticalthrowangle;}
		else if (option.equals("sneakingverticalthrowangle")) {return sneakingverticalthrowangle;}
		else if (option.equals("rocket_force")) {return this.rocket_force;}
		else if (option.equals("rocket_active_time")) {return rocket_active_time;}
		else if (option.equals("rocket_refuel_ratio")) {return rocket_refuel_ratio;}
		else if (option.equals("rocket_vertical_angle")) {return rocket_vertical_angle;}
		System.out.println("Option doesn't exist: " + option);
		return 0;
	}
	
	public void writeToBuf(ByteBuf buf) {
		for (String option : booleanoptions) {
			buf.writeBoolean(this.getBoolean(option));
		}
		for (String option : doubleoptions) {
			buf.writeDouble(this.getDouble(option));
		}
	}
	
	public void readFromBuf(ByteBuf buf) {
		for (String option : booleanoptions) {
			this.setBoolean(option, buf.readBoolean());
		}
		for (String option : doubleoptions) {
			this.setDouble(option, buf.readDouble());
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
		for (String option : booleanoptions) {
			if (this.getBoolean(option) != other.getBoolean(option)) {
				return false;
			}
		}
		for (String option : doubleoptions) {
			if (this.getDouble(option) != other.getDouble(option)) {
				return false;
			}
		}
		return true;
	}
	
	public static GrappleCustomization DEFAULT = new GrappleCustomization();
}
