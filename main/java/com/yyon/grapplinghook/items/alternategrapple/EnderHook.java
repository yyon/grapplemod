package com.yyon.grapplinghook.items.alternategrapple;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.items.grappleBow;

public class EnderHook extends grappleBow {
	public EnderHook() {
		super();
	}
	
	@Override
    public GrappleCustomization getDefaultCustomization() {
    	GrappleCustomization custom = new GrappleCustomization();
    	custom.enderstaff = true;

    	custom.maxlen = GrappleConfig.options.upgraded_maxlen;
    	custom.throwspeed = GrappleConfig.options.upgraded_throwspeed;
    	
    	return custom;
    }
}
