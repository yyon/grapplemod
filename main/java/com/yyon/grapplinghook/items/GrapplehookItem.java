package com.yyon.grapplinghook.items;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.client.ClientSetup;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.entities.grapplearrow.GrapplehookEntity;
import com.yyon.grapplinghook.network.DetachSingleHookMessage;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import com.yyon.grapplinghook.network.KeypressMessage;
import com.yyon.grapplinghook.server.ServerControllerManager;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.GrapplemodUtils;
import com.yyon.grapplinghook.utils.Vec;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

public class GrapplehookItem extends Item implements KeypressItem {
	public static HashMap<Entity, GrapplehookEntity> grapplearrows1 = new HashMap<Entity, GrapplehookEntity>();
	public static HashMap<Entity, GrapplehookEntity> grapplearrows2 = new HashMap<Entity, GrapplehookEntity>();
	
	public GrapplehookItem() {
		super(new Item.Properties().stacksTo(1).tab(CommonSetup.tabGrapplemod).durability(GrappleConfig.getconf().grapplinghook.other.default_durability));
	}

	public boolean hasArrow(Entity entity) {
		GrapplehookEntity arrow1 = getArrowLeft(entity);
		GrapplehookEntity arrow2 = getArrowRight(entity);
		return (arrow1 != null) || (arrow2 != null);
	}

	public void setArrowLeft(Entity entity, GrapplehookEntity arrow) {
		GrapplehookItem.grapplearrows1.put(entity, arrow);
	}
	public void setArrowRight(Entity entity, GrapplehookEntity arrow) {
		GrapplehookItem.grapplearrows2.put(entity, arrow);
	}
	public GrapplehookEntity getArrowLeft(Entity entity) {
		if (GrapplehookItem.grapplearrows1.containsKey(entity)) {
			GrapplehookEntity arrow = GrapplehookItem.grapplearrows1.get(entity);
			if (arrow != null && arrow.isAlive()) {
				return arrow;
			}
		}
		return null;
	}
	public GrapplehookEntity getArrowRight(Entity entity) {
		if (GrapplehookItem.grapplearrows2.containsKey(entity)) {
			GrapplehookEntity arrow = GrapplehookItem.grapplearrows2.get(entity);
			if (arrow != null && arrow.isAlive()) {
				return arrow;
			}
		}
		return null;
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
        if (repair != null && repair.getItem().equals(Items.LEATHER)) return true;
        return super.isValidRepairItem(stack, repair);
	}


