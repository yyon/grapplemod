package com.yyon.grapplemod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class BaseMessageClient implements CustomPacketPayload {
	public BaseMessageClient(FriendlyByteBuf buf) {
		this.decode(buf);
	}

	public BaseMessageClient() {
	}

	public abstract void decode(FriendlyByteBuf buf);

	public abstract void encode(FriendlyByteBuf buf);

	public abstract void processMessage();
}
