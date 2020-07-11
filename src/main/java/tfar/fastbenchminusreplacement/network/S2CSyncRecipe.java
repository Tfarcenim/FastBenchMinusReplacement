package tfar.fastbenchminusreplacement.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.fastbenchminusreplacement.interfaces.CraftingDuck;

import java.util.function.Supplier;

public class S2CSyncRecipe {

  public static final ResourceLocation NULL = new ResourceLocation("null", "null");

  ResourceLocation rec;

  public S2CSyncRecipe() {
  }

  public S2CSyncRecipe(IRecipe<CraftingInventory> toSend) {
    rec = toSend == null ? NULL : toSend.getId();
  }

  public S2CSyncRecipe(PacketBuffer packetBuffer) {
   this.rec = packetBuffer.readResourceLocation();
  }

  public void encode(PacketBuffer buf) {
    buf.writeResourceLocation(rec);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    PlayerEntity player = DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
    if (player == null) return;

    ctx.get().enqueueWork(() -> {
      Container container = player.openContainer;
      if (container instanceof CraftingDuck) {
        Minecraft.getInstance().world.getRecipeManager().getRecipe(rec)
                .ifPresent(iRecipe -> ((CraftingDuck) container).updateLastRecipe((IRecipe<CraftingInventory>) iRecipe));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}