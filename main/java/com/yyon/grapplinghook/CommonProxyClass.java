package com.yyon.grapplinghook;

import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public interface CommonProxyClass {

//	public void sendplayermovementmessage(grappleArrow grappleArrow, int playerid, int arrowid);

//	public void getplayermovement(grappleController control, int playerid);
	
	public void resetlaunchertime(int playerid);

	public void launchplayer(PlayerEntity player);
	
	public boolean isSneaking(Entity entity);
    
	public String getkeyname(grapplemod.keys keyenum);

	public void openModifierScreen(TileEntityGrappleModifier tileent);
	
	public String localize(String string);

	public void startrocket(PlayerEntity player, GrappleCustomization custom);
	
	public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio);

	public double getRocketFunctioning();

	public boolean iswallrunning(Entity entity, vec motion);
	
	public boolean issliding(Entity entity, vec motion);
		
//	public grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, BlockPos blockpos, GrappleCustomization custom);

	public void playSlideSound(Entity entity);
	
	public void playWallrunJumpSound(Entity entity);
}
