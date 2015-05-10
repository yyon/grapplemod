package com.yyon.grapplinghook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

class grappleBow extends Item {

	public grappleArrow entityarrow = null;
	
	
	public grappleBow() {
		super();
		maxStackSize = 1;
		setFull3D();
		setUnlocalizedName("grapplinghook");
		
		this.setMaxDamage(500);
		
//		func_111022_d("grappling");
		setCreativeTab(CreativeTabs.tabCombat);
	}
	
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}
	
	
	public void dorightclick(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
        	
        	if (entityarrow != null) {
        		if (entityarrow.shootingEntity == null) {
        			entityarrow = null;
        		}
        	}
        	
			float f = 2.0F;
			if (entityarrow == null) {
				this.createarrow(stack, worldIn, playerIn);
	
//				entityarrow.setIsCritical(false);
//				entityarrow.setDamage(0.0);
//				entityarrow.setKnockbackStrength(0);
	
				stack.damageItem(1, playerIn);
				worldIn.playSoundAtEntity(playerIn, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
				
//				entityarrow.canBePickedUp = 0;
				
				worldIn.spawnEntityInWorld(entityarrow);
			} else {
				entityarrow.grappleend();
				entityarrow = null;
			}
		//            if (flag)
		//            {
		//                entityarrow.canBePickedUp = 2;
		//            }
		//            else
		//            {
		//                par3EntityPlayer.inventory.consumeInventoryItem(Items.arrow);
		//            }

//			worldIn.spawnEntityInWorld(entityarrow);
    	}
//		World world = worldIn;
//		EntityPlayer entity = par3EntityPlayer;
//		int i = (int)entity.posX;
//		int j = (int)entity.posY;
//		int k = (int)entity.posZ;
	}
	
	public void createarrow(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		entityarrow = new grappleArrow(worldIn, playerIn, 0);
	}
	
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft)
    {
    	
//        int j = this.getMaxItemUseDuration(stack) - timeLeft;
//        net.minecraftforge.event.entity.player.ArrowLooseEvent event = new net.minecraftforge.event.entity.player.ArrowLooseEvent(playerIn, stack, j);
//        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return;
        
    }
    
	public ItemStack onItemRightClick(ItemStack stack, World worldIn, final EntityPlayer playerIn){
//        net.minecraftforge.event.entity.player.ArrowNockEvent event = new net.minecraftforge.event.entity.player.ArrowNockEvent(playerIn, stack);
//        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return event.result;
        
        playerIn.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        
        this.dorightclick(stack, worldIn, playerIn);
        
		return stack;
	}

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
        return stack;
    }


	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.NONE;
	}
}
