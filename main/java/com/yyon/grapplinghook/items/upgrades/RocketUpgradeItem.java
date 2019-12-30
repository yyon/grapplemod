package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class RocketUpgradeItem extends BaseUpgradeItem {
	public RocketUpgradeItem() {
		super(BaseUpgradeItem.getproperties(false));
	}

	public void setvars() {
		unlocalizedname = "rocketupgradeitem";
		category = grapplemod.upgradeCategories.ROCKET;
	}
}
