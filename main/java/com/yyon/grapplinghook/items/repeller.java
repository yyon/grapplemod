package com.yyon.grapplinghook.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.UseAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.grappleController;

public class repeller extends Item {
	public repeller() {
		super();
		maxStackSize = 1;
		setFull3D();
		setUnlocalizedName("repeller");
		
		this.setMaxDamage(500);
		
		setCreativeTab(grapplemod.tabGrapplemod);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}

	public void dorightclick(ItemStack stack, World worldIn, PlayerEntity player) {
		if (worldIn.isRemote) {
			int playerid = player.getEntityId();
			if (grapplemod.controllers.containsKey(playerid) && grapplemod.controllers.get(playerid).controllerid != grapplemod.AIRID) {
				grappleController controller = grapplemod.controllers.get(playerid);
				controller.unattach();
			} else {
				grapplemod.createControl(grapplemod.REPELID, -1, playerid, worldIn, new vec(0,0,0), null, null);
			}
		}
	}

    @Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand)
    {
    	ItemStack itemStackIn = playerIn.getHeldItem(hand);
        this.dorightclick(itemStackIn, worldIn, playerIn);
        
        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemStackIn);
    }

    @Override
    public UseAction getItemUseAction(ItemStack par1ItemStack)
	{
		return UseAction.NONE;
	}
    
	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4)
	{
		list.add("Player is repelled by nearby blocks");
		list.add("Can be used with ender staff");
		list.add("");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Turn on");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " again - Turn off");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindSneak) + " - Slow down");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindForward) + ", " +
				grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindLeft) + ", " +
				grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindBack) + ", " +
				grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindRight) +
				" - Move");
	}
}
