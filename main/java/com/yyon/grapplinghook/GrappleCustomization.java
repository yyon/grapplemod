package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import java.nio.ByteBuffer;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

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
	
	public GrappleCustomization() {
		this.setDefaults();
	}

	public void setDefaults() {	
		// rope
		this.maxlen = GrappleConfig.getconf().default_maxlen;
		this.phaserope = GrappleConfig.getconf().default_phaserope;
		this.sticky = GrappleConfig.getconf().default_sticky;

		// hook thrower
		this.hookgravity = GrappleConfig.getconf().default_hookgravity;
		this.throwspeed = GrappleConfig.getconf().default_throwspeed;
		this.reelin = GrappleConfig.getconf().default_reelin;
		this.verticalthrowangle = GrappleConfig.getconf().default_verticalthrowangle;
		this.sneakingverticalthrowangle = GrappleConfig.getconf().default_sneakingverticalthrowangle;
		this.detachonkeyrelease = GrappleConfig.getconf().default_detachonkeyrelease;

		// motor
		this.motor = GrappleConfig.getconf().default_motor;
		this.motormaxspeed = GrappleConfig.getconf().default_motormaxspeed;
		this.motoracceleration = GrappleConfig.getconf().default_motoracceleration;
		this.motorwhencrouching = GrappleConfig.getconf().default_motorwhencrouching;
		this.motorwhennotcrouching = GrappleConfig.getconf().default_motorwhennotcrouching;
		this.smartmotor = GrappleConfig.getconf().default_smartmotor;
		this.motordampener = GrappleConfig.getconf().default_motordampener;
		this.pullbackwards = GrappleConfig.getconf().default_pullbackwards;
		
		// swing speed
		this.playermovementmult = GrappleConfig.getconf().default_playermovementmult;

		// ender staff
		this.enderstaff = GrappleConfig.getconf().default_enderstaff;

		// forcefield
		this.repel = GrappleConfig.getconf().default_repel;
		this.repelforce = GrappleConfig.getconf().default_repelforce;
		
		// hook magnet
		this.attract = GrappleConfig.getconf().default_attract;
		this.attractradius = GrappleConfig.getconf().default_attractradius;
		
		// double hook
		this.doublehook = GrappleConfig.getconf().default_doublehook;
		this.smartdoublemotor = GrappleConfig.getconf().default_smartdoublemotor;
		this.angle = GrappleConfig.getconf().default_angle;
		this.sneakingangle = GrappleConfig.getconf().default_sneakingangle;
		this.oneropepull = GrappleConfig.getconf().default_oneropepull;
		
		// rocket
		this.rocket = GrappleConfig.getconf().default_rocketenabled;
		this.rocket_force = GrappleConfig.getconf().default_rocket_force;
		this.rocket_active_time = GrappleConfig.getconf().default_rocket_active_time;
		this.rocket_refuel_ratio = GrappleConfig.getconf().default_rocket_refuel_ratio;
		this.rocket_vertical_angle = GrappleConfig.getconf().default_rocket_vertical_angle;
	}
	
	public NBTTagCompound writeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		for (String option : booleanoptions) {
			compound.setBoolean(option, this.getBoolean(option));
		}
		for (String option : doubleoptions) {
			compound.setDouble(option, this.getDouble(option));
		}
 		compound.setLong("crc32", this.getChecksum());
		return compound;
	}
	
	public void loadNBT(NBTTagCompound compound) {
		for (String option : booleanoptions) {
			if (compound.hasKey(option)) {
				this.setBoolean(option, compound.getBoolean(option));
			}
		}
		for (String option : doubleoptions) {
			if (compound.hasKey(option)) {
				this.setDouble(option, compound.getDouble(option));
			}
		}
		if (compound.hasKey("crc32")) {
			long recordedChecksum = compound.getLong("crc32");
			if (this.getChecksum() != recordedChecksum) {
				System.out.println("Error checksum reading from NBT");
				this.setDefaults();
			}
		}
	}
	

	public long getChecksum() {
		Checksum checker = new CRC32();
		for (String option : booleanoptions) {
			checker.update(this.getBoolean(option) ? 1 : 0);
		}
		for (String option : doubleoptions) {
			// https://stackoverflow.com/questions/13071777/convert-double-to-byte-array
			byte[] longBytes = ByteBuffer.allocate(8).putDouble(this.getDouble(option)).array();
			checker.update(longBytes, 0, longBytes.length);
		}
		checker.update(54902349);
		return checker.getValue();
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
 		buf.writeLong(this.getChecksum());
 	}
	
	public void readFromBuf(ByteBuf buf) {
		for (String option : booleanoptions) {
			this.setBoolean(option, buf.readBoolean());
		}
		for (String option : doubleoptions) {
			this.setDouble(option, buf.readDouble());
		}
		long recordedChecksum = buf.readLong();
		if (this.getChecksum() != recordedChecksum) {
			System.out.println("Error checksum reading from buffer");
			this.setDefaults();
		}
	}

	public String getName(String option) {
		return "grapplecustomization." + option + ".name";
	}
	
	public String getDescription(String option) {
		return "grapplecustomization." + option + ".desc";
	}
	
	public boolean isoptionvalid(String option) {
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
		if (option.equals("maxlen")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_maxlen : GrappleConfig.getconf().max_maxlen;}
		else if (option.equals("hookgravity")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_hookgravity : GrappleConfig.getconf().max_hookgravity;}
		else if (option.equals("throwspeed")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_throwspeed : GrappleConfig.getconf().max_throwspeed;}
		else if (option.equals("motormaxspeed")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_motormaxspeed : GrappleConfig.getconf().max_motormaxspeed;}
		else if (option.equals("motoracceleration")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_motoracceleration : GrappleConfig.getconf().max_motoracceleration;}
		else if (option.equals("playermovementmult")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_playermovementmult : GrappleConfig.getconf().max_playermovementmult;}
		else if (option.equals("repelforce")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_repelforce : GrappleConfig.getconf().max_repelforce;}
		else if (option.equals("attractradius")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_attractradius : GrappleConfig.getconf().max_attractradius;}
		else if (option.equals("angle")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_angle : GrappleConfig.getconf().max_angle;}
		else if (option.equals("sneakingangle")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_sneakingangle : GrappleConfig.getconf().max_sneakingangle;}
		else if (option.equals("verticalthrowangle")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_verticalthrowangle : GrappleConfig.getconf().max_verticalthrowangle;}
		else if (option.equals("sneakingverticalthrowangle")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_sneakingverticalthrowangle : GrappleConfig.getconf().max_sneakingverticalthrowangle;}
		else if (option.equals("rocket_force")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_rocket_force: GrappleConfig.getconf().max_rocket_force;}
		else if (option.equals("rocket_active_time")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_rocket_active_time : GrappleConfig.getconf().max_rocket_active_time;}
		else if (option.equals("rocket_refuel_ratio")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_rocket_refuel_ratio : GrappleConfig.getconf().max_rocket_refuel_ratio;}
		else if (option.equals("rocket_vertical_angle")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_rocket_vertical_angle : GrappleConfig.getconf().max_rocket_vertical_angle;}
		System.out.println("Option doesn't exist: " + option);
		return 0;
	}
	
	public double getMin(String option, int upgrade) {
		if (option.equals("hookgravity")) {return upgrade == 1 ? GrappleConfig.getconf().min_upgrade_hookgravity : GrappleConfig.getconf().min_hookgravity;}
		if (option.equals("rocket_refuel_ratio")) {return upgrade == 1 ? GrappleConfig.getconf().min_upgrade_rocket_refuel_ratio : GrappleConfig.getconf().min_rocket_refuel_ratio;}
		
		return 0;
	}
	
	public int optionEnabled(String option) {
		if (option.equals("maxlen")) {return GrappleConfig.getconf().enable_maxlen;}
		else if (option.equals("phaserope")) {return GrappleConfig.getconf().enable_phaserope;}
		else if (option.equals("hookgravity")) {return GrappleConfig.getconf().enable_hookgravity;}
		else if (option.equals("throwspeed")) {return GrappleConfig.getconf().enable_throwspeed;}
		else if (option.equals("reelin")) {return GrappleConfig.getconf().enable_reelin;}
		else if (option.equals("verticalthrowangle")) {return GrappleConfig.getconf().enable_verticalthrowangle;}
		else if (option.equals("motor")) {return GrappleConfig.getconf().enable_motor;}
		else if (option.equals("motormaxspeed")) {return GrappleConfig.getconf().enable_motormaxspeed;}
		else if (option.equals("motoracceleration")) {return GrappleConfig.getconf().enable_motoracceleration;}
		else if (option.equals("motorwhencrouching")) {return GrappleConfig.getconf().enable_motorwhencrouching;}
		else if (option.equals("motorwhennotcrouching")) {return GrappleConfig.getconf().enable_motorwhennotcrouching;}
		else if (option.equals("smartmotor")) {return GrappleConfig.getconf().enable_smartmotor;}
		else if (option.equals("motordampener")) {return GrappleConfig.getconf().enable_motordampener;}
		else if (option.equals("pullbackwards")) {return GrappleConfig.getconf().enable_pullbackwards;}
		else if (option.equals("playermovementmult")) {return GrappleConfig.getconf().enable_playermovementmult;}
		else if (option.equals("enderstaff")) {return GrappleConfig.getconf().enable_enderstaff;}
		else if (option.equals("repel")) {return GrappleConfig.getconf().enable_repel;}
		else if (option.equals("repelforce")) {return GrappleConfig.getconf().enable_repelforce;}
		else if (option.equals("attract")) {return GrappleConfig.getconf().enable_attract;}
		else if (option.equals("attractradius")) {return GrappleConfig.getconf().enable_attractradius;}
		else if (option.equals("doublehook")) {return GrappleConfig.getconf().enable_doublehook;}
		else if (option.equals("smartdoublemotor")) {return GrappleConfig.getconf().enable_smartdoublemotor;}
		else if (option.equals("angle")) {return GrappleConfig.getconf().enable_angle;}
		else if (option.equals("sneakingangle")) {return GrappleConfig.getconf().enable_sneakingangle;}
		else if (option.equals("oneropepull")) {return GrappleConfig.getconf().enable_oneropepull;}
		else if (option.equals("sneakingverticalthrowangle")) {return GrappleConfig.getconf().enable_sneakingverticalthrowangle;}
		else if (option.equals("sticky")) {return GrappleConfig.getconf().enable_sticky;}
		else if (option.equals("detachonkeyrelease")) {return GrappleConfig.getconf().enable_detachonkeyrelease;}
		else if (option.equals("rocket")) {return GrappleConfig.getconf().enable_rocket;}
		else if (option.equals("rocket_force")) {return GrappleConfig.getconf().enable_rocket_force;}
		else if (option.equals("rocket_active_time")) {return GrappleConfig.getconf().enable_rocket_active_time;}
		else if (option.equals("rocket_refuel_ratio")) {return GrappleConfig.getconf().enable_rocket_refuel_ratio;}
		else if (option.equals("rocket_vertical_angle")) {return GrappleConfig.getconf().enable_rocket_vertical_angle;}
		System.out.println("Unknown option");
		return 0;
	}
}
