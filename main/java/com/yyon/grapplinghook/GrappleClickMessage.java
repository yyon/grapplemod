package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GrappleClickMessage implements IMessage {
   
	public int id;
	public boolean leftclick;
//	public double r;
//	public double x;
//	public double y;
//	public double z;
//	public double mx;
//	public double my;
//	public double mz;

    public GrappleClickMessage() { }

    public GrappleClickMessage(int id, boolean leftclick) {
    	this.id = id;
    	this.leftclick = leftclick;
//    	this.r = r;
//        this.x = x;
//        this.y = y;
//        this.z = z;
//        this.mx = mx;
//        this.my = my;
//        this.mz = mz;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.id = buf.readInt();
    	this.leftclick = buf.readBoolean();
//    	this.r = buf.readDouble();
//        this.x = buf.readDouble();
//        this.y = buf.readDouble();
//        this.z = buf.readDouble();
//        this.mx = buf.readDouble();
//        this.my = buf.readDouble();
//        this.mz = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.id);
    	buf.writeBoolean(this.leftclick);
//    	buf.writeDouble(this.r);
//        buf.writeDouble(this.x);
//        buf.writeDouble(this.y);
//        buf.writeDouble(this.z);
//        buf.writeDouble(this.mx);
//        buf.writeDouble(this.my);
//        buf.writeDouble(this.mz);
    }

    public static class Handler implements IMessageHandler<GrappleClickMessage, IMessage> {
    	public class runner implements Runnable {
    		GrappleClickMessage message;
    		MessageContext ctx;
    		public runner(GrappleClickMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
            	grapplemod.receiveGrappleClick(message.id, message.leftclick);
//            	Entity grapple = world.getEntityByID(message.id);
//            	if (grapple instanceof grappleArrow) {
//	            	((grappleArrow) grapple).clientAttach(message.x, message.y, message.z);
 //           	}
            }
    	}
    	
       
        @Override
        public IMessage onMessage(GrappleClickMessage message, MessageContext ctx) {
//            System.out.println(String.format("Received %s from %s", message.text, ctx.getServerHandler().playerEntity.getDisplayName()));
        	IThreadListener mainThread = Minecraft.getMinecraft(); // or Minecraft.getMinecraft() on the client
            mainThread.addScheduledTask(new runner(message, ctx));
            
        	//            Entity arrowentity = world.getEntityByID(message.arrowId);
//            if (arrowentity instanceof grappleArrow) {
//            	((grappleArrow) arrowentity).receivePlayerMovementMessage(message.strafe, message.forward);
//            }
            return null; // no response in this case
        }
    }
}