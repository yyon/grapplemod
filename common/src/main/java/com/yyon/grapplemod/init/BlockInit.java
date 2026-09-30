package com.yyon.grapplemod.init;

import com.yyon.grapplemod.Constants;
import com.yyon.grapplemod.blocks.modifierblock.BlockGrappleModifier;
import com.yyon.grapplemod.blocks.modifierblock.TileEntityGrappleModifier;
import com.yyon.grapplemod.registration.RegistrationProvider;
import com.yyon.grapplemod.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockInit {
	public static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(Registries.BLOCK, Constants.MODID);
	public static final RegistrationProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistrationProvider.get(Registries.BLOCK_ENTITY_TYPE, Constants.MODID);

	public static final RegistryObject<Block, BlockGrappleModifier> GRAPPLE_MODIFIER = BLOCKS.register("block_grapple_modifier", BlockGrappleModifier::new);
	public static final RegistryObject<Item, BlockItem> GRAPPLE_MODIFIER_ITEM = ItemInit.ITEMS.register("block_grapple_modifier", () -> new BlockItem(GRAPPLE_MODIFIER.get(), new Item.Properties().stacksTo(64)));

	public static final RegistryObject<BlockEntityType<?>, BlockEntityType<TileEntityGrappleModifier>> GRAPPLE_MODIFIER_BLOCK_ENTITY = BLOCK_ENTITIES.register("block_grapple_modifier", () -> BlockEntityType.Builder.of(TileEntityGrappleModifier::new, GRAPPLE_MODIFIER.get()).build(null));

	public static void loadClass() {
	}
}
