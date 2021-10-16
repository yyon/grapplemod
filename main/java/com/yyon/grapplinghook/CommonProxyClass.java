package com.yyon.grapplinghook;

import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.network.BaseMessageClient;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public abstract class CommonProxyClass {
	public static CommonProxyClass proxy = DistExecutor.unsafeRunForDist(() -> ClientProxyClass::new, () -> () -> null);

	public abstract void resetlaunchertime(int playerid);

	public abstract void launchplayer(PlayerEntity player);
	
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
	public abstract String getkeyname(mckeys keyenum);

	public abstract void openModifierScreen(TileEntityGrappleModifier tileent);
	
	public abstract String localize(String string);

	public abstract void startrocket(PlayerEntity player, GrappleCustomization custom);
	
	public abstract void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio);

	public abstract double getRocketFunctioning();

	public abstract boolean iswallrunning(Entity entity, vec motion);
	
	public abstract boolean issliding(Entity entity, vec motion);
		
	public abstract grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, BlockPos blockpos, GrappleCustomization custom);

	public abstract void playSlideSound(Entity entity);
	
	public abstract void playWallrunJumpSound(Entity entity);

	public abstract void playDoubleJumpSound(Entity entity);

	public abstract void onMessageReceivedClient(BaseMessageClient baseMessage, Context ctx);

	public abstract void fillGrappleVariants(ItemGroup tab, NonNullList<ItemStack> items);
	
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
	public abstract boolean isKeyDown(grapplekeys key);

	public abstract grappleController unregisterController(int entityId);

	public abstract double getTimeSinceLastRopeJump(World world);

	public abstract void resetRopeJumpTime(World level);
}
