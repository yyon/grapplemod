package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

import net.minecraft.item.Item;

public class BaseUpgradeItem extends Item {
	public grapplemod.upgradeCategories category = null;
	
	public BaseUpgradeItem(int maxStackSize, grapplemod.upgradeCategories theCategory) {
		super(new Item.Properties().stacksTo(maxStackSize).tab(grapplemod.tabGrapplemod));
		
		this.category = theCategory;
	}
	
	public BaseUpgradeItem() {
		this(64, null);
	}
}
