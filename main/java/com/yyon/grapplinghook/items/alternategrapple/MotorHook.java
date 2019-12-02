package com.yyon.grapplinghook.items.alternategrapple;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.items.grappleBow;

public class MotorHook extends grappleBow {
	public MotorHook() {
		super();
	}
	
	@Override
    public GrappleCustomization getDefaultCustomization() {
    	GrappleCustomization custom = new GrappleCustomization();
    	custom.motor = true;

    	custom.maxlen = GrappleConfig.options.upgraded_maxlen;
    	custom.throwspeed = GrappleConfig.options.upgraded_throwspeed;
    	
    	return custom;
    }
}
