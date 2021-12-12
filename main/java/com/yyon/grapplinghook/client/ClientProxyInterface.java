package com.yyon.grapplinghook.client;

import com.yyon.grapplinghook.blocks.modifierblock.TileEntityGrappleModifier;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.network.BaseMessageClient;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.Vec;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public abstract class ClientProxyInterface {
	public static ClientProxyInterface proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> () -> null);

	public abstract void resetLauncherTime(int playerid);

	public abstract void launchPlayer(PlayerEntity player);
	
	public enum McKeys {
		keyBindUseItem,
		keyBindForward,
		keyBindLeft,
		keyBindBack,
		keyBindRight,
		keyBindJump,
		keyBindSneak,
		keyBindAttack
	}
	public abstract String getKeyname(McKeys keyenum);

	public abstract boolean isKeyDown(McKeys keybindjump);

	public abstract void openModifierScreen(TileEntityGrappleModifier tileent);
	
	public abstract String localize(String string);

	public abstract void startRocket(PlayerEntity player, GrappleCustomization custom);
	
	public abstract void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio);

	public abstract double getRocketFunctioning();

	public abstract boolean isWallRunning(Entity entity, Vec motion);
	
	public abstract boolean isSliding(Entity entity, Vec motion);
		
	public abstract GrappleController createControl(int id, int hookEntityId, int entityid, World world, Vec pos, BlockPos blockpos, GrappleCustomization custom);

	public abstract void playSlideSound(Entity entity);
	
	public abstract void playWallrunJumpSound(Entity entity);

	public abstract void playDoubleJumpSound(Entity entity);

	public abstract void onMessageReceivedClient(BaseMessageClient baseMessage, Context ctx);

	public abstract void fillGrappleVariants(ItemGroup tab, NonNullList<ItemStack> items);
	
	public enum GrappleKeys {
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
	public abstract boolean isKeyDown(GrappleKeys key);

	public abstract GrappleController unregisterController(int entityId);

	public abstract double getTimeSinceLastRopeJump(World world);

	public abstract void resetRopeJumpTime(World level);

	public abstract boolean isMovingSlowly(Entity entity);

	public abstract void playSound(ResourceLocation loc, float volume);
	
	public abstract int getWallrunTicks();
	
	public abstract void setWallrunTicks(int newWallrunTicks);
}
