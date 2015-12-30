package com.yyon.grapplinghook.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.entities.hookArrow;

/* // 1.7.10 Compatability
import net.minecraft.client.renderer.texture.IIconRegister;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
//*/

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

public class hookBow extends grappleBow {
	public hookBow() {
		super();
		setUnlocalizedName("hookshot");
	}
	
	@Override
	public grappleArrow createarrow(ItemStack satack, World worldIn, EntityPlayer playerIn) {
		return new hookArrow(worldIn, playerIn, 0);
	}
/* // 1.7.10 Compatability
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		 itemIcon = iconRegister.registerIcon("grapplemod:hookshot");
	}
//*/

}
