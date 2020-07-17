package tfar.fastbenchminusreplacement.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.fastbenchminusreplacement.MixinHooks;

import javax.annotation.Nullable;

@Mixin(PlayerContainer.class)
abstract class PlayerContainerMixin extends Container {

	@Shadow
	@Final
	private CraftingInventory craftMatrix;
	@Shadow
	@Final
	private CraftResultInventory craftResult;
	@Shadow
	@Final
	private PlayerEntity player;

	protected PlayerContainerMixin(@Nullable ContainerType<?> type, int syncId) {
		super(type, syncId);
	}

	@Overwrite
	public void onCraftMatrixChanged(IInventory inventory) {
		MixinHooks.slotChangedCraftingGrid(player.world,player,craftMatrix,craftResult);
	}

	@Inject(method = "transferStackInSlot",at = @At("HEAD"),cancellable = true)
	private void handleShiftCraft(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir) {
		if (index != 0) return;
		cir.setReturnValue(MixinHooks.handleShiftCraft(player, this, this.inventorySlots.get(index), this.craftMatrix, this.craftResult, 9, 45));
	}
}
