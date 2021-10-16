package com.yyon.grapplinghook.items;

import java.util.List;

import javax.annotation.Nullable;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.CommonSetup;
import com.yyon.grapplinghook.GrappleConfig;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

public class LongFallBoots extends ArmorItem {
	public LongFallBoots(ArmorMaterial material, int type) {
	    super(material, EquipmentSlotType.FEET, new Item.Properties().stacksTo(1).tab(CommonSetup.tabGrapplemod));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag par4) {
		if (!stack.isEnchanted()) {
			if (GrappleConfig.getconf().longfallboots.longfallbootsrecipe) {
				list.add(new StringTextComponent(CommonProxyClass.proxy.localize("grappletooltip.longfallbootsrecipe.desc")));
			}
		}
		list.add(new StringTextComponent(CommonProxyClass.proxy.localize("grappletooltip.longfallboots.desc")));
	}

	@Override
	public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> items) {
			if (this.allowdedIn(tab)) {
	        	ItemStack stack = new ItemStack(this);
	            items.add(stack);
	            
	        	stack = new ItemStack(this);
	        	stack.enchant(CommonSetup.wallrunenchantment, 1);
	        	stack.enchant(CommonSetup.doublejumpenchantment, 1);
	        	stack.enchant(CommonSetup.slidingenchantment, 1);
	            items.add(stack);
			}
	}
}
