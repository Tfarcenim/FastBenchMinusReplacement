package tfar.fastbenchminusreplacement.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
  public static SimpleChannel INSTANCE;

  public static void registerMessages(String channelName) {
    int id = 0;

    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation("fastbench", channelName), () -> "1.0", s -> true, s -> true);

    INSTANCE.registerMessage(id++, S2CSyncRecipe.class,
            S2CSyncRecipe::encode,
            S2CSyncRecipe::new,
            S2CSyncRecipe::handle);
  }
}
