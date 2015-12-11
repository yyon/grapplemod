package com.yyon.grapplinghook;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class hookBow extends grappleBow {
	public hookBow() {
		super();
		setUnlocalizedName("hookshot");
	}
	
	@Override
	public grappleArrow createarrow(ItemStack satack, World worldIn, EntityPlayer playerIn) {
		return new hookArrow(worldIn, playerIn, 0);
	}
}
