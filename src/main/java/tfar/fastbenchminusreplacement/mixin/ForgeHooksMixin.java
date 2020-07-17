package tfar.fastbenchminusreplacement.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.fastbenchminusreplacement.FastBenchMinusReplacement;

@Mixin(ForgeHooks.class)
public class ForgeHooksMixin {
	@Inject(method = "getDefaultCreatorModId",at = @At("HEAD"),cancellable = true,remap = false)
	private static void registryReplacementIsNotNeededForAccountability(ItemStack itemStack, CallbackInfoReturnable<String> cir) {
		if (itemStack.getItem() == Items.CRAFTING_TABLE) {
			cir.setReturnValue(FastBenchMinusReplacement.MODID);
		}
	}
}
