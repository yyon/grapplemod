package com.yyon.grapplinghook.enchantments;

import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.config.GrappleConfigUtils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class DoublejumpEnchantment extends Enchantment {
	public DoublejumpEnchantment() {
		super(GrappleConfigUtils.getRarityFromInt(GrappleConfig.getConf().enchantments.doublejump.enchant_rarity_double_jump), EnchantmentType.ARMOR_FEET, new EquipmentSlotType[] {EquipmentSlotType.FEET});
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
