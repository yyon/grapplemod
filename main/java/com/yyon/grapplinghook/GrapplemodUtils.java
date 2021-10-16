package com.yyon.grapplinghook;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class GrapplemodUtils {
	public static void sendtocorrectclient(Object message, int playerid, World w) {
		Entity entity = w.getEntity(playerid);
		if (entity instanceof ServerPlayerEntity) {
			CommonSetup.network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), message);
		} else {
			System.out.println("ERROR! couldn't find player");
		}
	}

	public static BlockRayTraceResult rayTraceBlocks(World world, vec from, vec to) {
		RayTraceResult result = world.clip(new RayTraceContext(from.toVec3d(), to.toVec3d(), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null));
		if (result != null && result instanceof BlockRayTraceResult) {
			BlockRayTraceResult blockhit = (BlockRayTraceResult) result;
			if (blockhit.getType() != RayTraceResult.Type.BLOCK) {
				return null;
			}
			return blockhit;
		}
		return null;
	}

	public static long getTime(World w) {
		return w.getGameTime();
	}

	private static int controllerid = 0;
	public static int GRAPPLEID = controllerid++;
	public static int REPELID = controllerid++;
	public static int AIRID = controllerid++;

}
