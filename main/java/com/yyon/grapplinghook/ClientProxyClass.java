package com.yyon.grapplinghook;

import java.util.ArrayList;
import java.util.List;

import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.network.BaseMessageClient;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ClientProxyClass implements CommonProxyClass {
	public ResourceLocation doubleJumpSoundLoc = new ResourceLocation("grapplemod", "doublejump");
	public ResourceLocation slideSoundLoc = new ResourceLocation("grapplemod", "slide");

	public ClientProxyClass() {
	}
	
	@Override
	public void startrocket(PlayerEntity player, GrappleCustomization custom) {
		if (!custom.rocket) return;
		
		if (!grapplemod.controllers.containsKey(player.getId())) {
			this.createControl(grapplemod.AIRID, -1, player.getId(), player.level, new vec(0,0,0), null, custom);
		} else {
			grappleController controller = grapplemod.controllers.get(player.getId());
			if (controller.custom == null || !controller.custom.rocket) {
				if (controller.custom == null) {controller.custom = custom;}
				controller.custom.rocket = true;
				controller.custom.rocket_active_time = custom.rocket_active_time;
				controller.custom.rocket_force = custom.rocket_force;
				controller.custom.rocket_refuel_ratio = custom.rocket_refuel_ratio;
				this.updateRocketRegen(custom.rocket_active_time, custom.rocket_refuel_ratio);
			}
		}
	}

	
	@Override
	public String getkeyname(mckeys keyenum) {
		KeyBinding binding = null;
		
		GameSettings gs = Minecraft.getInstance().options;
		
		if (keyenum == mckeys.keyBindAttack) {
			binding = gs.keyAttack;
		} else if (keyenum == mckeys.keyBindBack) {
			binding = gs.keyDown;
		} else if (keyenum == mckeys.keyBindForward) {
			binding = gs.keyUp;
		} else if (keyenum == mckeys.keyBindJump) {
			binding = gs.keyJump;
		} else if (keyenum == mckeys.keyBindLeft) {
			binding = gs.keyLeft;
		} else if (keyenum == mckeys.keyBindRight) {
			binding = gs.keyRight;
		} else if (keyenum == mckeys.keyBindSneak) {
			binding = gs.keyShift;
		} else if (keyenum == mckeys.keyBindUseItem) {
			binding = gs.keyUse;
		}
		
		if (binding == null) {
			return "";
		}
		
		String displayname = binding.getTranslatedKeyMessage().getString();
		if (displayname.equals("Button 1")) {
			return "Left Click";
		} else if (displayname.equals("Button 2")) {
			return "Right Click";
		} else {
			return displayname;
		}
	}

	@Override
	public void openModifierScreen(TileEntityGrappleModifier tileent) {
		Minecraft.getInstance().setScreen(new GuiModifier(tileent));

	}
	
	@Override
	public String localize(String string) {
		return I18n.get(string);
	}

	@Override
	public void onMessageReceivedClient(BaseMessageClient msg, Context ctx) {
		msg.processMessage(ctx);
	}


	@Override
	public void playSlideSound(Entity entity) {
		entity.playSound(new SoundEvent(this.slideSoundLoc), GrappleConfig.getclientconf().sounds.slide_sound_volume, 1.0F);
	}

	@Override
	public void playDoubleJumpSound(Entity entity) {
		entity.playSound(new SoundEvent(this.doubleJumpSoundLoc), GrappleConfig.getclientconf().sounds.doublejump_sound_volume * 0.7F, 1.0F);
	}

	@Override
	public void playWallrunJumpSound(Entity entity) {
		entity.playSound(new SoundEvent(this.doubleJumpSoundLoc), GrappleConfig.getclientconf().sounds.wallrunjump_sound_volume * 0.7F, 1.0F);
	}
	
	List<ItemStack> grapplinghookvariants = null;

	@Override
	public void fillGrappleVariants(ItemGroup tab, NonNullList<ItemStack> items) {
		if (Minecraft.getInstance().isRunning() == false || Minecraft.getInstance().player == null || Minecraft.getInstance().player.level == null || Minecraft.getInstance().player.level.getRecipeManager() == null) {
			return;
		}
		
		if (grapplinghookvariants == null) {
			grapplinghookvariants = new ArrayList<ItemStack>();
			RecipeManager recipemanager = Minecraft.getInstance().player.level.getRecipeManager();
			recipemanager.getRecipeIds().filter(loc -> loc.getNamespace().equals(grapplemod.MODID)).forEach(loc -> {
				ItemStack stack = recipemanager.byKey(loc).get().getResultItem();
				if (stack.getItem() instanceof grappleBow) {
					if (!grapplemod.grapplebowitem.getCustomization(stack).equals(new GrappleCustomization())) {
						grapplinghookvariants.add(stack);
					}
				}
			});
		}
		
		items.addAll(grapplinghookvariants);
	}
	
	public Screen onConfigScreen(Minecraft mc, Screen screen) {
		return AutoConfig.getConfigScreen(GrappleConfig.class, screen).get();
	}

	@Override
	public void resetlaunchertime(int playerid) {
		ClientControllerManager.instance.resetlaunchertime(playerid);
	}

	@Override
	public void launchplayer(PlayerEntity player) {
		ClientControllerManager.instance.launchplayer(player);
	}

	@Override
	public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio) {
		ClientControllerManager.instance.updateRocketRegen(rocket_active_time, rocket_refuel_ratio);
	}

	@Override
	public double getRocketFunctioning() {
		return ClientControllerManager.instance.getRocketFunctioning();
	}

	@Override
	public boolean iswallrunning(Entity entity, vec motion) {
		return ClientControllerManager.instance.iswallrunning(entity, motion);
	}

	@Override
	public boolean issliding(Entity entity, vec motion) {
		return ClientControllerManager.instance.issliding(entity, motion);
	}

	@Override
	public grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, BlockPos blockpos,
			GrappleCustomization custom) {
		return ClientControllerManager.instance.createControl(id, arrowid, entityid, world, pos, blockpos, custom);
	}

	@Override
	public boolean isKeyDown(grapplekeys key) {
		if (key == CommonProxyClass.grapplekeys.key_boththrow) {return ClientSetup.key_boththrow.isDown();}
		else if (key == CommonProxyClass.grapplekeys.key_leftthrow) {return ClientSetup.key_leftthrow.isDown();}
		else if (key == CommonProxyClass.grapplekeys.key_rightthrow) {return ClientSetup.key_rightthrow.isDown();}
		else if (key == CommonProxyClass.grapplekeys.key_motoronoff) {return ClientSetup.key_motoronoff.isDown();}
		else if (key == CommonProxyClass.grapplekeys.key_jumpanddetach) {return ClientSetup.key_jumpanddetach.isDown();}
		else if (key == CommonProxyClass.grapplekeys.key_slow) {return ClientSetup.key_slow.isDown();}
		else if (key == CommonProxyClass.grapplekeys.key_climb) {return ClientSetup.key_climb.isDown();}
		else if (key == CommonProxyClass.grapplekeys.key_climbup) {return ClientSetup.key_climbup.isDown();}
		else if (key == CommonProxyClass.grapplekeys.key_climbdown) {return ClientSetup.key_climbdown.isDown();}
		else if (key == CommonProxyClass.grapplekeys.key_enderlaunch) {return ClientSetup.key_enderlaunch.isDown();}
		else if (key == CommonProxyClass.grapplekeys.key_rocket) {return ClientSetup.key_rocket.isDown();}
		else if (key == CommonProxyClass.grapplekeys.key_slide) {return ClientSetup.key_slide.isDown();}
		return false;
	}
}
