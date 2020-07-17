package tfar.fastbenchminusreplacement.mixin;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfar.fastbenchminusreplacement.interfaces.CraftingInventoryDuck;

@Mixin(CraftingInventory.class)
public class CraftingInventoryMixin implements CraftingInventoryDuck {

	@Shadow @Final
	private Container eventHandler;

	public boolean checkMatrixChanges = true;

	@Override
	public void setCheckMatrixChanges(boolean checkMatrixChanges) {
		this.checkMatrixChanges = checkMatrixChanges;
	}

	@Redirect(method = {"decrStackSize",
					"setInventorySlotContents"},at = @At(value = "INVOKE",target = "net/minecraft/inventory/container/Container.onCraftMatrixChanged(Lnet/minecraft/inventory/IInventory;)V"))
	private void checkForChanges(Container container, IInventory inventory) {
		if (checkMatrixChanges)eventHandler.onCraftMatrixChanged((IInventory)this);
	}
}
