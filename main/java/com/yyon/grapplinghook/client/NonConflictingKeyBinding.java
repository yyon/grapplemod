package com.yyon.grapplinghook.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.IKeyConflictContext;

public class NonConflictingKeyBinding extends KeyBinding {
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

	public NonConflictingKeyBinding(String description, InputMappings.Type type, int keyCode, String category) {
		super(description, type, keyCode, category);
		this.setNonConflict();
	}

   public boolean same(KeyBinding p_197983_1_) {
	   return false;
   }
   public boolean hasKeyCodeModifierConflict(KeyBinding other) {
	   return true;
   }
   
   public boolean is_down = false;
   
   public boolean isDown() {
	   return is_down;
   }
   
   @Override
   public void setDown(boolean value) {
	   this.is_down = value;
   }
}
