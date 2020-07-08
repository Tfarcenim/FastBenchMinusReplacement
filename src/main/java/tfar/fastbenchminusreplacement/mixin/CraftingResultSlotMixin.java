package tfar.fastbenchminusreplacement.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;

import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfar.fastbenchminusreplacement.interfaces.CraftingScreenHandlerDuck;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin extends Slot {

	@Shadow
	@Final
	private PlayerEntity player;
	@Shadow
	@Final
	private CraftingInventory craftMatrix;

	public CraftingResultSlotMixin(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Redirect(method = "onTake",at = @At(value = "INVOKE",target = "Lnet/minecraft/item/crafting/RecipeManager;getRecipeNonNull(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Lnet/minecraft/util/NonNullList;"))
	private NonNullList<ItemStack> cache(RecipeManager recipeManager, IRecipeType<ICraftingRecipe> recipeType, IInventory inventory, World world){
		if (player.openContainer.getClass() == WorkbenchContainer.class) {
			IRecipe<CraftingInventory> lastRecipe = ((CraftingScreenHandlerDuck) player.openContainer).lastRecipe();
			if (lastRecipe != null &&
							lastRecipe.matches(craftMatrix, player.world))
				return lastRecipe.getRemainingItems(craftMatrix);
			else return ((CraftingInventoryAccessor) craftMatrix).getStackList();
		}
		return player.world.getRecipeManager().getRecipeNonNull(IRecipeType.CRAFTING, this.craftMatrix, player.world);
	}
}