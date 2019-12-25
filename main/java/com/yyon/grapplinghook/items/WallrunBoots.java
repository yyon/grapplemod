package com.yyon.grapplinghook.items;

import com.yyon.grapplinghook.grapplemod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

public class WallrunBoots extends ItemArmor {
	public WallrunBoots(ArmorMaterial material, int type) {
	    super(material, 0, EntityEquipmentSlot.FEET);
//	    this.setUnlocalizedName("wallrunboots");
//	    MinecraftForge.EVENT_BUS.register(this);
		setCreativeTab(grapplemod.tabGrapplemod);
	}
	
//	@Override
//    @SideOnly(Side.CLIENT)
//	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4)
//	{
//		list.add("Allows running on walls");
//	}

	@Override
    @SideOnly(Side.CLIENT)
    public ItemStack getDefaultInstance()
    {
        ItemStack stack = new ItemStack(this);
        stack.addEnchantment(grapplemod.wallrunenchantment, 1);
        return stack;
    }
	
	@Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
        	ItemStack stack = new ItemStack(this);
            stack.addEnchantment(grapplemod.wallrunenchantment, 1);
            items.add(stack);
        }
    }
}
