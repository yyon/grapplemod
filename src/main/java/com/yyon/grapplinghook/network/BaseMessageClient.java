package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class BaseMessageClient {
	public BaseMessageClient(FriendlyByteBuf buf) {
		this.decode(buf);
	}
	
	public BaseMessageClient() {
	}
	
	public abstract void decode(FriendlyByteBuf buf);
	
	public abstract void encode(FriendlyByteBuf buf);

    @OnlyIn(Dist.CLIENT)
    public abstract void processMessage(NetworkEvent.Context ctx);
    
    public void onMessageReceived(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        if (sideReceived != LogicalSide.CLIENT) {
			GrappleMod.LOGGER.warn("message received on wrong side:" + ctx.getDirection().getReceptionSide());
			return;
        }
        
        ctx.setPacketHandled(true);
        
        ctx.enqueueWork(() -> 
        	ClientProxyInterface.proxy.onMessageReceivedClient(this, ctx)
        );
    }

}
