package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class GrappleCustomization {
	public static final String[] booleanoptions = new String[] {"phaserope", "motor", "motorwhencrouching", "motorwhennotcrouching", "smartmotor", "enderstaff", "repel", "attract", "doublehook", "smartdoublemotor", "motordampener", "reelin", "pullbackwards"};
	public static final String[] doubleoptions = new String[] {"maxlen", "hookgravity", "throwspeed", "motormaxspeed", "motoracceleration", "playermovementmult", "repelforce", "attractradius", "angle", "sneakingangle"};
	
	// rope
	public double maxlen = 30;
	public boolean phaserope = false;

	// hook thrower
	public double hookgravity = 0.05F;
	public double throwspeed = 2F;
	public boolean reelin = true;

	// motor
	public boolean motor = false;
	public double motormaxspeed = 4;
	public double motoracceleration = 0.2;
	public boolean motorwhencrouching = true;
	public boolean motorwhennotcrouching = true;
	public boolean smartmotor = false;
	public boolean motordampener = false;
	public boolean pullbackwards = true;
	
	// swing speed
	public double playermovementmult = 1;

	// ender staff
	public boolean enderstaff = false;

	// forcefield
	public boolean repel = false;
	public double repelforce = 1;
	
	// hook magnet
	public boolean attract = false;
	public double attractradius = 3;
	
	// double hook
	public boolean doublehook = false;
	public boolean smartdoublemotor = true;
	public double angle = 20;
	public double sneakingangle = 10;
	
	public GrappleCustomization() {
		
	}
	
	public NBTTagCompound writeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		for (String option : booleanoptions) {
			compound.setBoolean(option, this.getBoolean(option));
		}
		for (String option : doubleoptions) {
			compound.setDouble(option, this.getDouble(option));
		}
		return compound;
	}
	
	public void loadNBT(NBTTagCompound compound) {
		for (String option : booleanoptions) {
			this.setBoolean(option, compound.getBoolean(option));
		}
		for (String option : doubleoptions) {
			this.setDouble(option, compound.getDouble(option));
		}
	}
	
	public void setBoolean(String option, boolean bool) {
		if (option == "phaserope") {this.phaserope = bool;}
		else if (option == "motor") {this.motor = bool;}
		else if (option == "motorwhencrouching") {this.motorwhencrouching = bool;}
		else if (option == "motorwhennotcrouching") {this.motorwhennotcrouching = bool;}
		else if (option == "smartmotor") {this.smartmotor = bool;}
		else if (option == "enderstaff") {this.enderstaff = bool;}
		else if (option == "repel") {this.repel = bool;}
		else if (option == "attract") {this.attract = bool;}
		else if (option == "doublehook") {this.doublehook = bool;}
		else if (option == "smartdoublemotor") {this.smartdoublemotor = bool;}
		else if (option == "motordampener") {this.motordampener = bool;}
		else if (option == "reelin") {this.reelin = bool;}
		else if (option == "pullbackwards") {this.pullbackwards = bool;}
		
	}
	
	public boolean getBoolean(String option) {
		if (option == "phaserope") {return this.phaserope;}
		else if (option == "motor") {return this.motor;}
		else if (option == "motorwhencrouching") {return this.motorwhencrouching;}
		else if (option == "motorwhennotcrouching") {return this.motorwhennotcrouching;}
		else if (option == "smartmotor") {return this.smartmotor;}
		else if (option == "enderstaff") {return this.enderstaff;}
		else if (option == "repel") {return this.repel;}
		else if (option == "attract") {return this.attract;}
		else if (option == "doublehook") {return this.doublehook;}
		else if (option == "smartdoublemotor") {return this.smartdoublemotor;}
		else if (option == "motordampener") {return this.motordampener;}
		else if (option == "reelin") {return this.reelin;}
		else if (option == "pullbackwards") {return this.pullbackwards;}
		System.out.println("Option doesn't exist: " + option);
		return false;
	}
	
	public void setDouble(String option, double d) {
		if (option == "maxlen") {this.maxlen = d;}
		else if (option == "hookgravity") {this.hookgravity = d;}
		else if (option == "throwspeed") {this.throwspeed = d;}
		else if (option == "motormaxspeed") {this.motormaxspeed = d;}
		else if (option == "motoracceleration") {this.motoracceleration = d;}
		else if (option == "playermovementmult") {this.playermovementmult = d;}
		else if (option == "repelforce") {this.repelforce = d;}
		else if (option == "attractradius") {this.attractradius = d;}
		else if (option == "angle") {this.angle = d;}
		else if (option == "sneakingangle") {this.sneakingangle = d;}
	}
	
	public double getDouble(String option) {
		if (option == "maxlen") {return maxlen;}
		else if (option == "hookgravity") {return hookgravity;}
		else if (option == "throwspeed") {return throwspeed;}
		else if (option == "motormaxspeed") {return motormaxspeed;}
		else if (option == "motoracceleration") {return motoracceleration;}
		else if (option == "playermovementmult") {return playermovementmult;}
		else if (option == "repelforce") {return repelforce;}
		else if (option == "attractradius") {return attractradius;}
		else if (option == "angle") {return angle;}
		else if (option == "sneakingangle") {return sneakingangle;}
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
}
