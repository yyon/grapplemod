package com.yyon.grapplinghook.common;

import com.yyon.grapplinghook.grapplemod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = grapplemod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeTabHandler {

    public static void nudge() { }

    @SubscribeEvent
    public static void buildContents(CreativeModeTabEvent.Register event) {
        CreativeModeTab MOD_CREATIVE_ITEMS = event.registerCreativeModeTab(new ResourceLocation(grapplemod.MODID, "grappling_equip"), builder -> builder
                .title(Component.translatable("itemGroup.tabGrapplemod"))
                .icon(() -> new ItemStack(CommonSetup.grapplingHookItem.get()))
                .displayItems((flags, output, operatorTabEnabled) ->
                        CommonSetup.queuedCreativeTabStacks.stream()
                                .map(Supplier::get) // unpack
                                .flatMap(Collection::stream) // combine
                                .forEachOrdered(output::accept) // display
                )
        );
    }

}