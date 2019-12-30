package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class ForcefieldUpgradeItem extends BaseUpgradeItem {
	public ForcefieldUpgradeItem() {
		super(BaseUpgradeItem.getproperties(false));
	}

	public void setvars() {
		unlocalizedname = "forcefieldupgradeitem";
		category = grapplemod.upgradeCategories.FORCEFIELD;
	}
}
