package com.yyon.grapplinghook.network;

import java.util.HashSet;

import com.yyon.grapplinghook.server.ServerControllerManager;

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

public class GrappleEndMessage extends BaseMessageServer {
   
	public int entityId;
	public HashSet<Integer> hookEntityIds;

    public GrappleEndMessage(PacketBuffer buf) {
    	super(buf);
    }

    public GrappleEndMessage(int entityId, HashSet<Integer> hookEntityIds) {
    	this.entityId = entityId;
    	this.hookEntityIds = hookEntityIds;
    }

    public void decode(PacketBuffer buf) {
    	this.entityId = buf.readInt();
    	int size = buf.readInt();
    	this.hookEntityIds = new HashSet<Integer>();
    	for (int i = 0; i < size; i++) {
    		this.hookEntityIds.add(buf.readInt());
    	}
    }

    public void encode(PacketBuffer buf) {
    	buf.writeInt(this.entityId);
    	buf.writeInt(this.hookEntityIds.size());
    	for (int id : this.hookEntityIds) {
        	buf.writeInt(id);
    	}
    }

    public void processMessage(NetworkEvent.Context ctx) {
		int id = this.entityId;
		
		ServerPlayerEntity player = ctx.getSender();
		if (player == null) {
			return;
		}
		World w = player.level;
		
		ServerControllerManager.receiveGrappleEnd(id, w, this.hookEntityIds);
    }
}
