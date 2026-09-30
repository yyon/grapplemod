package com.yyon.grapplemod;

import com.yyon.grapplemod.init.BlockInit;
import com.yyon.grapplemod.init.ComponentInit;
import com.yyon.grapplemod.init.EntityInit;
import com.yyon.grapplemod.init.ItemInit;

public class CommonClass {
	public static void init() {
		ComponentInit.loadClass();
		ItemInit.loadClass();
		BlockInit.loadClass();
		EntityInit.loadClass();
	}
}
