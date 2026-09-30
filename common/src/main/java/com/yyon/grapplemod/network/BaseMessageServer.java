package com.yyon.grapplemod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public abstract class BaseMessageServer implements CustomPacketPayload {
	public BaseMessageServer(FriendlyByteBuf buf) {
		this.decode(buf);
	}

	public BaseMessageServer() {
	}

	public abstract void decode(FriendlyByteBuf buf);

	public abstract void encode(FriendlyByteBuf buf);

	public abstract void processMessage(ServerPlayer player);
}
