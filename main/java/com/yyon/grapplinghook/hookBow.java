package com.yyon.grapplinghook;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

class hookBow extends grappleBow {

	public hookBow() {
		super();
		maxStackSize = 1;
		setFull3D();
		setUnlocalizedName("hookshot");
		
		this.setMaxDamage(500);
		
//		func_111022_d("grappling");
		setCreativeTab(CreativeTabs.tabCombat);
	}
	
	@Override
	public void createarrow(ItemStack satack, World worldIn, EntityPlayer playerIn) {
		entityarrow = new hookArrow(worldIn, playerIn, 0);
	}
}
