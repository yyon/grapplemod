package com.yyon.grapplinghook;

import net.minecraft.client.settings.KeyBinding;

public class NonConflictingKeyBinding extends KeyBinding {

	public NonConflictingKeyBinding(String description, int keyCode, String category) {
		super(description, keyCode, category);
	}

	@Override
    public boolean isActiveAndMatches(int keyCode) {
		return false;
	}

}
