package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class ThrowUpgradeItem extends BaseUpgradeItem {
	public ThrowUpgradeItem() {
		super(BaseUpgradeItem.getproperties(false));
	}

	public void setvars() {
		unlocalizedname = "throwupgradeitem";
		category = grapplemod.upgradeCategories.THROW;
	}
}
