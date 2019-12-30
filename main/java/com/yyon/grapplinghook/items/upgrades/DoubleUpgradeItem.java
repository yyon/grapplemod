package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class DoubleUpgradeItem extends BaseUpgradeItem {
	public DoubleUpgradeItem() {
		super(BaseUpgradeItem.getproperties(false));
	}

	public void setvars() {
		unlocalizedname = "doubleupgradeitem";
		category = grapplemod.upgradeCategories.DOUBLE;
	}
}
