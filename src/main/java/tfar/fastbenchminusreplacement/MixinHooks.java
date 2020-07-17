package tfar.fastbenchminusreplacement;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import tfar.fastbenchminusreplacement.interfaces.CraftingInventoryDuck;
import tfar.fastbenchminusreplacement.mixin.ContainerAccessor;
import tfar.fastbenchminusreplacement.network.PacketHandler;
import tfar.fastbenchminusreplacement.network.S2CSyncRecipe;

public class MixinHooks {

	public static void slotChangedCraftingGrid(World world, PlayerEntity player, CraftingInventory inv, CraftResultInventory result) {
		if (!world.isRemote) {

			ItemStack itemstack = ItemStack.EMPTY;

			IRecipe<CraftingInventory> recipe = (IRecipe<CraftingInventory>) result.getRecipeUsed();
			if (recipe == null || !recipe.matches(inv, world)) recipe = findRecipe(inv, world);

			if (recipe != null) {
				itemstack = recipe.getCraftingResult(inv);
			}

			result.setInventorySlotContents(0, itemstack);
			PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
			buf.writeString(recipe != null ? recipe.getId().toString() : "null");
			PacketHandler.INSTANCE.sendTo(new S2CSyncRecipe(recipe), ((ServerPlayerEntity)player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
			result.setRecipeUsed(recipe);
		}
	}

	public static ItemStack handleShiftCraft(PlayerEntity player, Container container, Slot resultSlot, CraftingInventory input, CraftResultInventory craftResult, int outStart, int outEnd) {
		ItemStack outputCopy = ItemStack.EMPTY;
		CraftingInventoryDuck duck = (CraftingInventoryDuck)input;
		duck.setCheckMatrixChanges(false);
		if (resultSlot != null && resultSlot.getHasStack()) {

			IRecipe<CraftingInventory> recipe = (IRecipe<CraftingInventory>) craftResult.getRecipeUsed();
			while (recipe != null && recipe.matches(input, player.world)) {
				ItemStack recipeOutput = resultSlot.getStack().copy();
				outputCopy = recipeOutput.copy();

				recipeOutput.getItem().onCreated(recipeOutput, player.world, player);

				if (!player.world.isRemote && !((ContainerAccessor)container).insert(recipeOutput, outStart, outEnd,true)) {
					duck.setCheckMatrixChanges(true);
					return ItemStack.EMPTY;
				}

				resultSlot.onSlotChange(recipeOutput, outputCopy);
				resultSlot.onSlotChanged();

				if (!player.world.isRemote && recipeOutput.getCount() == outputCopy.getCount()) {
					duck.setCheckMatrixChanges(true);
					return ItemStack.EMPTY;
				}

				ItemStack itemstack2 = resultSlot.onTake(player, recipeOutput);
				player.dropItem(itemstack2, false);
			}
			duck.setCheckMatrixChanges(true);
			slotChangedCraftingGrid(player.world, player, input, craftResult);
		}
		duck.setCheckMatrixChanges(true);
		return craftResult.getRecipeUsed() == null ? ItemStack.EMPTY : outputCopy;
	}

	public static IRecipe<CraftingInventory> findRecipe(CraftingInventory inv, World world) {
		return world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, inv, world).orElse(null);
	}

}
