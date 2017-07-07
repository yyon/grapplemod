package com.yyon.grapplinghook.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.yyon.grapplinghook.grapplemod;
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

public class MultiHookMessage implements IMessage {
   
	public int id;
	public boolean sneaking;

    public MultiHookMessage() { }

    public MultiHookMessage(int id, boolean sneaking) {
    	this.id = id;
    	this.sneaking = sneaking;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.id = buf.readInt();
    	this.sneaking = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.id);
    	buf.writeBoolean(this.sneaking);
    }

    public static class Handler implements IMessageHandler<MultiHookMessage, IMessage> {
    	public class runner implements Runnable {
    		MultiHookMessage message;
    		MessageContext ctx;
    		public runner(MultiHookMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
				int id = message.id;
				
				World w = ctx.getServerHandler().player.world;
				
				grapplemod.receiveMultihookMessage(id, w, message.sneaking);
            }
    	}
    	
       
        @Override
        public IMessage onMessage(MultiHookMessage message, MessageContext ctx) {

        	IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world; // or Minecraft.getMinecraft() on the client
            mainThread.addScheduledTask(new runner(message, ctx));

            return null; // no response in this case
        }
    }
}
