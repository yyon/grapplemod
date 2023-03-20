package com.yyon.grapplinghook.items;

import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.common.CommonSetup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

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

public class EnderStaffItem extends Item {
	
	public EnderStaffItem() {
		super(new Item.Properties().stacksTo(1));
	}
	
	public void doRightClick(ItemStack stack, Level worldIn, Player player) {
		if (worldIn.isClientSide) {
			ClientProxyInterface.proxy.launchPlayer(player);
		}
	}
	
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
    	ItemStack stack = playerIn.getItemInHand(hand);
        this.doRightClick(stack, worldIn, playerIn);

    	return InteractionResultHolder.success(stack);
	}
    
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag par4) {
		list.add(Component.literal(ClientProxyInterface.proxy.localize("grappletooltip.launcheritem.desc")));
		list.add(Component.literal(""));
		list.add(Component.literal(ClientProxyInterface.proxy.localize("grappletooltip.launcheritemaim.desc")));
		list.add(Component.literal(ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindUseItem) + ClientProxyInterface.proxy.localize("grappletooltip.launcheritemcontrols.desc")));
	}
}
