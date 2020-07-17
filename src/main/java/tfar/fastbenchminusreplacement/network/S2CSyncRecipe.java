package tfar.fastbenchminusreplacement.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.fastbenchminusreplacement.mixin.CraftingContainerAccessor;
import tfar.fastbenchminusreplacement.mixin.PlayerContainerAccessor;

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
      if (container instanceof PlayerContainer || container instanceof WorkbenchContainer) {
        IRecipe<?> r = Minecraft.getInstance().world.getRecipeManager().getRecipe(rec).orElse(null);
        updateLastRecipe(player.openContainer, (IRecipe<CraftingInventory>) r);
      }
    });
    ctx.get().setPacketHandled(true);
  }

  public static void updateLastRecipe(Container container, IRecipe<CraftingInventory> rec) {

    CraftingInventory craftInput = null;
    CraftResultInventory craftResult = null;

    if (container instanceof PlayerContainer) {
      craftInput = ((PlayerContainerAccessor)container).getCraftMatrix();
      craftResult = ((PlayerContainerAccessor)container).getCraftResult();
    }

    else if (container instanceof WorkbenchContainer) {
      craftInput = ((CraftingContainerAccessor)container).getCraftMatrix();
      craftResult = ((CraftingContainerAccessor)container).getCraftResult();
    }

    if (craftInput == null) {
      System.out.println("why are these null?");
    } else {
      craftResult.setRecipeUsed(rec);
      if (rec != null) craftResult.setInventorySlotContents(0, rec.getCraftingResult(craftInput));
      else craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
    }
  }

}