package com.yyon.grapplinghook.enchantments;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.grapplemod;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;

public class WallrunEnchantment extends Enchantment {
	public WallrunEnchantment() {
		super(grapplemod.getRarityFromInt(GrappleConfig.getconf().enchant_rarity_wallrun), grapplemod.GRAPPLEENCHANTS_FEET, new EntityEquipmentSlot[] {EntityEquipmentSlot.FEET});
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
