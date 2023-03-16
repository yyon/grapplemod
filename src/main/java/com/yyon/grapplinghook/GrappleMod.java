package com.yyon.grapplinghook;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.common.CreativeTabHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

//TODO
// Pull mobs
// Attach 2 things together
// wallrun on diagonal walls
// smart motor acts erratically when aiming above hook
// key events

@Mod(GrappleMod.MODID)
public class GrappleMod {
    public static final String MODID = "grapplemod";

    public static final Logger LOGGER = LogManager.getLogger();

    public GrappleMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        CreativeTabHandler.nudge();
        CommonSetup.BLOCKS.register(bus);
        CommonSetup.ITEMS.register(bus);
        CommonSetup.ENTITY_TYPES.register(bus);
        CommonSetup.ENCHANTMENTS.register(bus);
        CommonSetup.BLOCK_ENTITY_TYPES.register(bus);
    }
}
