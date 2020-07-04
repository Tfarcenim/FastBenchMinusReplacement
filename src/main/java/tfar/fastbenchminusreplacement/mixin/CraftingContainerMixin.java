package tfar.fastbenchminusreplacement.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tfar.fastbenchminusreplacement.MixinHooks;
import tfar.fastbenchminusreplacement.interfaces.CraftingScreenHandlerDuck;

import javax.annotation.Nullable;

@Mixin(WorkbenchContainer.class)
abstract class CraftingContainerMixin extends Container implements CraftingScreenHandlerDuck {

	@Shadow
	@Final
	private CraftingInventory craftMatrix;
	@Shadow
	@Final
	private CraftResultInventory craftResult;
	@Shadow
	@Final
	private PlayerEntity player;

	public IRecipe<CraftingInventory> lastRecipe;
	protected IRecipe<CraftingInventory> lastLastRecipe;
	protected boolean checkMatrixChanges = true;

	protected CraftingContainerMixin(@Nullable ContainerType<?> type, int syncId) {
		super(type, syncId);
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		MixinHooks.updateResult((WorkbenchContainer) (Object) this, player, craftMatrix, craftResult);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index) {
		if (index != 0) {
			return super.transferStackInSlot(player, index);
		}

		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			checkMatrixChanges = false;
			while (lastRecipe != null && lastRecipe.matches(this.craftMatrix, this.player.world)) {
				ItemStack itemstack1 = slot.getStack();
				itemstack = itemstack1.copy();
				World world = player.world;

				itemstack1.getItem().onCreated(itemstack1, world, player);

				if (!world.isRemote && !this.mergeItemStack(itemstack1, 10, 46, true)) {
					checkMatrixChanges = true;
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemstack1, itemstack);
				slot.onSlotChanged();

				if (!world.isRemote && itemstack1.getCount() == itemstack.getCount()) {
					checkMatrixChanges = true;
					return ItemStack.EMPTY;
				}

				ItemStack itemstack2 = slot.onTake(player, itemstack1);
				player.dropItem(itemstack2, false);
			}
			checkMatrixChanges = true;
			MixinHooks.updateResult((WorkbenchContainer) (Object)this, player, craftMatrix, craftResult);
		}
		return lastRecipe == null ? ItemStack.EMPTY : itemstack;
	}

	public void updateLastRecipe(IRecipe<CraftingInventory> rec) {
		this.lastLastRecipe = lastRecipe;
		this.lastRecipe = rec;
		if (rec != null) this.craftResult.setInventorySlotContents(0, rec.getCraftingResult(craftMatrix));
		else this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
	}

	@Override
	public IRecipe<CraftingInventory> lastRecipe() {
		return lastRecipe;
	}

	@Override
	public IRecipe<CraftingInventory> lastLastRecipe() {
		return lastLastRecipe;
	}

	@Override
	public boolean checkMatrixChanges() {
		return checkMatrixChanges;
	}

	@Override
	public void setLastRecipe(IRecipe<CraftingInventory> recipe) {
		lastRecipe = recipe;
	}

	@Override
	public void setLastLastRecipe(IRecipe<CraftingInventory> recipe) {
		lastLastRecipe = recipe;
	}
}
