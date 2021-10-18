package com.yyon.grapplinghook.enchantments;

import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.config.GrappleConfigUtils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class WallrunEnchantment extends Enchantment {
	public WallrunEnchantment() {
		super(GrappleConfigUtils.getRarityFromInt(GrappleConfig.getConf().enchantments.wallrun.enchant_rarity_wallrun), EnchantmentType.ARMOR_FEET, new EquipmentSlotType[] {EquipmentSlotType.FEET});
	}
	
	@Override
    public int getMinCost(int enchantmentLevel)
    {
        return 1;
    }

	@Override
    public int getMaxCost(int enchantmentLevel)
    {
        return this.getMinCost(enchantmentLevel) + 40;
    }

	@Override
	public int getMaxLevel()
    {
        return 1;
    }
}
