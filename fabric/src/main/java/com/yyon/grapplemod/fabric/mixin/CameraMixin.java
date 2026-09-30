package com.yyon.grapplemod.fabric.mixin;

import com.yyon.grapplemod.client.ClientEventHandlers;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {
	@Inject(method = "setup", at = @At("HEAD"))
	private void grapplemod$updateTilt(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
		if (ClientEventHandlers.instance != null) {
			ClientEventHandlers.instance.updateCameraTilt();
		}
	}

	@ModifyArg(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"), index = 2)
	private float grapplemod$applyTilt(float roll) {
		if (ClientEventHandlers.instance == null) {
			return roll;
		}
		return roll - ClientEventHandlers.instance.cameraRoll * (float) (Math.PI / 180.0);
	}
}
