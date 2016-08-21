package com.yyon.grapplinghook.items;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.entities.magnetArrow;
import com.yyon.grapplinghook.network.ToolConfigMessage;

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

public class magnetBow extends grappleBow implements clickitem {
	public magnetBow() {
		super();
		setUnlocalizedName("magnetbow");
	}
	
	@Override
	public grappleArrow createarrow(ItemStack stack, World worldIn, EntityLivingBase playerIn, boolean righthand) {
		NBTTagCompound compound = stack.getSubCompound("grapplemod", true);
		int repelconf = compound.getInteger("repelconf");
		
		return new magnetArrow(worldIn, playerIn, righthand, repelconf);
	}
	
	@Override
	public void onLeftClick(ItemStack stack, EntityPlayer player) {
		if (player.isSneaking()) {
			int playerid = player.getEntityId();
			grapplemod.network.sendToServer(new ToolConfigMessage(playerid));
		}
	}

	@Override
	public void onLeftClickRelease(ItemStack stack, EntityPlayer player) {
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4)
	{
		list.add("A magnetic grappling hook");
		list.add("Hook is attracted to nearby blocks");
		list.add("Player is repelled by nearby blocks");
		list.add("");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Throw grappling hook");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " again - Release");
		list.add("Double-" + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Release and throw again");
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
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindSneak) + " + " + 
				grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindAttack) + " - Change repel force");
	}
}
