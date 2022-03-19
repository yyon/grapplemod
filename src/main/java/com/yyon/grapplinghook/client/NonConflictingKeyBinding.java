package com.yyon.grapplinghook.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.IKeyConflictContext;

public class NonConflictingKeyBinding extends KeyMapping {
	public NonConflictingKeyBinding(String description, int keyCode, String category) {
		super(description, keyCode, category);
		this.setNonConflict();
	}

//	boolean isActive = false;
	private void setNonConflict() {
		this.setKeyConflictContext(new IKeyConflictContext() {
			@Override
			public boolean isActive() {
				return false;
			}
			@Override
			public boolean conflicts(IKeyConflictContext other) {
				return false;
			}
		});
	}

	public NonConflictingKeyBinding(String description, InputConstants.Type type, int keyCode, String category) {
		super(description, type, keyCode, category);
		this.setNonConflict();
	}

   public boolean same(KeyMapping p_197983_1_) {
	   return false;
   }
   public boolean hasKeyCodeModifierConflict(KeyMapping other) {
	   return true;
   }
   
   public boolean isDown = false;
   
   public boolean isDown() {
	   return isDown;
   }
   
   @Override
   public void setDown(boolean value) {
	   this.isDown = value;
   }
}
