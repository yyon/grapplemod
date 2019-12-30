package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class SwingUpgradeItem extends BaseUpgradeItem {
	public SwingUpgradeItem() {
		super(BaseUpgradeItem.getproperties(false));
	}

	public void setvars() {
		unlocalizedname = "swingupgradeitem";
		category = grapplemod.upgradeCategories.SWING;
	}
}
