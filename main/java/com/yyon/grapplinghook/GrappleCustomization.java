package com.yyon.grapplinghook;

public class GrappleCustomization {
	// rope
	public int maxlen = 20;
	public boolean phaserope = false;

	// hook thrower
	public float hookgravity = 0.05F;
	public float throwspeed = 2F;

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
	public int attractradius = 3;
	
	// double hook
	public boolean doublehook = false;
	public boolean smartdoublemotor = true;
	
	public GrappleCustomization() {
		
	}
	
	
}
