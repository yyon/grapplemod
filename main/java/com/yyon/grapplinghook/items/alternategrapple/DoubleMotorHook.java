package com.yyon.grapplinghook.items.alternategrapple;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.items.grappleBow;

public class DoubleMotorHook extends grappleBow {
	public DoubleMotorHook() {
		super();
	}
	
	@Override
    public GrappleCustomization getDefaultCustomization() {
    	GrappleCustomization custom = new GrappleCustomization();
    	custom.doublehook = true;
    	custom.motor = true;
    	custom.reelin = false;
    	custom.sticky = true;
    	
    	custom.maxlen = GrappleConfig.options.upgraded_maxlen;
    	custom.throwspeed = GrappleConfig.options.upgraded_throwspeed;
    	
    	return custom;
    }
}
