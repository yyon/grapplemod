package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class RopeUpgradeItem extends BaseUpgradeItem {
	public RopeUpgradeItem() {
		super(BaseUpgradeItem.getproperties(false));
	}

	public void setvars() {
		unlocalizedname = "ropeupgradeitem";
		category = grapplemod.upgradeCategories.ROPE;
	}
}
