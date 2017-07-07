package com.yyon.grapplinghook.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.entities.smartHookArrow;
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

public class smartHookBow extends grappleBow implements clickitem {
	public smartHookBow() {
		super();
		setUnlocalizedName("smarthook");
	}
	
	@Override
	public grappleArrow createarrow(ItemStack stack, World worldIn, EntityLivingBase playerIn, boolean righthand) {
		NBTTagCompound compound = grapplemod.getstackcompound(stack, "grapplemod");
		boolean slow = false;
		if (compound.hasKey("slow")) {
			slow = compound.getBoolean("slow");
		}
		
		return new smartHookArrow(worldIn, playerIn, righthand, slow);
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
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4)
	{
		list.add("Pulls player towards hook");
		list.add("");
		list.add("Point crosshairs towards desired direction of movement");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Throw grappling hook");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " again - Release");
		list.add("Double-" + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Release and throw again");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindJump) + " - Release and jump");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindSneak) + " + " + 
				grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindAttack) + " - Toggle speed");
	}
}
