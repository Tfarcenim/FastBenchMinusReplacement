package tfar.fastbenchminusreplacement;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.fastbenchminusreplacement.network.PacketHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FastBenchMinusReplacement.MODID)
public class FastBenchMinusReplacement
{
    // Directly reference a log4j logger.

    public static final String MODID = "fastbenchminusreplacement";

    private static final Logger LOGGER = LogManager.getLogger();

    public static FastBenchMinusReplacement instance;

    public FastBenchMinusReplacement() {
        instance = this;
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.registerMessages("recipesync");
    }
}
