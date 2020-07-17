package tfar.fastbenchminusreplacement.mixin;

import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.PlayerContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerContainer.class)
public interface PlayerContainerAccessor {
	@Accessor
	CraftingInventory getCraftMatrix();
	@Accessor
	CraftResultInventory getCraftResult();
}
