package com.yyon.grapplinghook.network;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;

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

public class PlayerMovementMessage extends BaseMessageServer {
   
	public int entityId;
	public double x;
	public double y;
	public double z;
	public double mx;
	public double my;
	public double mz;
	
	public PlayerMovementMessage(PacketBuffer buf) {
		super(buf);
	}
	
    public PlayerMovementMessage(int entityId, double x, double y, double z, double mx, double my, double mz) {
    	this.entityId = entityId;
    	this.x = x;
    	this.y = y;
    	this.z = z;
    	this.mx = mx;
    	this.my = my;
    	this.mz = mz;
    }

    public void decode(PacketBuffer buf) {
    	try {
	    	this.entityId = buf.readInt();
	    	this.x = buf.readDouble();
	    	this.y = buf.readDouble();
	    	this.z = buf.readDouble();
	    	this.mx = buf.readDouble();
	    	this.my = buf.readDouble();
	    	this.mz = buf.readDouble();
    	} catch (Exception e) {
    		System.out.print("Playermovement error: ");
    		System.out.println(buf);
    	}
    }

    public void encode(PacketBuffer buf) {
        buf.writeInt(entityId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(mx);
        buf.writeDouble(my);
        buf.writeDouble(mz);
        
    }

    public void processMessage(NetworkEvent.Context ctx) {
    	final ServerPlayerEntity referencedPlayer = ctx.getSender();
        if (referencedPlayer == null) {
          grapplemod.LOGGER.warn("EntityPlayerMP was null when KeypressMessage was received");
          return;
        }
        
		if(referencedPlayer.getId() == this.entityId) {
			new vec(this.x, this.y, this.z).setpos(referencedPlayer);
			new vec(this.mx, this.my, this.mz).setmotion(referencedPlayer);

			referencedPlayer.connection.resetPosition();
			
			if (!referencedPlayer.isOnGround()) {
				if (this.my >= 0) {
					referencedPlayer.fallDistance = 0;
				} else {
					double gravity = 0.05 * 2;
					// d = v^2 / 2g
					referencedPlayer.fallDistance = (float) (Math.pow(this.my, 2) / (2 * gravity));
				}
			}
		}
    }
}
