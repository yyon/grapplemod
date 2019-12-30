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

public class DetachSingleHookMessage {
   
	public int id;
	public int hookid;

    public DetachSingleHookMessage(int id, int hookid) {
    	this.id = id;
    	this.hookid = hookid;
    }

    public static DetachSingleHookMessage fromBytes(PacketBuffer buf) {
    	return new DetachSingleHookMessage(
			buf.readInt(),
			buf.readInt()
    	);
    }

    public static void toBytes(DetachSingleHookMessage pkt, PacketBuffer buf) {
    	buf.writeInt(pkt.id);
    	buf.writeInt(pkt.hookid);
    }

    public static void handle(final DetachSingleHookMessage message, Supplier<NetworkEvent.Context> ctx) {
    	grapplemod.receiveGrappleDetachHook(message.id, message.hookid);
    }
}
