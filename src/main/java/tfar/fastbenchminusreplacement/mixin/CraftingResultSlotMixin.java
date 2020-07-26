package tfar.fastbenchminusreplacement.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.*;
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

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin extends Slot {

	@Shadow
	@Final
	private CraftingInventory craftMatrix;

	public CraftingResultSlotMixin(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Redirect(method = "decrStackSize",at = @At(value = "INVOKE",target = "Lnet/minecraft/inventory/container/Slot;decrStackSize(I)Lnet/minecraft/item/ItemStack;"))
	private ItemStack copy(Slot slot, int amount) {
		return slot.getStack().copy();
	}

	@Override
	public void putStack(ItemStack stack) {
		//do nothing
	}

	@Redirect(method = "onCrafting(Lnet/minecraft/item/ItemStack;)V",
					at = @At(value = "INVOKE",target = "Lnet/minecraft/inventory/IRecipeHolder;onCrafting(Lnet/minecraft/entity/player/PlayerEntity;)V"))
	public void no(IRecipeHolder recipeUnlocker, PlayerEntity player) {
		//do nothing
	}

	//inventory is actually the crafting result inventory so it's a safe cast
	@Redirect(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/crafting/RecipeManager;getRecipeNonNull(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Lnet/minecraft/util/NonNullList;"))
	private NonNullList<ItemStack> cache(RecipeManager recipeManager, IRecipeType<ICraftingRecipe> recipeType, IInventory craftMatrixInv, World world) {
		IRecipe<CraftingInventory> lastRecipe = (IRecipe<CraftingInventory>) ((CraftResultInventory)this.inventory).getRecipeUsed();
		if (lastRecipe != null && lastRecipe.matches(this.craftMatrix,world))
			return lastRecipe.getRemainingItems(this.craftMatrix);
		else return ((CraftingInventoryAccessor) this.craftMatrix).getStackList();
	}
}