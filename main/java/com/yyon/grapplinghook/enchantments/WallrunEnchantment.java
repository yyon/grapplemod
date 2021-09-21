package com.yyon.grapplinghook.enchantments;

import com.yyon.grapplinghook.grapplemod;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class WallrunEnchantment extends Enchantment {
	public WallrunEnchantment() {
		super(Rarity.RARE, grapplemod.GRAPPLEENCHANTS_FEET, new EntityEquipmentSlot[] {EntityEquipmentSlot.FEET});
		this.setName("wallrunenchantment");
	}
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 1;
    }

    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 40;
    }

    public int getMaxLevel()
    {
        return 1;
    }
}
