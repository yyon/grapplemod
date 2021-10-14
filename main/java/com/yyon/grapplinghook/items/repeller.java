package com.yyon.grapplinghook.items;

import java.util.List;

import javax.annotation.Nullable;

import com.yyon.grapplinghook.grapplemod;
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
		super(new Item.Properties().stacksTo(1).tab(grapplemod.tabGrapplemod));
	}
	
	public void dorightclick(ItemStack stack, World worldIn, PlayerEntity player) {
		if (worldIn.isClientSide) {
			int playerid = player.getId();
			if (grapplemod.controllers.containsKey(playerid) && grapplemod.controllers.get(playerid).controllerid != grapplemod.AIRID) {
				grappleController controller = grapplemod.controllers.get(playerid);
				controller.unattach();
			} else {
				grapplemod.proxy.createControl(grapplemod.REPELID, -1, playerid, worldIn, new vec(0,0,0), null, null);
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
		list.add(new StringTextComponent(grapplemod.proxy.localize("grappletooltip.repelleritem.desc")));
		list.add(new StringTextComponent(grapplemod.proxy.localize("grappletooltip.repelleritem2.desc")));
		list.add(new StringTextComponent(""));
		list.add(new StringTextComponent(grapplemod.proxy.getkeyname(grapplemod.keys.keyBindUseItem) + grapplemod.proxy.localize("grappletooltip.repelleritemon.desc")));
		list.add(new StringTextComponent(grapplemod.proxy.getkeyname(grapplemod.keys.keyBindUseItem) + grapplemod.proxy.localize("grappletooltip.repelleritemoff.desc")));
		list.add(new StringTextComponent(grapplemod.proxy.getkeyname(grapplemod.keys.keyBindSneak) + grapplemod.proxy.localize("grappletooltip.repelleritemslow.desc")));
		list.add(new StringTextComponent(grapplemod.proxy.getkeyname(grapplemod.keys.keyBindForward) + ", " +
				grapplemod.proxy.getkeyname(grapplemod.keys.keyBindLeft) + ", " +
				grapplemod.proxy.getkeyname(grapplemod.keys.keyBindBack) + ", " +
				grapplemod.proxy.getkeyname(grapplemod.keys.keyBindRight) +
				" " + grapplemod.proxy.localize("grappletooltip.repelleritemmove.desc")));
	}
}
