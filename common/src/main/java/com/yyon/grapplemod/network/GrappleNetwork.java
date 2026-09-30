package com.yyon.grapplemod.network;

import com.yyon.grapplemod.Constants;
import com.yyon.grapplemod.client.ClientProxyInterface;
import com.yyon.grapplemod.utils.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.List;

public class GrappleNetwork {
	public record Payload<T extends CustomPacketPayload>(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
	}

	public static final List<Payload<? extends BaseMessageServer>> SERVERBOUND = List.of(
			new Payload<>(PlayerMovementMessage.TYPE, PlayerMovementMessage.STREAM_CODEC),
			new Payload<>(GrappleEndMessage.TYPE, GrappleEndMessage.STREAM_CODEC),
			new Payload<>(GrappleModifierMessage.TYPE, GrappleModifierMessage.STREAM_CODEC),
			new Payload<>(KeypressMessage.TYPE, KeypressMessage.STREAM_CODEC)
	);

	public static final List<Payload<? extends BaseMessageClient>> CLIENTBOUND = List.of(
			new Payload<>(GrappleAttachMessage.TYPE, GrappleAttachMessage.STREAM_CODEC),
			new Payload<>(GrappleDetachMessage.TYPE, GrappleDetachMessage.STREAM_CODEC),
			new Payload<>(DetachSingleHookMessage.TYPE, DetachSingleHookMessage.STREAM_CODEC),
			new Payload<>(GrappleAttachPosMessage.TYPE, GrappleAttachPosMessage.STREAM_CODEC),
			new Payload<>(SegmentMessage.TYPE, SegmentMessage.STREAM_CODEC)
	);

	public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> type(String name) {
		return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MODID, name));
	}

	public static void sendToServer(BaseMessageServer message) {
		ClientProxyInterface.proxy.sendToServer(message);
	}

	public static void sendToPlayer(ServerPlayer player, BaseMessageClient message) {
		player.connection.send(new ClientboundCustomPayloadPacket(message));
	}

	public static void sendToTracking(Level level, Vec pos, BaseMessageClient message) {
		if (level instanceof ServerLevel serverLevel) {
			ChunkPos chunkPos = new ChunkPos(BlockPos.containing(pos.x, pos.y, pos.z));
			for (ServerPlayer player : serverLevel.getChunkSource().chunkMap.getPlayers(chunkPos, false)) {
				sendToPlayer(player, message);
			}
		}
	}
}
