package com.yyon.grapplinghook.items;

import java.util.List;

import javax.annotation.Nullable;

import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.utils.GrapplemodUtils;
import com.yyon.grapplinghook.utils.Vec;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ForcefieldItem extends Item {
	public ForcefieldItem() {
		super(new Item.Properties().stacksTo(1).tab(CommonSetup.tabGrapplemod));
	}
	
	public void doRightClick(ItemStack stack, World worldIn, PlayerEntity player) {
		if (worldIn.isClientSide) {
			int playerid = player.getId();
			GrappleController oldController = ClientProxyInterface.proxy.unregisterController(playerid);
			if (oldController == null || oldController.controllerId == GrapplemodUtils.AIRID) {
				ClientProxyInterface.proxy.createControl(GrapplemodUtils.REPELID, -1, playerid, worldIn, new Vec(0,0,0), null, null);
			}
		}
	}

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
    	ItemStack stack = playerIn.getItemInHand(hand);
        this.doRightClick(stack, worldIn, playerIn);
        
    	return ActionResult.success(stack);
	}
    
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag par4) {
		list.add(new StringTextComponent(ClientProxyInterface.proxy.localize("grappletooltip.repelleritem.desc")));
		list.add(new StringTextComponent(ClientProxyInterface.proxy.localize("grappletooltip.repelleritem2.desc")));
		list.add(new StringTextComponent(""));
		list.add(new StringTextComponent(ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindUseItem) + ClientProxyInterface.proxy.localize("grappletooltip.repelleritemon.desc")));
		list.add(new StringTextComponent(ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindUseItem) + ClientProxyInterface.proxy.localize("grappletooltip.repelleritemoff.desc")));
		list.add(new StringTextComponent(ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindSneak) + ClientProxyInterface.proxy.localize("grappletooltip.repelleritemslow.desc")));
		list.add(new StringTextComponent(ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindForward) + ", " +
				ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindLeft) + ", " +
				ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindBack) + ", " +
				ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindRight) +
				" " + ClientProxyInterface.proxy.localize("grappletooltip.repelleritemmove.desc")));
	}
}
