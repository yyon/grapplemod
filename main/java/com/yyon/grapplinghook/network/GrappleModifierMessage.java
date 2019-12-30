package com.yyon.grapplinghook.network;

import java.util.function.Supplier;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;


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

public class GrappleModifierMessage {
   
	public BlockPos pos;
	public GrappleCustomization custom;

    public GrappleModifierMessage() { }

    public GrappleModifierMessage(BlockPos pos, GrappleCustomization custom) {
    	this.pos = pos;
    	this.custom = custom;
    }

    public static GrappleModifierMessage fromBytes(PacketBuffer buf) {
    	GrappleModifierMessage pkt = new GrappleModifierMessage();
    	pkt.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    	pkt.custom = new GrappleCustomization();
    	pkt.custom.readFromBuf(buf);
    	return pkt;
    }

    public static void toBytes(GrappleModifierMessage pkt, PacketBuffer buf) {
    	buf.writeInt(pkt.pos.getX());
    	buf.writeInt(pkt.pos.getY());
    	buf.writeInt(pkt.pos.getZ());
    	pkt.custom.writeToBuf(buf);
    }

    public static void handle(final GrappleModifierMessage message, Supplier<NetworkEvent.Context> ctx) {
    	ServerPlayerEntity pl = ctx.get().getSender();
        World world = pl.world;
		
		TileEntity ent = world.getTileEntity(message.pos);
		
		if (ent instanceof TileEntityGrappleModifier) {
			((TileEntityGrappleModifier) ent).setCustomizationServer(message.custom);
		}
	}
}
