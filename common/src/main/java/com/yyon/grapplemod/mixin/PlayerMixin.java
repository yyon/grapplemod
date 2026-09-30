package com.yyon.grapplemod.mixin;

import com.yyon.grapplemod.common.CommonEventHandlers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
	@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
	private void grapplemod$onAttack(Entity target, CallbackInfo ci) {
		if (CommonEventHandlers.cancelAttack((Player) (Object) this)) {
			ci.cancel();
		}
	}
}
