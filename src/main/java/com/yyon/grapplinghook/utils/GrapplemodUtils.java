package com.yyon.grapplinghook.utils;

import com.yyon.grapplinghook.common.CommonSetup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.PacketDistributor;

public class GrapplemodUtils {
	public static void sendToCorrectClient(Object message, int playerid, Level w) {
		Entity entity = w.getEntity(playerid);
		if (entity instanceof ServerPlayer) {
			CommonSetup.network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), message);
		} else {
			System.out.println("ERROR! couldn't find player");
		}
	}

	public static BlockHitResult rayTraceBlocks(Level world, Vec from, Vec to) {
		HitResult result = world.clip(new ClipContext(from.toVec3d(), to.toVec3d(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
		if (result != null && result instanceof BlockHitResult) {
			BlockHitResult blockhit = (BlockHitResult) result;
			if (blockhit.getType() != HitResult.Type.BLOCK) {
				return null;
			}
			return blockhit;
		}
		return null;
	}

	public static long getTime(Level w) {
		return w.getGameTime();
	}

	private static int controllerid = 0;
	public static int GRAPPLEID = controllerid++;
	public static int REPELID = controllerid++;
	public static int AIRID = controllerid++;

}
