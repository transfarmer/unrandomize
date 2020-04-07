package transfarmer.deterministicloot;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Main.MOD_ID, name = Main.MOD_NAME, version = Main.VERSION)
public class Main {
    public static final String MOD_ID = "deterministicloot";
    public static final String MOD_NAME = "deterministic loot";
    public static final String VERSION = "0.0.0-beta";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @EventHandler
    public static void onPreinit(final FMLPreInitializationEvent event) {
        LOGGER.debug("deterministic loot preinit");
    }
}
