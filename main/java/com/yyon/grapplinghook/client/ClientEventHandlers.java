package com.yyon.grapplinghook.client;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.controllers.AirfrictionController;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.controllers.ForcefieldController;
import com.yyon.grapplinghook.items.KeypressItem;
import com.yyon.grapplinghook.utils.Vec;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandlers {
	public static ClientEventHandlers instance = null;
	
	public ClientEventHandlers() {
	    MinecraftForge.EVENT_BUS.register(this);
	}

	public boolean prevKeys[] = {false, false, false, false, false};
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (player != null) {
			if (!Minecraft.getInstance().isPaused()) {
				ClientControllerManager.instance.onClientTick(player);
				
				if (Minecraft.getInstance().screen == null) {
					// keep in same order as enum from KeypressItem
					boolean keys[] = {ClientSetup.key_enderlaunch.isDown(), ClientSetup.key_leftthrow.isDown(), ClientSetup.key_rightthrow.isDown(), ClientSetup.key_boththrow.isDown(), ClientSetup.key_rocket.isDown()};
					
					for (int i = 0; i < keys.length; i++) {
						boolean iskeydown = keys[i];
						boolean prevkey = prevKeys[i];
						
						if (iskeydown != prevkey) {
							KeypressItem.Keys key = KeypressItem.Keys.values()[i];
							
							ItemStack stack = getKeypressStack(player);
							if (stack != null) {
								if (!isLookingAtModifierBlock(player)) {
									if (iskeydown) {
										((KeypressItem) stack.getItem()).onCustomKeyDown(stack, player, key, true);
									} else {
										((KeypressItem) stack.getItem()).onCustomKeyUp(stack, player, key, true);
									}
								}
							}
						}
						
						prevKeys[i] = iskeydown;
					}
				}
			}
		}
	}
	
	@SubscribeEvent
    public void onBlockBreak(BreakEvent event) {
		if (event.getPos() != null) {
			if (ClientControllerManager.controllerPos.containsKey(event.getPos())) {
				GrappleController control = ClientControllerManager.controllerPos.get(event.getPos());

				control.unattach();
				
				ClientControllerManager.controllerPos.remove(event.getPos());
			}
		}
    }

	@SubscribeEvent
	public void onPlayerLoggedOutEvent(LoggedOutEvent e) {
		GrappleConfig.setServerOptions(null);
	}

	@SubscribeEvent
	public void onKeyInputEvent(KeyInputEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (!Minecraft.getInstance().isRunning() || player == null) {
			return;
		}
		
		GrappleController controller = null;
		if (ClientControllerManager.controllers.containsKey(player.getId())) {
			controller = ClientControllerManager.controllers.get(player.getId());
		}
		
		if (Minecraft.getInstance().options.keyJump.isDown()) {
			if (controller != null) {
				if (controller instanceof AirfrictionController && ((AirfrictionController) controller).wasSliding) {
					controller.slidingJump();
				}
			}
		}	

		ClientControllerManager.instance.checkSlide(Minecraft.getInstance().player);
	}
	
	@SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (!Minecraft.getInstance().isRunning() || player == null) {
			return;
		}
		
		int id = player.getId();
		if (ClientControllerManager.controllers.containsKey(id)) {
			MovementInput input = event.getMovementInput();
			GrappleController control = ClientControllerManager.controllers.get(id);
			control.receivePlayerMovementMessage(input.leftImpulse, input.forwardImpulse, input.jumping, input.shiftKeyDown);
			
			boolean overrideMovement = true;
			if (Minecraft.getInstance().player.isOnGround()) {
				if (!(control instanceof AirfrictionController) && !(control instanceof ForcefieldController)) {
					overrideMovement = false;
				}
			}
			
			if (overrideMovement) {
				input.jumping = false;
				input.down = false;
				input.up = false;
				input.left = false;
				input.right = false;
				input.forwardImpulse = 0;
				input.leftImpulse = 0;
//				input.sneak = false; // fix alternate throw angles
			}
		}
	}
	
	public float currentCameraTilt = 0;

	@SubscribeEvent
	public void onCameraSetup(CameraSetup event) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (!Minecraft.getInstance().isRunning() || player == null) {
			return;
		}

		int id = player.getId();
		int targetCameraTilt = 0;
		if (ClientControllerManager.controllers.containsKey(id)) {
			GrappleController controller = ClientControllerManager.controllers.get(id);
			if (controller instanceof AirfrictionController) {
				AirfrictionController afcontroller = (AirfrictionController) controller;
				if (afcontroller.wasWallrunning) {
					Vec walldirection = afcontroller.getWallDirection();
					if (walldirection != null) {
						Vec lookdirection = Vec.lookVec(player);
						int dir = lookdirection.cross(walldirection).y > 0 ? 1 : -1;
						targetCameraTilt = dir;
					}
				}
			}
		}
		
		if (currentCameraTilt != targetCameraTilt) {
			float cameraDiff = targetCameraTilt - currentCameraTilt;
			if (cameraDiff != 0) {
				float anim_s = GrappleConfig.getClientConf().camera.wallrun_camera_animation_s;
				float speed = (anim_s == 0) ? 9999 :  1.0f / (anim_s * 20.0f);
				if (speed > Math.abs(cameraDiff)) {
					currentCameraTilt = targetCameraTilt;
				} else {
					currentCameraTilt += speed * (cameraDiff > 0 ? 1 : -1);
				}
			}
		}
		
		if (currentCameraTilt != 0) {
		    event.setRoll(event.getRoll() + currentCameraTilt*GrappleConfig.getClientConf().camera.wallrun_camera_tilt_degrees);
		}
	}

	public ItemStack getKeypressStack(PlayerEntity player) {
		if (player != null) {
           ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
           if (stack != null) {
               Item item = stack.getItem();
               if (item instanceof KeypressItem) {
            	   return stack;
               }
           }
           
           stack = player.getItemInHand(Hand.OFF_HAND);
           if (stack != null) {
        	   Item item = stack.getItem();
        	   if (item instanceof KeypressItem) {
        		   return stack;
        	   }
           }
		}
		return null;
	}
	
	public boolean isLookingAtModifierBlock(PlayerEntity player) {
		RayTraceResult raytraceresult = Minecraft.getInstance().hitResult;
		if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
			BlockRayTraceResult bray = (BlockRayTraceResult) raytraceresult;
			BlockPos pos = bray.getBlockPos();
			BlockState state = player.level.getBlockState(pos);
			
			return (state.getBlock() == CommonSetup.grappleModifierBlock);
		}
		return false;
	}
	
	
}
