package com.yyon.grapplemod.fabric.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.platform.InputConstants;
import com.yyon.grapplemod.client.NonConflictingKeyBinding;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {
	@WrapWithCondition(method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	private boolean grapplemod$keepOutOfMap(Map<Object, Object> map, Object key, Object value) {
		return !(key instanceof InputConstants.Key && value instanceof NonConflictingKeyBinding);
	}

	@WrapWithCondition(method = "resetMapping", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	private static boolean grapplemod$keepOutOfMapOnReset(Map<Object, Object> map, Object key, Object value) {
		return !(key instanceof InputConstants.Key && value instanceof NonConflictingKeyBinding);
	}

	@Inject(method = "same", at = @At("HEAD"), cancellable = true)
	private void grapplemod$neverConflict(KeyMapping binding, CallbackInfoReturnable<Boolean> cir) {
		if (binding instanceof NonConflictingKeyBinding) {
			cir.setReturnValue(false);
		}
	}
}
