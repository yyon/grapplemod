package com.yyon.grapplinghook.network;

import java.util.HashSet;
import java.util.function.Supplier;

import com.yyon.grapplinghook.grapplemod;
//* // 1.8 Compatability

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

public class GrappleEndMessage {
   
	public int entityid;
	public HashSet<Integer> arrowIds;

    public GrappleEndMessage() { }

    public GrappleEndMessage(int entityid, HashSet<Integer> arrowIds) {
    	this.entityid = entityid;
    	this.arrowIds = arrowIds;
    }

    public static GrappleEndMessage fromBytes(PacketBuffer buf) {
    	GrappleEndMessage pkt = new GrappleEndMessage();
    	pkt.entityid = buf.readInt();
    	int size = buf.readInt();
    	pkt.arrowIds = new HashSet<Integer>();
    	for (int i = 0; i < size; i++) {
    		pkt.arrowIds.add(buf.readInt());
    	}
    	return pkt;
    }

    public static void toBytes(GrappleEndMessage pkt, PacketBuffer buf) {
    	buf.writeInt(pkt.entityid);
    	buf.writeInt(pkt.arrowIds.size());
    	for (int id : pkt.arrowIds) {
        	buf.writeInt(id);
    	}
    }

    public static void handle(final GrappleEndMessage message, Supplier<NetworkEvent.Context> ctx) {
    	ServerPlayerEntity pl = ctx.get().getSender();
        World world = pl.world;

		int id = message.entityid;
		
		grapplemod.receiveGrappleEnd(id, world, message.arrowIds);
    }
}
