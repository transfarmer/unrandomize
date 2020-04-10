package transfarmer.unrandomize.mixin.client.gui.screen.world;

import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.client.gui.screen.world.CreateWorldScreen.Mode")
enum Mode implements IMode {
    SURVIVAL("survival", GameMode.SURVIVAL),
    HARDCORE("hardcore", GameMode.SURVIVAL),
    CREATIVE("creative", GameMode.CREATIVE),
    DEBUG("spectator", GameMode.SPECTATOR);

    private final String translationSuffix;
    private final GameMode defaultGameMode;

    Mode(String translationSuffix, GameMode defaultGameMode) {
        this.translationSuffix = translationSuffix;
        this.defaultGameMode = defaultGameMode;
    }

    @Override
    public String getTranslationSuffix() {
        return this.translationSuffix;
    }

    @Override
    public GameMode getDefaultGameMode() {
        return this.defaultGameMode;
    }
}
