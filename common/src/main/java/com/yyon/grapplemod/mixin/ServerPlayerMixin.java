package com.yyon.grapplemod.mixin;

import com.yyon.grapplemod.common.CommonEventHandlers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
	@Inject(method = "die", at = @At("HEAD"))
	private void grapplemod$onDeath(DamageSource source, CallbackInfo ci) {
		CommonEventHandlers.onLivingDeath((ServerPlayer) (Object) this);
	}

	@Inject(method = "drop(Z)Z", at = @At("HEAD"))
	private void grapplemod$onDrop(boolean dropStack, CallbackInfoReturnable<Boolean> cir) {
		CommonEventHandlers.onPlayerDrop((ServerPlayer) (Object) this);
	}
}
