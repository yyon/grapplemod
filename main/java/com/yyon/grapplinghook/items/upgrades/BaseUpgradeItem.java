package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BaseUpgradeItem extends Item {
	public String unlocalizedname;
	public grapplemod.upgradeCategories category;
	
	public BaseUpgradeItem() {
		super();
		setvars();
		maxStackSize = 1;
		setFull3D();
		setUnlocalizedName(unlocalizedname);
		
		setCreativeTab(CreativeTabs.TRANSPORTATION);
	}
	
	public void setvars() {
		unlocalizedname = "baseupgradeitem";
		category = null;
	}
}
