package tfar.fastbenchminusreplacement.interfaces;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.IRecipe;

public interface CraftingDuck {

	IRecipe<CraftingInventory> lastRecipe();
	IRecipe<CraftingInventory> lastLastRecipe();
	boolean checkMatrixChanges();
	void setLastRecipe(IRecipe<CraftingInventory> recipe);
	void setLastLastRecipe(IRecipe<CraftingInventory> recipe);
	void updateLastRecipe(IRecipe<CraftingInventory> rec);
	}
