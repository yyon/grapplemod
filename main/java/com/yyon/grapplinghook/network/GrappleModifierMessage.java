package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleCustomization;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

/*
    GrappleMod is free software: you can redistribute it and/or modify
    it under the teHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

public class GrappleModifierMessage extends BaseMessage {
   
	public BlockPos pos;
	public GrappleCustomization custom;

    public GrappleModifierMessage(BlockPos pos, GrappleCustomization custom) {
    	this.pos = pos;
    	this.custom = custom;
    }

	public GrappleModifierMessage(PacketBuffer buf) {
		super(buf);
	}

    public void decode(PacketBuffer buf) {
    	this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    	this.custom = new GrappleCustomization();
    	this.custom.readFromBuf(buf);
    }

    public void encode(PacketBuffer buf) {
    	buf.writeInt(this.pos.getX());
    	buf.writeInt(this.pos.getY());
    	buf.writeInt(this.pos.getZ());
    	this.custom.writeToBuf(buf);
    }

    public void processMessage(NetworkEvent.Context ctx) {
    	/*
		World w = ctx.getSender().level;
		
		TileEntity ent = w.getBlockEntity(this.pos);
		
		if (ent != null && ent instanceof TileEntityGrappleModifier) {
			((TileEntityGrappleModifier) ent).setCustomizationServer(this.custom);
		}
		*/
    }
}
