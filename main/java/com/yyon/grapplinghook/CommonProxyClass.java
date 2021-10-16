package com.yyon.grapplinghook;

import org.lwjgl.glfw.GLFW;

import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.network.BaseMessageClient;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public interface CommonProxyClass {
	public void resetlaunchertime(int playerid);

	public void launchplayer(PlayerEntity player);
	
	public enum mckeys {
		keyBindUseItem,
		keyBindForward,
		keyBindLeft,
		keyBindBack,
		keyBindRight,
		keyBindJump,
		keyBindSneak,
		keyBindAttack
	}
	public String getkeyname(mckeys keyenum);

	public void openModifierScreen(TileEntityGrappleModifier tileent);
	
	public String localize(String string);

	public void startrocket(PlayerEntity player, GrappleCustomization custom);
	
	public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio);

	public double getRocketFunctioning();

	public boolean iswallrunning(Entity entity, vec motion);
	
	public boolean issliding(Entity entity, vec motion);
		
	public grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, BlockPos blockpos, GrappleCustomization custom);

	public void playSlideSound(Entity entity);
	
	public void playWallrunJumpSound(Entity entity);

	public void playDoubleJumpSound(Entity entity);

	public void onMessageReceivedClient(BaseMessageClient baseMessage, Context ctx);

	public void fillGrappleVariants(ItemGroup tab, NonNullList<ItemStack> items);
	
	public enum grapplekeys {
		key_boththrow,
		key_leftthrow,
		key_rightthrow,
		key_motoronoff,
		key_jumpanddetach,
		key_slow,
		key_climb,
		key_climbup,
		key_climbdown,
		key_enderlaunch,
		key_rocket,
		key_slide
	}
	public boolean isKeyDown(grapplekeys key);
}
