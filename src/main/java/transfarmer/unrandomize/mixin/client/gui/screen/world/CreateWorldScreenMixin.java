package transfarmer.unrandomize.mixin.client.gui.screen.world;

import com.google.gson.JsonElement;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    @Shadow
    private TextFieldWidget seedField;
    @Shadow
    private Mode currentMode;
    @Shadow
    private boolean creatingLevel;

    public CreateWorldScreenMixin(final Screen parent) {
        super(new TranslatableText("selectWorld.create"));



        this.currentMode = CreateWorldScreen.Mode.SURVIVAL;
        this.structures = true;
        this.generatorOptionsTag = new CompoundTag();
        this.parent = parent;
        this.seed = "";
        this.levelName = I18n.translate("selectWorld.newWorld");
    }

    private void createLevel() {
        this.minecraft.openScreen(null);
        if (!this.creatingLevel) {
            this.creatingLevel = true;

            long seed = 1;
            final String stringSeed = this.seedField.getText();

            if (!StringUtils.isEmpty(stringSeed)) {
                try {
                    long m = Long.parseLong(stringSeed);

                    if (m != 0L) {
                        seed = m;
                    }
                } catch (final NumberFormatException exception) {
                    seed = stringSeed.hashCode();
                }
            }

            final LevelInfo levelInfo = new LevelInfo(seed, this.currentMode.defaultGameMode, this.structures, this.hardcore, LevelGeneratorType.TYPES[this.generatorType]);
            levelInfo.setGeneratorOptions((JsonElement) Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.generatorOptionsTag));

            if (this.bonusChest && !this.hardcore) {
                levelInfo.setBonusChest();
            }

            if (this.cheatsEnabled && !this.hardcore) {
                levelInfo.enableCommands();
            }

            this.minecraft.startIntegratedServer(this.saveDirectoryName, this.levelNameField.getText().trim(), levelInfo);
        }
    }

}
