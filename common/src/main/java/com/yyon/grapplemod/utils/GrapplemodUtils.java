package com.yyon.grapplemod.utils;

import com.yyon.grapplemod.Constants;
import com.yyon.grapplemod.network.BaseMessageClient;
import com.yyon.grapplemod.network.GrappleNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;

public class GrapplemodUtils {
	public static void sendToCorrectClient(BaseMessageClient message, int playerid, Level w) {
		Entity entity = w.getEntity(playerid);
		if (entity instanceof ServerPlayer) {
			GrappleNetwork.sendToPlayer((ServerPlayer) entity, message);
		} else {
			Constants.LOG.error("Couldn't find player to send message to");
		}
	}

	public static BlockHitResult rayTraceBlocks(Level world, Vec from, Vec to) {
		HitResult result = world.clip(new ClipContext(from.toVec3d(), to.toVec3d(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
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
