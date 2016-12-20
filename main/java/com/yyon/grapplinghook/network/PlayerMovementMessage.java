package com.yyon.grapplinghook.network;

import java.lang.reflect.Field;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
//* // 1.8 Compatability


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

public class PlayerMovementMessage implements IMessage {
   
	public int entityId;
	public double x;
	public double y;
	public double z;
	public double mx;
	public double my;
	public double mz;
	
	public PlayerMovementMessage() {
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

    @Override
    public void fromBytes(ByteBuf buf) {
    	try {
	    	this.entityId = buf.readInt();
	    	this.x = buf.readDouble();
	    	this.y = buf.readDouble();
	    	this.z = buf.readDouble();
	    	this.mx = buf.readDouble();
	    	this.my = buf.readDouble();
	    	this.mz = buf.readDouble();
    	} catch (Exception e) {
    		System.out.println(buf);
    	}
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(mx);
        buf.writeDouble(my);
        buf.writeDouble(mz);
        
    }

    public static class Handler implements IMessageHandler<PlayerMovementMessage, IMessage> {
       
		private Field firstGoodX;
		private Field firstGoodY;
		private Field firstGoodZ;
		private boolean failed;
		
    	public class runner implements Runnable {
    		PlayerMovementMessage message;
    		MessageContext ctx;
    		public runner(PlayerMovementMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
                World world = ctx.getServerHandler().playerEntity.worldObj;
                Entity entity = world.getEntityByID(message.entityId);
                if (entity == null) {return;}
                entity.posX = message.x;
                entity.posY = message.y;
                entity.posZ = message.z;
                entity.motionX = message.mx;
                entity.motionY = message.my;
                entity.motionZ = message.mz;
                if (entity instanceof EntityPlayerMP) {
                    EntityPlayerMP player = ((EntityPlayerMP) entity);
                    if (firstGoodX == null) {
                        if (!failed) {
                            try {
                                firstGoodX = NetHandlerPlayServer.class.getDeclaredField("field_184349_l");
                                firstGoodY = NetHandlerPlayServer.class.getDeclaredField("field_184350_m");
                                firstGoodZ = NetHandlerPlayServer.class.getDeclaredField("field_184351_n");
                                firstGoodX.setAccessible(true);
                                firstGoodY.setAccessible(true);
                                firstGoodZ.setAccessible(true);
                                System.out.println("Was able to access firstGoodX!");
                            } catch (Exception e) {
                                failed = true;
                                System.out.println("Couldn't access firstGoodX!");
                            }
                        }
                    }
                    if (firstGoodX != null) {
                        try {
                            firstGoodX.set(player.connection, entity.posX);
                            firstGoodY.set(player.connection, entity.posY);
                            firstGoodZ.set(player.connection, entity.posZ);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    	}
    	
        @Override
        public IMessage onMessage(PlayerMovementMessage message, MessageContext ctx) {
        	IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
            mainThread.addScheduledTask(new runner(message, ctx));
            
            return null; // no response in this case
        }
    }
}
