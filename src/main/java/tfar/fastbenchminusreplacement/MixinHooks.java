package tfar.fastbenchminusreplacement;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import tfar.fastbenchminusreplacement.interfaces.CraftingDuck;
import tfar.fastbenchminusreplacement.network.PacketHandler;
import tfar.fastbenchminusreplacement.network.S2CSyncRecipe;

public class MixinHooks {

	public static void updateResult(Container fastBenchContainer, PlayerEntity player, CraftingInventory inv, CraftResultInventory result) {
		updateResult(fastBenchContainer,(CraftingDuck)fastBenchContainer,player,inv,result);
	}

		public static void updateResult(Container fastBenchContainer,CraftingDuck duck, PlayerEntity player, CraftingInventory inv, CraftResultInventory result) {
		World world = player.world;
		if (!world.isRemote) {

			ItemStack itemstack = ItemStack.EMPTY;


			if (duck.checkMatrixChanges() && (duck.lastRecipe() == null || !duck.lastRecipe().matches(inv, world)))
				duck.setLastRecipe(findRecipe(inv, world));

			if (duck.lastRecipe() != null) {
				itemstack = duck.lastRecipe().getCraftingResult(inv);
			}

			result.setInventorySlotContents(0, itemstack);
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			if (duck.lastLastRecipe() != duck.lastRecipe()) serverPlayerEntity.connection.sendPacket(new SSetSlotPacket(fastBenchContainer.windowId, 0, itemstack));
			else if (duck.lastLastRecipe() != null && duck.lastLastRecipe() == duck.lastRecipe() && !ItemStack.areItemsEqual(duck.lastLastRecipe().getCraftingResult(inv),
							duck.lastRecipe().getCraftingResult(inv)))
				serverPlayerEntity.connection.sendPacket(new SSetSlotPacket(fastBenchContainer.windowId, 0, itemstack));
			PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
			buf.writeString(duck.lastRecipe() != null ? duck.lastRecipe().getId().toString() : "null");
			PacketHandler.INSTANCE.sendTo(new S2CSyncRecipe(duck.lastRecipe()), serverPlayerEntity.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
			duck.setLastLastRecipe(duck.lastRecipe());
		}
	}

	public static IRecipe<CraftingInventory> findRecipe(CraftingInventory inv, World world) {
		return world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, inv, world).orElse(null);
	}

}
