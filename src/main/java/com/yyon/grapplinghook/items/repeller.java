package com.yyon.grapplinghook.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.grappleController;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class repeller extends Item {
	public repeller() {
		super();
		maxStackSize = 1;
		setFull3D();
		setUnlocalizedName("repeller");
		
		this.setMaxDamage(500);
		
		setCreativeTab(CreativeTabs.tabTransport);
		
		FMLCommonHandler.instance().bus().register(this);
	    MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		itemIcon = iconRegister.registerIcon("grapplemod:repelleron");
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}

	public void dorightclick(ItemStack stack, World worldIn, EntityPlayer player) {
		if (worldIn.isRemote) {
			int playerid = player.getEntityId();
			if (grapplemod.controllers.containsKey(playerid) && grapplemod.controllers.get(playerid).controllerid != grapplemod.AIRID) {
				grappleController controller = grapplemod.controllers.get(playerid);
				controller.unattach();
			} else {
				grapplemod.createControl(grapplemod.REPELID, -1, playerid, worldIn, new vec(0,0,0), -1, null);
			}
		}
	}

    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        this.dorightclick(itemStackIn, worldIn, playerIn);
        
        return itemStackIn;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.none;
	}
    
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
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