	@Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
    	return true;
    }
    
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		return true;
	}
	
	@Override
	public boolean canAttackBlock(BlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_,
			PlayerEntity p_195938_4_) {
		return false;
	}

	@Override
	public void onCustomKeyDown(ItemStack stack, PlayerEntity player, KeypressItem.Keys key, boolean ismainhand) {
		if (player.level.isClientSide) {
			if (key == KeypressItem.Keys.LAUNCHER) {
				if (this.getCustomization(stack).enderstaff) {
					ClientProxyInterface.proxy.launchplayer(player);
				}
			} else if (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT || key == KeypressItem.Keys.THROWBOTH) {
				CommonSetup.network.sendToServer(new KeypressMessage(key, true));
			} else if (key == KeypressItem.Keys.ROCKET) {
				GrappleCustomization custom = this.getCustomization(stack);
				if (custom.rocket) {
					ClientProxyInterface.proxy.startrocket(player, custom);
				}
			}
		} else {
			if (key == KeypressItem.Keys.THROWBOTH) {
	        	throwBoth(stack, player.level, player, ismainhand);
			} else if (key == KeypressItem.Keys.THROWLEFT) {
				GrapplehookEntity arrow1 = getArrowLeft(player);

	    		if (arrow1 != null) {
	    			detachLeft(player);
		    		return;
				}
				
				stack.hurtAndBreak(1, (ServerPlayerEntity) player, (p) -> {});
				if (stack.getCount() <= 0) {
					return;
				}
				
				boolean threw = throwLeft(stack, player.level, player, ismainhand);

				if (threw) {
			        player.level.playSound((PlayerEntity) null, player.position().x, player.position().y, player.position().z, SoundEvents.ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (Item.random.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
				}
			} else if (key == KeypressItem.Keys.THROWRIGHT) {
				GrapplehookEntity arrow2 = getArrowRight(player);

	    		if (arrow2 != null) {
	    			detachRight(player);
		    		return;
				}
				
				stack.hurtAndBreak(1, (ServerPlayerEntity) player, (p) -> {});
				if (stack.getCount() <= 0) {
					return;
				}
				
				throwRight(stack, player.level, player, ismainhand);

		        player.level.playSound((PlayerEntity) null, player.position().x, player.position().y, player.position().z, SoundEvents.ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (Item.random.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
			}
		}
	}
	
	@Override
	public void onCustomKeyUp(ItemStack stack, PlayerEntity player, KeypressItem.Keys key, boolean ismainhand) {
		if (player.level.isClientSide) {
			if (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT || key == KeypressItem.Keys.THROWBOTH) {
				CommonSetup.network.sendToServer(new KeypressMessage(key, false));
			}
		} else {
	    	GrappleCustomization custom = this.getCustomization(stack);
	    	
	    	if (custom.detachonkeyrelease) {
	    		GrapplehookEntity arrow_left = getArrowLeft(player);
	    		GrapplehookEntity arrow_right = getArrowRight(player);
	    		
				if (key == KeypressItem.Keys.THROWBOTH) {
					detachBoth(player);
				} else if (key == KeypressItem.Keys.THROWLEFT) {
		    		if (arrow_left != null) detachLeft(player);
				} else if (key == KeypressItem.Keys.THROWRIGHT) {
		    		if (arrow_right != null) detachRight(player);
				}
	    	}
		}
	}

	public void throwBoth(ItemStack stack, World worldIn, LivingEntity entityLiving, boolean righthand) {
		GrapplehookEntity arrow_left = getArrowLeft(entityLiving);
		GrapplehookEntity arrow_right = getArrowRight(entityLiving);

		if (arrow_left != null || arrow_right != null) {
			detachBoth(entityLiving);
    		return;
		}

		stack.hurtAndBreak(1, (ServerPlayerEntity) entityLiving, (p) -> {});
		if (stack.getCount() <= 0) {
			return;
		}

    	GrappleCustomization custom = this.getCustomization(stack);
  		double angle = custom.angle;
//  		double verticalangle = custom.verticalthrowangle;
  		if (entityLiving.isCrouching()) {
  			angle = custom.sneakingangle;
//  			verticalangle = custom.sneakingverticalthrowangle;
  		}

    	if (!(!custom.doublehook || angle == 0)) {
    		throwLeft(stack, worldIn, entityLiving, righthand);
    	}
		throwRight(stack, worldIn, entityLiving, righthand);

		entityLiving.level.playSound((PlayerEntity) null, entityLiving.position().x, entityLiving.position().y, entityLiving.position().z, SoundEvents.ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (Item.random.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
	}
	
	public boolean throwLeft(ItemStack stack, World worldIn, LivingEntity entityLiving, boolean righthand) {
    	GrappleCustomization custom = this.getCustomization(stack);
    	
  		double angle = custom.angle;
  		double verticalangle = custom.verticalthrowangle;
  		
  		if (entityLiving.isCrouching()) {
  			angle = custom.sneakingangle;
  			verticalangle = custom.sneakingverticalthrowangle;
  		}
  		
  		LivingEntity player = entityLiving;
  		
  		Vec anglevec = Vec.fromAngles(Math.toRadians(-angle), Math.toRadians(verticalangle)); //new vec(0,0,1).rotate_yaw(Math.toRadians(angle)).rotate_pitch(Math.toRadians(verticalangle));
  		anglevec = anglevec.rotate_pitch(Math.toRadians(-player.getViewXRot(1.0F)));
  		anglevec = anglevec.rotate_yaw(Math.toRadians(player.getViewYRot(1.0F)));
        float velx = -MathHelper.sin((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
        float vely = -MathHelper.sin((float) anglevec.getPitch() * 0.017453292F);
        float velz = MathHelper.cos((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
		GrapplehookEntity entityarrow = this.createarrow(stack, worldIn, entityLiving, false, true);// new grappleArrow(worldIn, player, false);
        float extravelocity = (float) Vec.motionvec(entityLiving).dist_along(new Vec(velx, vely, velz));
        if (extravelocity < 0) { extravelocity = 0; }
        entityarrow.shoot((double) velx, (double) vely, (double) velz, entityarrow.getVelocity() + extravelocity, 0.0F);
        
		worldIn.addFreshEntity(entityarrow);
		setArrowLeft(entityLiving, entityarrow);    			
		
		return true;
	}
	
	public void throwRight(ItemStack stack, World worldIn, LivingEntity entityLiving, boolean righthand) {
    	GrappleCustomization custom = this.getCustomization(stack);
    	
  		double angle = custom.angle;
  		double verticalangle = custom.verticalthrowangle;
  		if (entityLiving.isCrouching()) {
  			angle = custom.sneakingangle;
  			verticalangle = custom.sneakingverticalthrowangle;
  		}

    	if (!custom.doublehook || angle == 0) {
			GrapplehookEntity entityarrow = this.createarrow(stack, worldIn, entityLiving, righthand, false);
      		Vec anglevec = new Vec(0,0,1).rotate_pitch(Math.toRadians(verticalangle));
      		anglevec = anglevec.rotate_pitch(Math.toRadians(-entityLiving.getViewXRot(1.0F)));
      		anglevec = anglevec.rotate_yaw(Math.toRadians(entityLiving.getViewYRot(1.0F)));
	        float velx = -MathHelper.sin((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
	        float vely = -MathHelper.sin((float) anglevec.getPitch() * 0.017453292F);
	        float velz = MathHelper.cos((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
	        float extravelocity = (float) Vec.motionvec(entityLiving).dist_along(new Vec(velx, vely, velz));
	        if (extravelocity < 0) { extravelocity = 0; }
	        entityarrow.shoot((double) velx, (double) vely, (double) velz, entityarrow.getVelocity() + extravelocity, 0.0F);
			setArrowRight(entityLiving, entityarrow);
			worldIn.addFreshEntity(entityarrow);
    	} else {
      		LivingEntity player = entityLiving;
      		
      		Vec anglevec = Vec.fromAngles(Math.toRadians(angle), Math.toRadians(verticalangle)); //new vec(0,0,1).rotate_yaw(Math.toRadians(angle)).rotate_pitch(Math.toRadians(verticalangle));
      		anglevec = anglevec.rotate_pitch(Math.toRadians(-player.getViewXRot(1.0F)));
      		anglevec = anglevec.rotate_yaw(Math.toRadians(player.getViewYRot(1.0F)));
	        float velx = -MathHelper.sin((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
	        float vely = -MathHelper.sin((float) anglevec.getPitch() * 0.017453292F);
	        float velz = MathHelper.cos((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
			GrapplehookEntity entityarrow = this.createarrow(stack, worldIn, entityLiving, true, true);//new grappleArrow(worldIn, player, true);
//            entityarrow.shoot(player, (float) anglevec.getPitch(), (float)anglevec.getYaw(), 0.0F, entityarrow.getVelocity(), 0.0F);
	        float extravelocity = (float) Vec.motionvec(entityLiving).dist_along(new Vec(velx, vely, velz));
	        if (extravelocity < 0) { extravelocity = 0; }
	        entityarrow.shoot((double) velx, (double) vely, (double) velz, entityarrow.getVelocity() + extravelocity, 0.0F);
            
			worldIn.addFreshEntity(entityarrow);
			setArrowRight(entityLiving, entityarrow);
		}
	}
	
	public void detachBoth(LivingEntity entityLiving) {
		GrapplehookEntity arrow1 = getArrowLeft(entityLiving);
		GrapplehookEntity arrow2 = getArrowRight(entityLiving);

		setArrowLeft(entityLiving, null);
		setArrowRight(entityLiving, null);
		
		if (arrow1 != null) {
			arrow1.removeServer();
		}
		if (arrow2 != null) {
			arrow2.removeServer();
		}

		int id = entityLiving.getId();
		GrapplemodUtils.sendtocorrectclient(new GrappleDetachMessage(id), entityLiving.getId(), entityLiving.level);

		if (ServerControllerManager.attached.contains(id)) {
			ServerControllerManager.attached.remove(id);
		}
	}
	
	public void detachLeft(LivingEntity entityLiving) {
		GrapplehookEntity arrow1 = getArrowLeft(entityLiving);
		
		setArrowLeft(entityLiving, null);
		
		if (arrow1 != null) {
			arrow1.removeServer();
		}
		
		int id = entityLiving.getId();
		
		// remove controller if hook is attached
		if (getArrowRight(entityLiving) == null) {
			GrapplemodUtils.sendtocorrectclient(new GrappleDetachMessage(id), id, entityLiving.level);
		} else {
			GrapplemodUtils.sendtocorrectclient(new DetachSingleHookMessage(id, arrow1.getId()), id, entityLiving.level);
		}
		
		if (ServerControllerManager.attached.contains(id)) {
			ServerControllerManager.attached.remove(id);
		}
	}
	
	public void detachRight(LivingEntity entityLiving) {
		GrapplehookEntity arrow2 = getArrowRight(entityLiving);
		
		setArrowRight(entityLiving, null);
		
		if (arrow2 != null) {
			arrow2.removeServer();
		}
		
		int id = entityLiving.getId();
		
		// remove controller if hook is attached
		if (getArrowLeft(entityLiving) == null) {
			GrapplemodUtils.sendtocorrectclient(new GrappleDetachMessage(id), id, entityLiving.level);
		} else {
			GrapplemodUtils.sendtocorrectclient(new DetachSingleHookMessage(id, arrow2.getId()), id, entityLiving.level);
		}
		
		if (ServerControllerManager.attached.contains(id)) {
			ServerControllerManager.attached.remove(id);
		}
	}
	
    public double getAngle(LivingEntity entity, ItemStack stack) {
    	GrappleCustomization custom = this.getCustomization(stack);
    	if (entity.isCrouching()) {
    		return custom.sneakingangle;
    	} else {
    		return custom.angle;
    	}
    }
	
	public GrapplehookEntity createarrow(ItemStack stack, World worldIn, LivingEntity entityLiving, boolean righthand, boolean isdouble) {
		GrapplehookEntity arrow = new GrapplehookEntity(worldIn, entityLiving, righthand, this.getCustomization(stack), isdouble);
		ServerControllerManager.addarrow(entityLiving.getId(), arrow);
		return arrow;
	}
    
    public GrappleCustomization getCustomization(ItemStack itemstack) {
    	CompoundNBT tag = itemstack.getOrCreateTag();
    	
    	if (tag.contains("custom")) {
        	GrappleCustomization custom = new GrappleCustomization();
    		custom.loadNBT(tag.getCompound("custom"));
        	return custom;
    	} else {
    		GrappleCustomization custom = this.getDefaultCustomization();

			CompoundNBT nbt = custom.writeNBT();
			
			tag.put("custom", nbt);
			itemstack.setTag(tag);

    		return custom;
    	}
    }
    
    public GrappleCustomization getDefaultCustomization() {
    	return new GrappleCustomization();
    }
    
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag par4) {
		GrappleCustomization custom = getCustomization(stack);
		
		if (Screen.hasShiftDown()) {
			if (!custom.detachonkeyrelease) {
				list.add(new StringTextComponent(ClientSetup.key_boththrow.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.throw.desc")));
				list.add(new StringTextComponent(ClientSetup.key_boththrow.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.release.desc")));
				list.add(new StringTextComponent(ClientProxyInterface.proxy.localize("grappletooltip.double.desc") + ClientSetup.key_boththrow.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.releaseandthrow.desc")));
			} else {
				list.add(new StringTextComponent(ClientSetup.key_boththrow.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.throwhold.desc")));
			}
			list.add(new StringTextComponent(ClientProxyInterface.proxy.getkeyname(ClientProxyInterface.mckeys.keyBindForward) + ", " +
					ClientProxyInterface.proxy.getkeyname(ClientProxyInterface.mckeys.keyBindLeft) + ", " +
					ClientProxyInterface.proxy.getkeyname(ClientProxyInterface.mckeys.keyBindBack) + ", " +
					ClientProxyInterface.proxy.getkeyname(ClientProxyInterface.mckeys.keyBindRight) +
					" " + ClientProxyInterface.proxy.localize("grappletooltip.swing.desc")));
			list.add(new StringTextComponent(ClientSetup.key_jumpanddetach.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.jump.desc")));
			list.add(new StringTextComponent(ClientSetup.key_slow.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.slow.desc")));
			list.add(new StringTextComponent((custom.climbkey ? ClientSetup.key_climb.getTranslatedKeyMessage().getString() + " + " : "") +
					ClientSetup.key_climbup.getTranslatedKeyMessage().getString() + 
					" " + ClientProxyInterface.proxy.localize("grappletooltip.climbup.desc")));
			list.add(new StringTextComponent((custom.climbkey ? ClientSetup.key_climb.getTranslatedKeyMessage().getString() + " + " : "") +
					ClientSetup.key_climbdown.getTranslatedKeyMessage().getString() + 
					" " + ClientProxyInterface.proxy.localize("grappletooltip.climbdown.desc")));
			if (custom.enderstaff) {
				list.add(new StringTextComponent(ClientSetup.key_enderlaunch.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.enderlaunch.desc")));
			}
			if (custom.rocket) {
				list.add(new StringTextComponent(ClientSetup.key_rocket.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.rocket.desc")));
			}
			if (custom.motor) {
				if (custom.motorwhencrouching && !custom.motorwhennotcrouching) {
					list.add(new StringTextComponent(ClientSetup.key_motoronoff.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.motoron.desc")));
				}
				else if (!custom.motorwhencrouching && custom.motorwhennotcrouching) {
					list.add(new StringTextComponent(ClientSetup.key_motoronoff.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.motoroff.desc")));
				}
			}
			if (custom.doublehook) {
				if (!custom.detachonkeyrelease) {
					list.add(new StringTextComponent(ClientSetup.key_leftthrow.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.throwleft.desc")));
					list.add(new StringTextComponent(ClientSetup.key_rightthrow.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.throwright.desc")));
				} else {
					list.add(new StringTextComponent(ClientSetup.key_leftthrow.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.throwlefthold.desc")));
					list.add(new StringTextComponent(ClientSetup.key_rightthrow.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.throwrighthold.desc")));
				}
			} else {
				list.add(new StringTextComponent(ClientSetup.key_rightthrow.getTranslatedKeyMessage().getString() + " " + ClientProxyInterface.proxy.localize("grappletooltip.throwalt.desc")));
			}
			if (custom.reelin) {
				list.add(new StringTextComponent(ClientProxyInterface.proxy.getkeyname(ClientProxyInterface.mckeys.keyBindSneak) + " " + ClientProxyInterface.proxy.localize("grappletooltip.reelin.desc")));
			}
		} else {
			if (Screen.hasControlDown()) {
				for (String option : GrappleCustomization.booleanoptions) {
					if (custom.isoptionvalid(option) && custom.getBoolean(option) != GrappleCustomization.DEFAULT.getBoolean(option)) {
						list.add(new StringTextComponent((custom.getBoolean(option) ? "" : ClientProxyInterface.proxy.localize("grappletooltip.negate.desc") + " ") + ClientProxyInterface.proxy.localize(custom.getName(option))));
					}
				}
				for (String option : GrappleCustomization.doubleoptions) {
					if (custom.isoptionvalid(option) && (custom.getDouble(option) != GrappleCustomization.DEFAULT.getDouble(option))) {
						list.add(new StringTextComponent(ClientProxyInterface.proxy.localize(custom.getName(option)) + ": " + Math.floor(custom.getDouble(option) * 100) / 100));
					}
				}
			} else {
				if (custom.doublehook) {
					list.add(new StringTextComponent(ClientProxyInterface.proxy.localize(custom.getName("doublehook"))));
				}
				if (custom.motor) {
					if (custom.smartmotor) {
						list.add(new StringTextComponent(ClientProxyInterface.proxy.localize(custom.getName("smartmotor"))));
					} else {
						list.add(new StringTextComponent(ClientProxyInterface.proxy.localize(custom.getName("motor"))));
					}
				}
				if (custom.enderstaff) {
					list.add(new StringTextComponent(ClientProxyInterface.proxy.localize(custom.getName("enderstaff"))));
				}
				if (custom.rocket) {
					list.add(new StringTextComponent(ClientProxyInterface.proxy.localize(custom.getName("rocket"))));
				}
				if (custom.attract) {
					list.add(new StringTextComponent(ClientProxyInterface.proxy.localize(custom.getName("attract"))));
				}
				if (custom.repel) {
					list.add(new StringTextComponent(ClientProxyInterface.proxy.localize(custom.getName("repel"))));
				}
				
				list.add(new StringTextComponent(""));
				list.add(new StringTextComponent(ClientProxyInterface.proxy.localize("grappletooltip.shiftcontrols.desc")));
				list.add(new StringTextComponent(ClientProxyInterface.proxy.localize("grappletooltip.controlconfiguration.desc")));
			}
		}
	}

	public void setCustomOnServer(ItemStack helditemstack, GrappleCustomization custom, PlayerEntity player) {
		CompoundNBT tag = helditemstack.getOrCreateTag();
		CompoundNBT nbt = custom.writeNBT();
		
		tag.put("custom", nbt);
		
		helditemstack.setTag(tag);
	}

	
	@Override
	public boolean onDroppedByPlayer(ItemStack item, PlayerEntity player) {
		int id = player.getId();
		GrapplemodUtils.sendtocorrectclient(new GrappleDetachMessage(id), id, player.level);
		
		if (!player.level.isClientSide) {
			if (ServerControllerManager.attached.contains(id)) {
				ServerControllerManager.attached.remove(id);
			}
		}
		
		if (grapplearrows1.containsKey(player)) {
			GrapplehookEntity arrow1 = grapplearrows1.get(player);
			setArrowLeft(player, null);
			if (arrow1 != null) {
				arrow1.removeServer();
			}
		}
		
		if (grapplearrows2.containsKey(player)) {
			GrapplehookEntity arrow2 = grapplearrows2.get(player);
			setArrowLeft(player, null);
			if (arrow2 != null) {
				arrow2.removeServer();
			}
		}
		
		return super.onDroppedByPlayer(item, player);
	}
	
	public boolean getPropertyRocket(ItemStack stack, World world, LivingEntity entity) {
		return this.getCustomization(stack).rocket;
	}

	public boolean getPropertyDouble(ItemStack stack, World world, LivingEntity entity) {
		return this.getCustomization(stack).doublehook;
	}

	public boolean getPropertyMotor(ItemStack stack, World world, LivingEntity entity) {
		return this.getCustomization(stack).motor;
	}

	public boolean getPropertySmart(ItemStack stack, World world, LivingEntity entity) {
		return this.getCustomization(stack).smartmotor;
	}

	public boolean getPropertyEnderstaff(ItemStack stack, World world, LivingEntity entity) {
		return this.getCustomization(stack).enderstaff;
	}

	public boolean getPropertyMagnet(ItemStack stack, World world, LivingEntity entity) {
		return this.getCustomization(stack).attract || this.getCustomization(stack).repel;
	}

	@Override
	public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> items) {
			if (this.allowdedIn(tab)) {
	        	ItemStack stack = new ItemStack(this);
	            items.add(stack);
	            if (ClientProxyInterface.proxy != null) {
	            	ClientProxyInterface.proxy.fillGrappleVariants(tab, items);
	            }
			}
	}
}
