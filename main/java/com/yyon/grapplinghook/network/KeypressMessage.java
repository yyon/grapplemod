package com.yyon.grapplinghook.network;

import java.util.function.Supplier;

import com.yyon.grapplinghook.grapplemod;
//* // 1.8 Compatability
import com.yyon.grapplinghook.items.KeypressItem;

import net.minecraft.entity.player.ServerPlayerEntity;
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

public class KeypressMessage {
	
	KeypressItem.Keys key;
	boolean isDown;

    public KeypressMessage() { }

    public KeypressMessage(KeypressItem.Keys thekey, boolean isDown) {
    	this.key = thekey;
    	this.isDown = isDown;
    }

    public static KeypressMessage fromBytes(PacketBuffer buf) {
    	KeypressMessage pkt = new KeypressMessage();
    	pkt.key = KeypressItem.Keys.values()[buf.readInt()];
    	pkt.isDown = buf.readBoolean();
    	return pkt;
    }

    public static void toBytes(KeypressMessage pkt, PacketBuffer buf) {
    	buf.writeInt(pkt.key.ordinal());
    	buf.writeBoolean(pkt.isDown);
    }

    public static void handle(final KeypressMessage message, Supplier<NetworkEvent.Context> ctx) {
    	ServerPlayerEntity pl = ctx.get().getSender();
		grapplemod.receiveKeypress(pl, message.key, message.isDown);
    }
}
