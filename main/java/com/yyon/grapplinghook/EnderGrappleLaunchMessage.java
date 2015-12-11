package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EnderGrappleLaunchMessage implements IMessage {
   
	public int id;
	public boolean leftclick;
//	public double r;
	public double x;
	public double y;
	public double z;
//	public double mx;
//	public double my;
//	public double mz;

    public EnderGrappleLaunchMessage() { }

    public EnderGrappleLaunchMessage(int id, double x, double y, double z) {
    	this.id = id;
//    	this.r = r;
        this.x = x;
        this.y = y;
        this.z = z;
//        this.mx = mx;
//        this.my = my;
//        this.mz = mz;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.id = buf.readInt();
    	this.leftclick = buf.readBoolean();
//    	this.r = buf.readDouble();
       this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
//        this.mx = buf.readDouble();
//        this.my = buf.readDouble();
//        this.mz = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.id);
    	buf.writeBoolean(this.leftclick);
//    	buf.writeDouble(this.r);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
//        buf.writeDouble(this.mx);
//        buf.writeDouble(this.my);
//        buf.writeDouble(this.mz);
    }

    public static class Handler implements IMessageHandler<EnderGrappleLaunchMessage, IMessage> {
    	public class runner implements Runnable {
    		EnderGrappleLaunchMessage message;
    		MessageContext ctx;
    		public runner(EnderGrappleLaunchMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
            	grapplemod.receiveEnderLaunch(message.id, message.x, message.y, message.z);
//            	Entity grapple = world.getEntityByID(message.id);
//            	if (grapple instanceof grappleArrow) {
//	            	((grappleArrow) grapple).clientAttach(message.x, message.y, message.z);
 //           	}
            }
    	}
    	
       
        @Override
        public IMessage onMessage(EnderGrappleLaunchMessage message, MessageContext ctx) {
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