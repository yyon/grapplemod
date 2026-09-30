package com.yyon.grapplemod.network;

import com.yyon.grapplemod.client.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

public class DetachSingleHookMessage extends BaseMessageClient {
	public static final CustomPacketPayload.Type<DetachSingleHookMessage> TYPE = GrappleNetwork.type("detach_single_hook");
	public static final StreamCodec<RegistryFriendlyByteBuf, DetachSingleHookMessage> STREAM_CODEC = CustomPacketPayload.codec(DetachSingleHookMessage::encode, DetachSingleHookMessage::new);

	public int id;
	public int hookid;

    public DetachSingleHookMessage(FriendlyByteBuf buf) {
    	super(buf);
    }

    public DetachSingleHookMessage(int id, int hookid) {
    	this.id = id;
    	this.hookid = hookid;
    }

    public void decode(FriendlyByteBuf buf) {
    	this.id = buf.readInt();
    	this.hookid = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
    	buf.writeInt(this.id);
    	buf.writeInt(this.hookid);
    }

    @Override
    public CustomPacketPayload.Type<DetachSingleHookMessage> type() {
    	return TYPE;
    }

    public void processMessage() {
    	ClientPacketHandler.handleDetachSingleHook(this);
    }
}
