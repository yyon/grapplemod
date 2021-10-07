package com.yyon.grapplinghook.network;

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

public class DetachSingleHookMessage extends BaseMessage {
   
	public int id;
	public int hookid;

    public DetachSingleHookMessage(PacketBuffer buf) {
    	super(buf);
    }

    public DetachSingleHookMessage(int id, int hookid) {
    	this.id = id;
    	this.hookid = hookid;
    }

    public void decode(PacketBuffer buf) {
    	this.id = buf.readInt();
    	this.hookid = buf.readInt();
    }

    public void encode(PacketBuffer buf) {
    	buf.writeInt(this.id);
    	buf.writeInt(this.hookid);
    }
    
    public void processMessage(NetworkEvent.Context ctx) {
//    	grapplemod.receiveGrappleDetachHook(this.id, this.hookid);
    }
}
