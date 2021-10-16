package com.yyon.grapplinghook.items;

import java.util.List;

import javax.annotation.Nullable;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.CommonSetup;
import com.yyon.grapplinghook.GrapplemodUtils;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.grappleController;

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

public class repeller extends Item {
	public repeller() {
		super(new Item.Properties().stacksTo(1).tab(CommonSetup.tabGrapplemod));
	}
	
	public void dorightclick(ItemStack stack, World worldIn, PlayerEntity player) {
		if (worldIn.isClientSide) {
			int playerid = player.getId();
			grappleController oldController = CommonProxyClass.proxy.unregisterController(playerid);
			if (oldController == null || oldController.controllerid == GrapplemodUtils.AIRID) {
				CommonProxyClass.proxy.createControl(GrapplemodUtils.REPELID, -1, playerid, worldIn, new vec(0,0,0), null, null);
			}
		}
	}

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
    	ItemStack stack = playerIn.getItemInHand(hand);
        this.dorightclick(stack, worldIn, playerIn);
        
    	return ActionResult.success(stack);
	}
    
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag par4) {
		list.add(new StringTextComponent(CommonProxyClass.proxy.localize("grappletooltip.repelleritem.desc")));
		list.add(new StringTextComponent(CommonProxyClass.proxy.localize("grappletooltip.repelleritem2.desc")));
		list.add(new StringTextComponent(""));
		list.add(new StringTextComponent(CommonProxyClass.proxy.getkeyname(CommonProxyClass.mckeys.keyBindUseItem) + CommonProxyClass.proxy.localize("grappletooltip.repelleritemon.desc")));
		list.add(new StringTextComponent(CommonProxyClass.proxy.getkeyname(CommonProxyClass.mckeys.keyBindUseItem) + CommonProxyClass.proxy.localize("grappletooltip.repelleritemoff.desc")));
		list.add(new StringTextComponent(CommonProxyClass.proxy.getkeyname(CommonProxyClass.mckeys.keyBindSneak) + CommonProxyClass.proxy.localize("grappletooltip.repelleritemslow.desc")));
		list.add(new StringTextComponent(CommonProxyClass.proxy.getkeyname(CommonProxyClass.mckeys.keyBindForward) + ", " +
				CommonProxyClass.proxy.getkeyname(CommonProxyClass.mckeys.keyBindLeft) + ", " +
				CommonProxyClass.proxy.getkeyname(CommonProxyClass.mckeys.keyBindBack) + ", " +
				CommonProxyClass.proxy.getkeyname(CommonProxyClass.mckeys.keyBindRight) +
				" " + CommonProxyClass.proxy.localize("grappletooltip.repelleritemmove.desc")));
	}
}
