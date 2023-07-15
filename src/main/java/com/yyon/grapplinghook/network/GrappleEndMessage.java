package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.server.ServerControllerManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;

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

public class GrappleEndMessage extends BaseMessageServer {
   
	public int entityId;
	public HashSet<Integer> hookEntityIds;

    public GrappleEndMessage(FriendlyByteBuf buf) {
    	super(buf);
    }

    public GrappleEndMessage(int entityId, HashSet<Integer> hookEntityIds) {
    	this.entityId = entityId;
    	this.hookEntityIds = hookEntityIds;
    }

    public void decode(FriendlyByteBuf buf) {
    	this.entityId = buf.readInt();
    	int size = buf.readInt();
    	this.hookEntityIds = new HashSet<Integer>();
    	for (int i = 0; i < size; i++) {
    		this.hookEntityIds.add(buf.readInt());
    	}
    }

    public void encode(FriendlyByteBuf buf) {
    	buf.writeInt(this.entityId);
    	buf.writeInt(this.hookEntityIds.size());
    	for (int id : this.hookEntityIds) {
        	buf.writeInt(id);
    	}
    }

    public void processMessage(NetworkEvent.Context ctx) {
		int id = this.entityId;
		
		ServerPlayer player = ctx.getSender();
		if (player == null) {
			return;
		}
		Level w = player.level();
		
		ServerControllerManager.receiveGrappleEnd(id, w, this.hookEntityIds);
    }
}
