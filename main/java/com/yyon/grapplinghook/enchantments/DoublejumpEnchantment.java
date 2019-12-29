package com.yyon.grapplinghook.enchantments;

import com.yyon.grapplinghook.grapplemod;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;

public class DoublejumpEnchantment extends Enchantment {
	public DoublejumpEnchantment() {
		super(Rarity.UNCOMMON, grapplemod.GRAPPLEENCHANTS_FEET, new EquipmentSlotType[] {EquipmentSlotType.FEET});
		this.setName("doublejumpenchantment");
	}
}
