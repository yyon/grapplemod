package com.yyon.grapplinghook.network;

import java.util.function.Supplier;

import com.yyon.grapplinghook.entities.grappleArrow;
//* // 1.8 Compatability

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
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

public class GrappleAttachPosMessage {
   
	public int id;
	public double x;
	public double y;
	public double z;

    public GrappleAttachPosMessage() { }

    public GrappleAttachPosMessage(int id, double x, double y, double z) {
    	this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static GrappleAttachPosMessage fromBytes(PacketBuffer buf) {
    	GrappleAttachPosMessage pkt = new GrappleAttachPosMessage();
    	pkt.id = buf.readInt();
        pkt.x = buf.readDouble();
        pkt.y = buf.readDouble();
        pkt.z = buf.readDouble();
        return pkt;
    }

    public static void toBytes(GrappleAttachPosMessage pkt, PacketBuffer buf) {
    	buf.writeInt(pkt.id);
        buf.writeDouble(pkt.x);
        buf.writeDouble(pkt.y);
        buf.writeDouble(pkt.z);
    }

    public static void handle(final GrappleAttachPosMessage message, Supplier<NetworkEvent.Context> ctx) {
    	ServerPlayerEntity pl = ctx.get().getSender();
        World world = pl.world;
    	Entity grapple = world.getEntityByID(message.id);
    	if (grapple instanceof grappleArrow) {
        	((grappleArrow) grapple).setAttachPos(message.x, message.y, message.z);
    	}
    }
}
