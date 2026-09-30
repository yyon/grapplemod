package com.yyon.grapplemod.network;

import com.yyon.grapplemod.client.ClientPacketHandler;
import com.yyon.grapplemod.utils.Vec;
import net.minecraft.core.Direction;
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

public class SegmentMessage extends BaseMessageClient {
	public static final CustomPacketPayload.Type<SegmentMessage> TYPE = GrappleNetwork.type("segment");
	public static final StreamCodec<RegistryFriendlyByteBuf, SegmentMessage> STREAM_CODEC = CustomPacketPayload.codec(SegmentMessage::encode, SegmentMessage::new);

	public int id;
	public boolean add;
	public int index;
	public Vec pos;
	public Direction topFacing;
	public Direction bottomFacing;

    public SegmentMessage(FriendlyByteBuf buf) {
    	super(buf);
    }

    public SegmentMessage(int id, boolean add, int index, Vec pos, Direction topfacing, Direction bottomfacing) {
    	this.id = id;
    	this.add = add;
    	this.index = index;
    	this.pos = pos;
    	this.topFacing = topfacing;
    	this.bottomFacing = bottomfacing;
    }

    public void decode(FriendlyByteBuf buf) {
    	this.id = buf.readInt();
    	this.add = buf.readBoolean();
    	this.index = buf.readInt();
    	this.pos = new Vec(buf.readDouble(), buf.readDouble(), buf.readDouble());
    	this.topFacing = buf.readEnum(Direction.class);
    	this.bottomFacing = buf.readEnum(Direction.class);
    }

    public void encode(FriendlyByteBuf buf) {
    	buf.writeInt(this.id);
    	buf.writeBoolean(this.add);
    	buf.writeInt(this.index);
    	buf.writeDouble(pos.x);
    	buf.writeDouble(pos.y);
    	buf.writeDouble(pos.z);
    	buf.writeEnum(this.topFacing);
    	buf.writeEnum(this.bottomFacing);
    }

    @Override
    public CustomPacketPayload.Type<SegmentMessage> type() {
    	return TYPE;
    }

    public void processMessage() {
    	ClientPacketHandler.handleSegment(this);
    }
}
