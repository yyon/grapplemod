package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class GrappleCustomization {
	public static final String[] booleanoptions = new String[] {"phaserope", "motor", "motorwhencrouching", "motorwhennotcrouching", "smartmotor", "enderstaff", "repel", "attract", "doublehook", "smartdoublemotor", "motordampener", "reelin", "pullbackwards", "oneropepull"};
	public static final String[] doubleoptions = new String[] {"maxlen", "hookgravity", "throwspeed", "motormaxspeed", "motoracceleration", "playermovementmult", "repelforce", "attractradius", "angle", "sneakingangle", "verticalthrowangle"};
	
	// rope
	public double maxlen = 30;
	public boolean phaserope = false;

	// hook thrower
	public double hookgravity = 1F;
	public double throwspeed = 2F;
	public boolean reelin = true;
	public double verticalthrowangle = 0F;

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
	public boolean oneropepull = true;
	
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
		else if (option == "oneropepull") {this.oneropepull = bool;}
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
		else if (option == "oneropepull") {return this.oneropepull;}
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
		else if (option == "verticalthrowangle") {this.verticalthrowangle = d;}
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
		else if (option == "verticalthrowangle") {return verticalthrowangle;}
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
		if (option == "maxlen") {return "Rope Length";}
		else if (option == "phaserope") {return "Phase Rope";}
		else if (option == "hookgravity") {return "Gravity on hook";}
		else if (option == "throwspeed") {return "Throw Speed";}
		else if (option == "reelin") {return "Crouch to Reel In";}
		else if (option == "verticalthrowangle") {return "Vertical Throw Angle";}
		else if (option == "motor") {return "Motor Enabled";}
		else if (option == "motormaxspeed") {return "Motor Maximum Speed";}
		else if (option == "motoracceleration") {return "Motor Acceleration";}
		else if (option == "motorwhencrouching") {return "Motor when crouching";}
		else if (option == "motorwhennotcrouching") {return "Motor when not crouching";}
		else if (option == "smartmotor") {return "Smart Motor";}
		else if (option == "motordampener") {return "Sideways Motion Dampener";}
		else if (option == "pullbackwards") {return "Pull Backwards";}
		else if (option == "playermovementmult") {return "Swing speed";}
		else if (option == "enderstaff") {return "Ender Staff";}
		else if (option == "repel") {return "Forcefield Enabled";}
		else if (option == "repelforce") {return "Repel Force";}
		else if (option == "attract") {return "Magnet Enabled";}
		else if (option == "attractradius") {return "Attraction Radius";}
		else if (option == "doublehook") {return "Double Hook";}
		else if (option == "smartdoublemotor") {return "Smart Motor";}
		else if (option == "angle") {return "Angle";}
		else if (option == "sneakingangle") {return "Angle when crouching";}
		else if (option == "oneropepull") {return "Allow pulling with one rope";}
		return "unknown option";
	}
	
	public String getDescription(String option) {
		if (option == "maxlen") {return "The length of the rope";}
		else if (option == "phaserope") {return "Allows rope to phase through blocks";}
		else if (option == "hookgravity") {return "Gravity on hook when thrown";}
		else if (option == "throwspeed") {return "Speed of hook when thrown";}
		else if (option == "reelin") {return "Before the hook is attached, crouching will stop the hook from moving farther and slowly reel it in";}
		else if (option == "verticalthrowangle") {return "Throws the grappling hook above the crosshairs by this angle";}
		else if (option == "motor") {return "Pulls player towards hook";}
		else if (option == "motormaxspeed") {return "Maximum speed of motor";}
		else if (option == "motoracceleration") {return "Acceleration of motor";}
		else if (option == "motorwhencrouching") {return "Motor is active when crouching";}
		else if (option == "motorwhennotcrouching") {return "Motor is active when crouching";}
		else if (option == "smartmotor") {return "Adjusts motor speed so that player moves towards crosshairs (up/down)";}
		else if (option == "motordampener") {return "Reduces motion perpendicular to the rope so that the rope pulls straighter";}
		else if (option == "pullbackwards") {return "Motor pulls even if you are facing the other way";}
		else if (option == "playermovementmult") {return "Acceleration of player when using movement keys while swinging";}
		else if (option == "enderstaff") {return "Left click launches player forwards";}
		else if (option == "repel") {return "Player is repelled from nearby blocks when swinging";}
		else if (option == "repelforce") {return "Force nearby blocks exert on the player";}
		else if (option == "attract") {return "Hook is attracted to nearby blocks when thrown";}
		else if (option == "attractradius") {return "Radius of attraction";}
		else if (option == "doublehook") {return "Two hooks are thrown at once";}
		else if (option == "smartdoublemotor") {return "Adjusts motor speed so that player moves towards crosshairs (left/right) when used with motor";}
		else if (option == "angle") {return "Angle that each hook is thrown from center";}
		else if (option == "sneakingangle") {return "Angle that each hook is thrown from center when crouching (don't have 'crouch to reel in' enabled if you want to use this)";}
		else if (option == "oneropepull") {return "When motor is enabled and only one hook is attached, activate the motor (if disabled, wait until both hooks are attached before pulling)";}
		return "unknown option";
	}
	
	public boolean isoptionvalid(String option) {
		if (option == "motormaxspeed" || option == "motoracceleration" || option == "motorwhencrouching" || option == "motorwhennotcrouching" || option == "smartmotor" || option == "motordampener" || option == "pullbackwards") {
			return this.motor;
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
		
		return true;
	}
}
