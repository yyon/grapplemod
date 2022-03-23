package com.yyon.grapplinghook.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public interface KeypressItem {
	enum Keys {
		LAUNCHER, THROWLEFT, THROWRIGHT, THROWBOTH, ROCKET
	}
	
	public abstract void onCustomKeyDown(ItemStack stack, Player player, Keys key, boolean ismainhand);
	public abstract void onCustomKeyUp(ItemStack stack, Player player, Keys key, boolean ismainhand);
}
