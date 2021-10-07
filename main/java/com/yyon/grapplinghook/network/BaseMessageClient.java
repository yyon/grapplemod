package com.yyon.grapplinghook.network;

import java.util.function.Supplier;

import com.yyon.grapplinghook.grapplemod;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public abstract class BaseMessageClient {
	public BaseMessageClient(PacketBuffer buf) {
		this.decode(buf);
	}
	
	public BaseMessageClient() {
	}
	
	public abstract void decode(PacketBuffer buf);
	
	public abstract void encode(PacketBuffer buf);

    @OnlyIn(Dist.CLIENT)
    public abstract void processMessage(NetworkEvent.Context ctx);
    
    public void onMessageReceived(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        if (sideReceived != LogicalSide.CLIENT) {
			grapplemod.LOGGER.warn("message received on wrong side:" + ctx.getDirection().getReceptionSide());
			return;
        }
        
        ctx.setPacketHandled(true);
        
        ctx.enqueueWork(() -> 
        	grapplemod.proxy.onMessageReceivedClient(this, ctx)
        );
    }
}
