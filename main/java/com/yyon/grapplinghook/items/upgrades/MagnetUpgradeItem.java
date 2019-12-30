package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class MagnetUpgradeItem extends BaseUpgradeItem {
	public MagnetUpgradeItem() {
		super(BaseUpgradeItem.getproperties(false));
	}

	public void setvars() {
		unlocalizedname = "magnetupgradeitem";
		category = grapplemod.upgradeCategories.MAGNET;
	}
}
