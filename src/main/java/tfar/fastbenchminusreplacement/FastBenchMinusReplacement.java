package tfar.fastbenchminusreplacement;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tfar.fastbenchminusreplacement.network.PacketHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FastBenchMinusReplacement.MODID)
public class FastBenchMinusReplacement {
    // Directly reference a log4j logger.

    public static final String MODID = "fastbenchminusreplacement";


    public FastBenchMinusReplacement() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.registerMessages("recipesync");
    }
}
