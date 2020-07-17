package tfar.fastbenchminusreplacement.mixin;

import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Container.class)
public interface ContainerAccessor {
	@Invoker("mergeItemStack")
	boolean insert(ItemStack stack, int startIndex, int endIndex, boolean fromLast);

}
