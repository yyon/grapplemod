package com.yyon.grapplemod.mixin.client;

import com.yyon.grapplemod.client.ClientEventHandlers;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
	@Inject(method = "keyPress", at = @At("TAIL"))
	private void grapplemod$onKeyPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
		if (ClientEventHandlers.instance != null) {
			ClientEventHandlers.instance.onKeyInputEvent();
		}
	}
}
