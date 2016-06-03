package com.yyon.grapplinghook.items;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

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
			int playerid = player.getEntityId();
			if (grapplemod.controllers.containsKey(playerid)) {
				grappleController controller = grapplemod.controllers.get(playerid);
				controller.unattach();
			} else {
				grapplemod.createControl(grapplemod.REPELID, -1, playerid, worldIn, new vec(0,0,0), -1, null);
			}
		}
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
		Minecraft minecraft = Minecraft.getMinecraft();
		list.add("Player is repelled by nearby blocks");
		list.add("Can be used with ender staff");
		list.add("");
		list.add(grapplemod.getkeyname(minecraft.gameSettings.keyBindUseItem) + " - Turn on");
		list.add(grapplemod.getkeyname(minecraft.gameSettings.keyBindUseItem) + " again - Turn off");
		list.add(grapplemod.getkeyname(minecraft.gameSettings.keyBindSneak) + " - Slow down");
		list.add(grapplemod.getkeyname(minecraft.gameSettings.keyBindForward) + ", " +
				grapplemod.getkeyname(minecraft.gameSettings.keyBindLeft) + ", " +
				grapplemod.getkeyname(minecraft.gameSettings.keyBindBack) + ", " +
				grapplemod.getkeyname(minecraft.gameSettings.keyBindRight) +
				" - Move");
	}
}
