package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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

public class DummyMessage implements IMessage {
   
	public DummyMessage() {
	}
	
    @Override
    public void fromBytes(ByteBuf buf) {
    	System.out.println("ERROR! Reading Dummy Message!");
    	System.out.println(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	System.out.println("ERROR! Writing Dummy Message!");
    }

    public static class Handler implements IMessageHandler<DummyMessage, IMessage> {
       
        @Override
        public IMessage onMessage(DummyMessage message, MessageContext ctx) {
            return null; // no response in this case
        }
    }
}