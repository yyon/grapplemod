package com.yyon.grapplinghook;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface clickitem {
	public abstract void onLeftClick(ItemStack stack, EntityPlayer player);
//	public abstract void onRightClick(ItemStack stack, EntityPlayer player);
}
