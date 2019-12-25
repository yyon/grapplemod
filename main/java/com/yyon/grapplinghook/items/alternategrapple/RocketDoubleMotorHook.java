package com.yyon.grapplinghook.items.alternategrapple;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.items.grappleBow;

public class RocketDoubleMotorHook extends grappleBow {
	public RocketDoubleMotorHook() {
		super();
	}
	
	@Override
    public GrappleCustomization getDefaultCustomization() {
    	GrappleCustomization custom = new GrappleCustomization();
    	custom.doublehook = true;
    	custom.motor = true;
    	custom.reelin = false;
    	custom.sticky = true;
    	
    	custom.hookgravity = 20;
    	custom.verticalthrowangle = 30;
    	custom.reelin = false;
    	
    	custom.motorwhencrouching = true;
    	custom.smartdoublemotor = true;
    	
    	custom.angle = 25;
    	custom.sneakingangle = 0;
    	
    	custom.rocket = true;
    	
    	custom.maxlen = GrappleConfig.options.upgraded_maxlen;
    	custom.throwspeed = 10;
    	
    	return custom;
    }
}
