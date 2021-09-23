package com.yyon.grapplinghook.enchantments;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.grapplemod;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class DoublejumpEnchantment extends Enchantment {
	public DoublejumpEnchantment() {
		super(grapplemod.getRarityFromInt(GrappleConfig.getconf().enchant_rarity_double_jump), grapplemod.GRAPPLEENCHANTS_FEET, new EntityEquipmentSlot[] {EntityEquipmentSlot.FEET});
		this.setName("doublejumpenchantment");
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
