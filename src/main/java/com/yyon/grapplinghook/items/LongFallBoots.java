package com.yyon.grapplinghook.items;

import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

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

public class LongFallBoots extends ArmorItem {
    public LongFallBoots(ArmorMaterials material, int type) {
        super(material, Type.BOOTS, new Item.Properties().stacksTo(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag par4) {
        if (!stack.isEnchanted()) {
            if (GrappleConfig.getConf().longfallboots.longfallbootsrecipe) {
                list.add(Component.literal(ClientProxyInterface.proxy.localize("grappletooltip.longfallbootsrecipe.desc")));
            }
        }
        list.add(Component.literal(ClientProxyInterface.proxy.localize("grappletooltip.longfallboots.desc")));
    }


    public static void addToTab(CreativeModeTab.Output items) {
        ItemStack stack = new ItemStack(CommonSetup.longFallBootsItem.get());
        items.accept(stack);
        stack = new ItemStack(CommonSetup.longFallBootsItem.get());
        stack.enchant(CommonSetup.wallrunEnchantment.get(), 1);
        stack.enchant(CommonSetup.doubleJumpEnchantment.get(), 1);
        stack.enchant(CommonSetup.slidingEnchantment.get(), 1);
        items.accept(stack);
    }
}
