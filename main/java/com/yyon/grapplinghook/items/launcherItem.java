package com.yyon.grapplinghook.items;

import java.util.List;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.MixinEnvironment.Side;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.grapplemod;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
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

public class launcherItem extends Item {
	
	public launcherItem() {
		super(new Item.Properties().stacksTo(1).tab(grapplemod.tabGrapplemod));
//		super();
//		maxStackSize = 1;
//		setFull3D();
//		setUnlocalizedName("launcheritem");
//		
//		this.setMaxDamage(500);
//		
//		setCreativeTab(grapplemod.tabGrapplemod);
		
//		MinecraftForge.EVENT_BUS.register(this);
	}
	
//	@Override
//	public int getMaxItemUseDuration(ItemStack par1ItemStack)
//	{
//		return 72000;
//	}

	public void dorightclick(ItemStack stack, World worldIn, PlayerEntity player) {
		if (worldIn.isClientSide) {
			grapplemod.proxy.launchplayer(player);
		}
	}
	
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
    	ItemStack stack = playerIn.getItemInHand(hand);
        this.dorightclick(stack, worldIn, playerIn);
        
    	return ActionResult.success(stack);
	}
    
    /*
    @Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity entityLiving, Hand hand)
	{
    	ItemStack stack = entityLiving.getItemInHand(hand);
    	this.dorightclick(stack, worldIn, entityLiving);
        
    	return ActionResult.success(stack);
    }
    */

//    @Override
//    public EnumAction getItemUseAction(ItemStack par1ItemStack)
//	{
//		return EnumAction.NONE;
//	}
    
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag par4) {
		list.add(new StringTextComponent(grapplemod.proxy.localize("grappletooltip.launcheritem.desc")));
		list.add(new StringTextComponent(""));
		list.add(new StringTextComponent(grapplemod.proxy.localize("grappletooltip.launcheritemaim.desc")));
		list.add(new StringTextComponent(grapplemod.proxy.getkeyname(grapplemod.keys.keyBindUseItem) + grapplemod.proxy.localize("grappletooltip.launcheritemcontrols.desc")));
	}
}
