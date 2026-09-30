package com.yyon.grapplemod.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.yyon.grapplemod.mixin.client.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class NonConflictingKeyBinding extends KeyMapping {
	public NonConflictingKeyBinding(String description, int keyCode, String category) {
		super(description, keyCode, category);
	}

	public NonConflictingKeyBinding(String description, InputConstants.Type type, int keyCode, String category) {
		super(description, type, keyCode, category);
	}

	@Override
	public boolean same(KeyMapping other) {
		return false;
	}

	@Override
	public boolean isDown() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen != null) {
			return false;
		}

		InputConstants.Key key = ((KeyMappingAccessor) this).grapplemod$getKey();
		long window = mc.getWindow().getWindow();
		if (key.getType() == InputConstants.Type.KEYSYM) {
			return key.getValue() != InputConstants.UNKNOWN.getValue() && InputConstants.isKeyDown(window, key.getValue());
		} else if (key.getType() == InputConstants.Type.MOUSE) {
			return GLFW.glfwGetMouseButton(window, key.getValue()) == GLFW.GLFW_PRESS;
		}
		return false;
	}
}
