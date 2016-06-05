package com.yyon.grapplinghook.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.grapplemod;

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

public class launcherItem extends Item {
	
	public launcherItem() {
		super();
		maxStackSize = 1;
		setFull3D();
		setUnlocalizedName("launcheritem");
		
		this.setMaxDamage(500);
		
		setCreativeTab(CreativeTabs.TRANSPORTATION);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}

	public void dorightclick(ItemStack stack, World worldIn, EntityPlayer player) {
		if (worldIn.isRemote) {
			grapplemod.proxy.launchplayer(player);
		}
	}
	
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        this.dorightclick(stack, worldIn, playerIn);
        
    	return EnumActionResult.SUCCESS;
	}
    
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        this.dorightclick(itemStackIn, worldIn, playerIn);
        
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.NONE;
	}
    
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4)
	{
		list.add("Launches player");
		list.add("");
		list.add("Use crosshairs to aim");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Launch player");
	}
}
