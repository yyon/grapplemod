package com.yyon.grapplinghook.network;

import java.util.function.Supplier;

import com.yyon.grapplinghook.grapplemod;

import net.minecraft.network.PacketBuffer;
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

public class GrappleDetachMessage {
   
	public int id;

    public GrappleDetachMessage() { }

    public GrappleDetachMessage(int id) {
    	this.id = id;
    }

    public static GrappleDetachMessage fromBytes(PacketBuffer buf) {
    	GrappleDetachMessage pkt = new GrappleDetachMessage();
    	pkt.id = buf.readInt();
    	return pkt;
    }

    public static void toBytes(GrappleDetachMessage pkt, PacketBuffer buf) {
    	buf.writeInt(pkt.id);
    }

    public static void handle(final GrappleDetachMessage message, Supplier<NetworkEvent.Context> ctx) {
    	grapplemod.receiveGrappleDetach(message.id);
	}
}
