package com.yyon.grapplinghook.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;


public interface KeypressItem {
	enum Keys {
		LAUNCHER, THROWLEFT, THROWRIGHT, THROWBOTH, ROCKET
	}
	
	public abstract void onCustomKeyDown(ItemStack stack, PlayerEntity player, Keys key, boolean ismainhand);
	public abstract void onCustomKeyUp(ItemStack stack, PlayerEntity player, Keys key, boolean ismainhand);
}
