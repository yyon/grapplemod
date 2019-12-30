package com.yyon.grapplinghook.network;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
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

public class PlayerMovementMessage {
   
	public int entityId;
	public double x;
	public double y;
	public double z;
	public double mx;
	public double my;
	public double mz;
	
    public PlayerMovementMessage(int entityId, double x, double y, double z, double mx, double my, double mz) {
    	this.entityId = entityId;
    	this.x = x;
    	this.y = y;
    	this.z = z;
    	this.mx = mx;
    	this.my = my;
    	this.mz = mz;
    }

    public static PlayerMovementMessage fromBytes(PacketBuffer buf) {
		return new PlayerMovementMessage(
			buf.readInt(), 
			buf.readDouble(), 
			buf.readDouble(), 
			buf.readDouble(), 
			buf.readDouble(), 
			buf.readDouble(), 
			buf.readDouble()
		);
    }

    public static void toBytes(PlayerMovementMessage pkt, PacketBuffer buf) {
        buf.writeInt(pkt.entityId);
        buf.writeDouble(pkt.x);
        buf.writeDouble(pkt.y);
        buf.writeDouble(pkt.z);
        buf.writeDouble(pkt.mx);
        buf.writeDouble(pkt.my);
        buf.writeDouble(pkt.mz);
        
    }

    public static void handle(final PlayerMovementMessage message, Supplier<NetworkEvent.Context> ctx) {
    	ServerPlayerEntity pl = ctx.get().getSender();
        World world = pl.world;
        Entity entity = world.getEntityByID(message.entityId);
        if (entity == null) {return;}
        entity.posX = message.x;
        entity.posY = message.y;
        entity.posZ = message.z;
        entity.setMotion(message.x, message.y, message.z);
        if (entity instanceof ServerPlayerEntity) {
        	ServerPlayerEntity player = ((ServerPlayerEntity) entity);
        	player.connection.captureCurrentPosition();
        	
    		if (!player.onGround) {
            	if (message.my >= 0) {
            		player.fallDistance = 0;
            	} else {
            		double gravity = 0.05 * 2;
            		// d = v^2 / 2g
                	player.fallDistance = (float) (Math.pow(message.my, 2) / (2 * gravity));
            	}
    		}
        }
    }
}
