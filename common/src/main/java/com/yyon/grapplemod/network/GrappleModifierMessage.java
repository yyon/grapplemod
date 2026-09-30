package com.yyon.grapplemod.network;

import com.yyon.grapplemod.blocks.modifierblock.TileEntityGrappleModifier;
import com.yyon.grapplemod.utils.GrappleCustomization;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

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

public class GrappleModifierMessage extends BaseMessageServer {
	public static final CustomPacketPayload.Type<GrappleModifierMessage> TYPE = GrappleNetwork.type("grapple_modifier");
	public static final StreamCodec<RegistryFriendlyByteBuf, GrappleModifierMessage> STREAM_CODEC = CustomPacketPayload.codec(GrappleModifierMessage::encode, GrappleModifierMessage::new);

	public BlockPos pos;
	public GrappleCustomization custom;

    public GrappleModifierMessage(BlockPos pos, GrappleCustomization custom) {
    	this.pos = pos;
    	this.custom = custom;
    }

	public GrappleModifierMessage(FriendlyByteBuf buf) {
		super(buf);
	}

    public void decode(FriendlyByteBuf buf) {
    	this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    	this.custom = new GrappleCustomization();
    	this.custom.readFromBuf(buf);
    }

    public void encode(FriendlyByteBuf buf) {
    	buf.writeInt(this.pos.getX());
    	buf.writeInt(this.pos.getY());
    	buf.writeInt(this.pos.getZ());
    	this.custom.writeToBuf(buf);
    }

    @Override
    public CustomPacketPayload.Type<GrappleModifierMessage> type() {
    	return TYPE;
    }

    public void processMessage(ServerPlayer player) {
		Level w = player.level();

		BlockEntity ent = w.getBlockEntity(this.pos);

		if (ent != null && ent instanceof TileEntityGrappleModifier) {
			((TileEntityGrappleModifier) ent).setCustomizationServer(this.custom);
		}
    }
}
