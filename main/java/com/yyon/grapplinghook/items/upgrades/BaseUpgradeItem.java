package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

import net.minecraft.item.Item;

public class BaseUpgradeItem extends Item {
	public String unlocalizedname;
	public grapplemod.upgradeCategories category;
	public static Item containerItem;
	
	public Item.Properties prop;
	
	public BaseUpgradeItem(Item.Properties prop) {
		super(prop);
		this.prop = prop;
		this.prop.containerItem(this);

		setvars();
	}
	
	public static Item.Properties getproperties(boolean isbase) {
		Item.Properties properties = new Properties();
		if (isbase) {
			properties.maxStackSize(64);
		} else {
			properties.maxStackSize(1);
		}
		properties.group(grapplemod.tabGrapplemod);
		return properties;
	}

	public void setvars() {
		unlocalizedname = "baseupgradeitem";
		category = null;
	}
}
