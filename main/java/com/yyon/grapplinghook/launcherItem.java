package com.yyon.grapplinghook;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

class launcherItem extends Item {
	
	EntityPlayer playerused = null;
	int reusetimer = 0;
	int reusetime = 100;

	public launcherItem() {
		super();
		maxStackSize = 1;
		setFull3D();
		setUnlocalizedName("launcheritem");
		
		this.setMaxDamage(500);
		
//		func_111022_d("grappling");
		setCreativeTab(CreativeTabs.tabCombat);
		
		FMLCommonHandler.instance().bus().register(this);
	}
	
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}
	
	
	public Vec3 multvec(Vec3 a, double changefactor) {
		return new Vec3(a.xCoord * changefactor, a.yCoord * changefactor, a.zCoord * changefactor);
	}
	
	public void dorightclick(ItemStack stack, World worldIn, EntityPlayer player) {
		if (!worldIn.isRemote) {
			if (playerused == null) {
				playerused = player;
				reusetimer = reusetime;
				
	        	Vec3 facing = player.getLookVec();
				Vec3 playermotion = new Vec3(player.motionX, player.motionY, player.motionZ);
				Vec3 newvec = playermotion.add(multvec(facing, 3));
				
				player.setVelocity(newvec.xCoord, newvec.yCoord, newvec.zCoord);
				if (player instanceof EntityPlayerMP) {
					((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
				}
			}
		}
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
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (reusetimer > 0) {
			reusetimer--;
		}
		if (playerused != null) {
			if (playerused.onGround && reusetimer <= 0) {
				playerused = null;
			}
		}
	}
}
