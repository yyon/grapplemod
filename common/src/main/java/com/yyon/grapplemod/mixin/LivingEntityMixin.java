package com.yyon.grapplemod.mixin;

import com.yyon.grapplemod.common.CommonEventHandlers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "die", at = @At("HEAD"))
	private void grapplemod$onDeath(DamageSource source, CallbackInfo ci) {
		CommonEventHandlers.onLivingDeath((LivingEntity) (Object) this);
	}

	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	private void grapplemod$onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (CommonEventHandlers.cancelHurt((LivingEntity) (Object) this, source)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
	private void grapplemod$onFall(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		if (CommonEventHandlers.cancelFall((LivingEntity) (Object) this)) {
			cir.setReturnValue(false);
		}
	}
}
