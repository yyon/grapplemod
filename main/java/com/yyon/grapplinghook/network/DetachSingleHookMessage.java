package com.yyon.grapplinghook.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.yyon.grapplinghook.grapplemod;

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

public class DetachSingleHookMessage implements IMessage {
   
	public int id;
	public int hookid;

    public DetachSingleHookMessage() { }

    public DetachSingleHookMessage(int id, int hookid) {
    	this.id = id;
    	this.hookid = hookid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.id = buf.readInt();
    	this.hookid = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.id);
    	buf.writeInt(this.hookid);
    }

    public static class Handler implements IMessageHandler<DetachSingleHookMessage, IMessage> {
    	public class runner implements Runnable {
    		DetachSingleHookMessage message;
    		MessageContext ctx;
    		public runner(DetachSingleHookMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
            	grapplemod.receiveGrappleDetachHook(message.id, message.hookid);
            }
    	}
    	
       
        @Override
        public IMessage onMessage(DetachSingleHookMessage message, MessageContext ctx) {

        	IThreadListener mainThread = Minecraft.getMinecraft(); // or Minecraft.getMinecraft() on the client
            mainThread.addScheduledTask(new runner(message, ctx));

            return null; // no response in this case
        }
    }
}
