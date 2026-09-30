package com.yyon.grapplemod.mixin.client;

import com.yyon.grapplemod.client.CrosshairRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
	@Inject(method = "renderCrosshair", at = @At("TAIL"))
	private void grapplemod$renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (CrosshairRenderer.instance != null) {
			CrosshairRenderer.instance.onRenderCrosshair(guiGraphics);
		}
	}
}
