package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class LimitsUpgradeItem extends BaseUpgradeItem {
	public LimitsUpgradeItem() {
		super(BaseUpgradeItem.getproperties(false));
	}

	public void setvars() {
		unlocalizedname = "limitsupgradeitem";
		category = grapplemod.upgradeCategories.LIMITS;
	}
}
