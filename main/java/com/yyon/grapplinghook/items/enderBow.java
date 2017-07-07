package com.yyon.grapplinghook.items;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.entities.enderArrow;
import com.yyon.grapplinghook.entities.grappleArrow;

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

public class enderBow extends grappleBow implements clickitem {
	
	public enderBow() {
		super();
		setUnlocalizedName("enderhook");
	}
	
	@Override
	public grappleArrow createarrow(ItemStack stack, World worldIn, EntityLivingBase playerIn, boolean righthand) {
		return new enderArrow(worldIn, playerIn, righthand);
	}
	
	@Override
	public void onLeftClick(ItemStack stack, EntityPlayer player) {
		if (player.world.isRemote) {
			grapplemod.proxy.launchplayer(player);
		}
	}
	@Override
	public void onLeftClickRelease(ItemStack stack, EntityPlayer player) {
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4)
	{
		list.add("A grappling hook which uses an ender staff to speed up or change directions");
		list.add("");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Throw grappling hook");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " again - Release");
		list.add("Double-" + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Release and throw again");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindAttack) + " - Launch player towards crosshairs");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindForward) + ", " +
				grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindLeft) + ", " +
				grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindBack) + ", " +
				grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindRight) +
				" - Swing");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindJump) + " - Release and jump (while in midair)");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindSneak) + " - Stop swinging");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindSneak) + " + " +
				grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindForward) + 
				" - Climb up");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindSneak) + " + " +
				grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindBack) + 
				" - Climb down");
	}
}
