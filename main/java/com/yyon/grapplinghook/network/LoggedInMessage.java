package com.yyon.grapplinghook.network;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

import com.yyon.grapplinghook.GrappleConfig;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
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

public class LoggedInMessage implements IMessage {
    GrappleConfig.Config conf = null;

    public LoggedInMessage() { }
    
    public LoggedInMessage(GrappleConfig.Config serverconf) {
    	this.conf = serverconf;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	Class<GrappleConfig.Config> confclass = GrappleConfig.Config.class;
    	Field[] fields = confclass.getDeclaredFields();
    	Arrays.sort(fields, new Comparator<Field>() {
    	    @Override
    	    public int compare(Field o1, Field o2) {
    	        return o1.getName().compareTo(o2.getName());
    	    }
    	});
    	
    	this.conf = new GrappleConfig.Config();
    	
    	for (Field field : fields) {
    		Type fieldtype = field.getGenericType();
    		try {
        		if (fieldtype.getTypeName().equals("int")) {
        			field.setInt(this.conf, buf.readInt());
        		} else if (fieldtype.getTypeName().equals("double")) {
        			field.setDouble(this.conf, buf.readDouble());
        		} else if (fieldtype.getTypeName().equals("boolean")) {
        			field.setBoolean(this.conf, buf.readBoolean());
        		} else if (fieldtype.getTypeName().equals("java.lang.String")) {
        			int len = buf.readInt();
        			CharSequence charseq = buf.readCharSequence(len, Charset.defaultCharset());
        			field.set(this.conf, charseq.toString());
        		} else {
        			System.out.println("Unknown Type");
        			System.out.println(fieldtype.getTypeName());
        		}
    		} catch (IllegalAccessException e) {
    			System.out.println(e);
    		}
    	}
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	Class<GrappleConfig.Config> confclass = GrappleConfig.Config.class;
    	Field[] fields = confclass.getDeclaredFields();
    	Arrays.sort(fields, new Comparator<Field>() {
    	    @Override
    	    public int compare(Field o1, Field o2) {
    	        return o1.getName().compareTo(o2.getName());
    	    }
    	});
    	
    	for (Field field : fields) {
    		Type fieldtype = field.getGenericType();
    		try {
        		if (fieldtype.getTypeName().equals("int")) {
        			buf.writeInt(field.getInt(this.conf));
        		} else if (fieldtype.getTypeName().equals("double")) {
        			buf.writeDouble(field.getDouble(this.conf));
        		} else if (fieldtype.getTypeName().equals("boolean")) {
        			buf.writeBoolean(field.getBoolean(this.conf));
        		} else if (fieldtype.getTypeName().equals("java.lang.String")) {
        			String str = (String) field.get(this.conf);
        			buf.writeInt(str.length());
        			buf.writeCharSequence(str.subSequence(0, str.length()), Charset.defaultCharset());
        		} else {
        			System.out.println("Unknown Type");
        			System.out.println(fieldtype.getTypeName());
        		}
    		} catch (IllegalAccessException e) {
    			System.out.println(e);
    		}
    	}
    }

    public static class Handler implements IMessageHandler<LoggedInMessage, IMessage> {
    	public class runner implements Runnable {
    		LoggedInMessage message;
    		MessageContext ctx;
    		public runner(LoggedInMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
            	GrappleConfig.setserveroptions(this.message.conf);
            }
    	}
    	
       
        @Override
        public IMessage onMessage(LoggedInMessage message, MessageContext ctx) {

        	IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new runner(message, ctx));

            return null; 
        }
    }
}
