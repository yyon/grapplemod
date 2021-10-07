package com.yyon.grapplinghook.network;

import java.util.HashSet;

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

public class GrappleEndMessage extends BaseMessageServer {
   
	public int entityid;
	public HashSet<Integer> arrowIds;

    public GrappleEndMessage(PacketBuffer buf) {
    	super(buf);
    }

    public GrappleEndMessage(int entityid, HashSet<Integer> arrowIds) {
    	this.entityid = entityid;
    	this.arrowIds = arrowIds;
    }

    public void decode(PacketBuffer buf) {
    	this.entityid = buf.readInt();
    	int size = buf.readInt();
    	this.arrowIds = new HashSet<Integer>();
    	for (int i = 0; i < size; i++) {
    		this.arrowIds.add(buf.readInt());
    	}
    }

    public void encode(PacketBuffer buf) {
    	buf.writeInt(this.entityid);
    	buf.writeInt(this.arrowIds.size());
    	for (int id : this.arrowIds) {
        	buf.writeInt(id);
    	}
    }

    public void processMessage(NetworkEvent.Context ctx) {
    	/*
		int id = this.entityid;
		
		World w = ctx.getServerHandler().player.world;
		
		grapplemod.receiveGrappleEnd(id, w, this.arrowIds);
		*/
    }
}
