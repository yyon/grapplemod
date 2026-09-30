package com.yyon.grapplemod.items;

import com.yyon.grapplemod.client.ClientProxyInterface;
import com.yyon.grapplemod.config.GrappleConfig;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

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
    public LongFallBoots(Holder<ArmorMaterial> material, ArmorItem.Type type) {
        super(material, type, new Item.Properties().durability(type.getDurability(33)));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (!stack.isEnchanted()) {
            if (GrappleConfig.getConf().longfallboots.longfallbootsrecipe) {
                list.add(Component.literal(ClientProxyInterface.proxy.localize("grappletooltip.longfallbootsrecipe.desc")));
            }
        }
        list.add(Component.literal(ClientProxyInterface.proxy.localize("grappletooltip.longfallboots.desc")));
    }
}
