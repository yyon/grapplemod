package com.yyon.grapplinghook.items.alternategrapple;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.items.grappleBow;

public class RocketHook extends grappleBow {
	public RocketHook() {
		super();
	}
	
	@Override
    public GrappleCustomization getDefaultCustomization() {
    	GrappleCustomization custom = new GrappleCustomization();
    	custom.rocket = true;
    	// custom.rocket_vertical_angle = 30;

    	custom.maxlen = GrappleConfig.options.upgraded_maxlen;
    	custom.throwspeed = GrappleConfig.options.upgraded_throwspeed;
    	
    	return custom;
    }
}
