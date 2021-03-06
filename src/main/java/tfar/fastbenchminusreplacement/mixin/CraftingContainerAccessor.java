package tfar.fastbenchminusreplacement.mixin;

import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorkbenchContainer.class)
public interface CraftingContainerAccessor {
	@Accessor
	CraftingInventory getCraftMatrix();
	@Accessor
	CraftResultInventory getCraftResult();
}
