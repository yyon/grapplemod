package com.yyon.grapplemod.client;

import com.yyon.grapplemod.blocks.modifierblock.TileEntityGrappleModifier;
import com.yyon.grapplemod.controllers.GrappleController;
import com.yyon.grapplemod.network.BaseMessageServer;
import com.yyon.grapplemod.utils.GrappleCustomization;
import com.yyon.grapplemod.utils.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;

public abstract class ClientProxyInterface {
	public static ClientProxyInterface proxy = null;

	public abstract void resetLauncherTime(int playerid);

	public abstract void launchPlayer(Player player);

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

	public abstract void startRocket(Player player, GrappleCustomization custom);

	public abstract void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio);

	public abstract double getRocketFunctioning();

	public abstract boolean isWallRunning(Entity entity, Vec motion);

	public abstract boolean isSliding(Entity entity, Vec motion);

	public abstract GrappleController createControl(int id, int hookEntityId, int entityid, Level world, Vec pos, BlockPos blockpos, GrappleCustomization custom);

	public abstract void playSlideSound(Entity entity);

	public abstract void playWallrunJumpSound(Entity entity);

	public abstract void playDoubleJumpSound(Entity entity);

	public abstract void sendToServer(BaseMessageServer message);

	public abstract void fillGrappleVariants(CreativeModeTab.Output items);

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

	public abstract String getKeyname(GrappleKeys key);

	public abstract boolean isShiftDown();

	public abstract boolean isControlDown();

	public abstract GrappleController unregisterController(int entityId);

	public abstract double getTimeSinceLastRopeJump(Level world);

	public abstract void resetRopeJumpTime(Level level);

	public abstract boolean isMovingSlowly(Entity entity);

	public abstract void playSound(ResourceLocation loc, float volume);

	public abstract int getWallrunTicks();

	public abstract void setWallrunTicks(int newWallrunTicks);
}
