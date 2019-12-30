package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class MotorUpgradeItem extends BaseUpgradeItem {
	public MotorUpgradeItem() {
		super(BaseUpgradeItem.getproperties(false));
	}

	public void setvars() {
		unlocalizedname = "motorupgradeitem";
		category = grapplemod.upgradeCategories.MOTOR;
	}
}
