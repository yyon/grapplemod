package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BaseUpgradeItem extends Item {
	public String unlocalizedname;
	public grapplemod.upgradeCategories category;
	
	public BaseUpgradeItem() {
		super();
		maxStackSize = 1;
		setvars();
		setFull3D();
		setUnlocalizedName(unlocalizedname);
		
		setCreativeTab(grapplemod.tabGrapplemod);
	}
	
	public void setvars() {
		unlocalizedname = "baseupgradeitem";
		category = null;
		maxStackSize = 64;
	}
}
