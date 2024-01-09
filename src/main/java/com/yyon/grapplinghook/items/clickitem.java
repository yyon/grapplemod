package com.yyon.grapplinghook.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface clickitem {
	public abstract void onLeftClick(ItemStack stack, EntityPlayer player);
	public abstract void onLeftClickRelease(ItemStack stack, EntityPlayer player);
}
