package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class StaffUpgradeItem extends BaseUpgradeItem {
	public StaffUpgradeItem() {
		super(BaseUpgradeItem.getproperties(false));
	}

	public void setvars() {
		unlocalizedname = "staffupgradeitem";
		category = grapplemod.upgradeCategories.STAFF;
	}
}
