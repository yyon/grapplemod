package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class GrappleCustomization {
	// rope
	public double maxlen = 20;
	public boolean phaserope = false;

	// hook thrower
	public double hookgravity = 0.05F;
	public double throwspeed = 2F;

	// motor
	public boolean motor = false;
	public double motormaxspeed = 4;
	public double motoracceleration = 0.2;
	public boolean motorwhencrouching = true;
	public boolean motorwhennotcrouching = true;
	public boolean smartmotor = false;
	
	// swing speed
	public double playermovementmult = 1;

	// ender staff
	public boolean enderstaff = false;

	// forcefield
	public boolean repel = false;
	public double repelforce = 1.5;
	
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
		compound.setDouble("maxlen", this.maxlen);
		compound.setBoolean("phaserope", this.phaserope);
		compound.setDouble("hookgravity", this.hookgravity);
		compound.setDouble("throwspeed", this.throwspeed);
		compound.setBoolean("motor", this.motor);
		compound.setDouble("motormaxspeed", this.motormaxspeed);
		compound.setDouble("motoracceleration", this.motoracceleration);
		compound.setBoolean("motorwhencrouching", this.motorwhencrouching);
		compound.setBoolean("motorwhennotcrouching", this.motorwhennotcrouching);
		compound.setBoolean("smartmotor", this.smartmotor);
		compound.setDouble("playermovementmult", this.playermovementmult);
		compound.setBoolean("enderstaff", this.enderstaff);
		compound.setBoolean("repel", this.repel);
		compound.setDouble("repelforce", this.repelforce);
		compound.setBoolean("attract", this.attract);
		compound.setDouble("attractradius", this.attractradius);
		compound.setBoolean("doublehook", this.doublehook);
		compound.setBoolean("smartdoublemotor", this.smartdoublemotor);
		compound.setDouble("angle", this.angle);
		compound.setDouble("sneakingangle", this.sneakingangle);
		return compound;
	}
	
	public void loadNBT(NBTTagCompound compound) {
		this.maxlen = compound.getDouble("maxlen");
		this.phaserope = compound.getBoolean("phaserope");
		this.hookgravity = compound.getDouble("hookgravity");
		this.throwspeed = compound.getDouble("throwspeed");
		this.motor = compound.getBoolean("motor");
		this.motormaxspeed = compound.getDouble("motormaxspeed");
		this.motoracceleration = compound.getDouble("motoracceleration");
		this.motorwhencrouching = compound.getBoolean("motorwhencrouching");
		this.motorwhennotcrouching = compound.getBoolean("motorwhennotcrouching");
		this.smartmotor = compound.getBoolean("smartmotor");
		this.playermovementmult = compound.getDouble("playermovementmult");
		this.enderstaff = compound.getBoolean("enderstaff");
		this.repel = compound.getBoolean("repel");
		this.repelforce = compound.getDouble("repelforce");
		this.attract = compound.getBoolean("attract");
		this.attractradius = compound.getDouble("attractradius");
		this.doublehook = compound.getBoolean("doublehook");
		this.smartdoublemotor = compound.getBoolean("smartdoublemotor");
		this.angle = compound.getDouble("angle");
		this.sneakingangle = compound.getDouble("sneakingangle");
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
	
	/*
	public GrappleCustomization copy() {
		GrappleCustomization newcustom = new GrappleCustomization();
		
		newcustom.loadNBT(this.writeNBT());
		
		return newcustom;
	}
	*/

	public void writeToBuf(ByteBuf buf) {
		buf.writeDouble(this.maxlen);
		buf.writeBoolean(this.phaserope);
		buf.writeDouble(this.hookgravity);
		buf.writeDouble(this.throwspeed);
		buf.writeBoolean(this.motor);
		buf.writeDouble(this.motormaxspeed);
		buf.writeDouble(this.motoracceleration);
		buf.writeBoolean(this.motorwhencrouching);
		buf.writeBoolean(this.motorwhennotcrouching);
		buf.writeBoolean(this.smartmotor);
		buf.writeDouble(this.playermovementmult);
		buf.writeBoolean(this.enderstaff);
		buf.writeBoolean(this.repel);
		buf.writeDouble(this.repelforce);
		buf.writeBoolean(this.attract);
		buf.writeDouble(this.attractradius);
		buf.writeBoolean(this.doublehook);
		buf.writeBoolean(this.smartdoublemotor);
		buf.writeDouble(this.angle);
		buf.writeDouble(this.sneakingangle);
	}
	
	public void readFromBuf(ByteBuf buf) {
		this.maxlen = buf.readDouble();
		this.phaserope = buf.readBoolean();
		this.hookgravity = buf.readDouble();
		this.throwspeed = buf.readDouble();
		this.motor = buf.readBoolean();
		this.motormaxspeed = buf.readDouble();
		this.motoracceleration = buf.readDouble();
		this.motorwhencrouching = buf.readBoolean();
		this.motorwhennotcrouching = buf.readBoolean();
		this.smartmotor = buf.readBoolean();
		this.playermovementmult = buf.readDouble();
		this.enderstaff = buf.readBoolean();
		this.repel = buf.readBoolean();
		this.repelforce = buf.readDouble();
		this.attract = buf.readBoolean();
		this.attractradius = buf.readDouble();
		this.doublehook = buf.readBoolean();
		this.smartdoublemotor = buf.readBoolean();
		this.angle = buf.readDouble();
		this.sneakingangle = buf.readDouble();
	}
}
