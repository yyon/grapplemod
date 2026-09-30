package com.yyon.grapplemod.enchantments;

import com.yyon.grapplemod.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Optional;

public class GrappleEnchantments {
	public static final ResourceKey<Enchantment> WALLRUN = key("wallrunenchantment");
	public static final ResourceKey<Enchantment> DOUBLE_JUMP = key("doublejumpenchantment");
	public static final ResourceKey<Enchantment> SLIDING = key("slidingenchantment");

	private static ResourceKey<Enchantment> key(String name) {
		return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Constants.MODID, name));
	}

	public static boolean isWearing(Entity entity, ResourceKey<Enchantment> enchantment) {
		if (!(entity instanceof LivingEntity livingEntity)) {
			return false;
		}
		Optional<Holder.Reference<Enchantment>> holder = entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(enchantment);
		if (holder.isEmpty()) {
			return false;
		}
		for (ItemStack stack : livingEntity.getArmorSlots()) {
			if (stack != null && EnchantmentHelper.getItemEnchantmentLevel(holder.get(), stack) >= 1) {
				return true;
			}
		}
		return false;
	}
}
