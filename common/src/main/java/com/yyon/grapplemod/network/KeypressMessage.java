package com.yyon.grapplemod.network;

import com.yyon.grapplemod.items.KeypressItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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

public class KeypressMessage extends BaseMessageServer {
	public static final CustomPacketPayload.Type<KeypressMessage> TYPE = GrappleNetwork.type("keypress");
	public static final StreamCodec<RegistryFriendlyByteBuf, KeypressMessage> STREAM_CODEC = CustomPacketPayload.codec(KeypressMessage::encode, KeypressMessage::new);

	KeypressItem.Keys key;
	boolean isDown;

    public KeypressMessage(FriendlyByteBuf buf) {
    	super(buf);
    }

    public KeypressMessage(KeypressItem.Keys thekey, boolean isDown) {
    	this.key = thekey;
    	this.isDown = isDown;
    }

    public void decode(FriendlyByteBuf buf) {
    	this.key = KeypressItem.Keys.values()[buf.readInt()];
    	this.isDown = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
    	buf.writeInt(this.key.ordinal());
    	buf.writeBoolean(this.isDown);
    }

    @Override
    public CustomPacketPayload.Type<KeypressMessage> type() {
    	return TYPE;
    }

	@Override
    public void processMessage(ServerPlayer player) {
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (stack != null) {
			Item item = stack.getItem();
			if (item instanceof KeypressItem) {
				if (isDown) {
					((KeypressItem)item).onCustomKeyDown(stack, player, key, true);
				} else {
					((KeypressItem)item).onCustomKeyUp(stack, player, key, true);
				}
				return;
			}
		}

		stack = player.getItemInHand(InteractionHand.OFF_HAND);
		if (stack != null) {
			Item item = stack.getItem();
			if (item instanceof KeypressItem) {
				if (isDown) {
					((KeypressItem)item).onCustomKeyDown(stack, player, key, false);
				} else {
					((KeypressItem)item).onCustomKeyUp(stack, player, key, false);
				}
				return;
			}
		}
	}
}
