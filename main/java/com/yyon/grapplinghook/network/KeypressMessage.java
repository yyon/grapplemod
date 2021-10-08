package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.grapplemod;
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

public class KeypressMessage extends BaseMessageServer {
	
	KeypressItem.Keys key;
	boolean isDown;

    public KeypressMessage(PacketBuffer buf) {
    	super(buf);
    }

    public KeypressMessage(KeypressItem.Keys thekey, boolean isDown) {
    	this.key = thekey;
    	this.isDown = isDown;
    }

    public void decode(PacketBuffer buf) {
    	this.key = KeypressItem.Keys.values()[buf.readInt()];
    	this.isDown = buf.readBoolean();
    }

    public void encode(PacketBuffer buf) {
    	buf.writeInt(this.key.ordinal());
    	buf.writeBoolean(this.isDown);
    }

    public void processMessage(NetworkEvent.Context ctx) {
    	final ServerPlayerEntity sendingPlayer = ctx.getSender();
        if (sendingPlayer == null) {
          grapplemod.LOGGER.warn("EntityPlayerMP was null when KeypressMessage was received");
        }
        
		grapplemod.receiveKeypress(sendingPlayer, this.key, this.isDown);
	}
}
