package tfar.fastbenchminusreplacement.mixin;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingInventory.class)
public interface CraftingInventoryAccessor {
	@Accessor
	NonNullList<ItemStack> getStackList();

	@Accessor
	Container getEventHandler();

}
