package com.yyon.grapplinghook.enchantments;

import com.yyon.grapplinghook.grapplemod;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;

public class SlidingEnchantment extends Enchantment {
	public SlidingEnchantment() {
		super(Rarity.UNCOMMON, grapplemod.GRAPPLEENCHANTS_FEET, new EquipmentSlotType[] {EquipmentSlotType.FEET});
		this.setName("slidingenchantment");
	}
}
