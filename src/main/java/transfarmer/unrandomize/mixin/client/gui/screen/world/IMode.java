package transfarmer.unrandomize.mixin.client.gui.screen.world;

import net.minecraft.world.GameMode;

public interface IMode {
    String getTranslationSuffix();

    GameMode getDefaultGameMode();
}
