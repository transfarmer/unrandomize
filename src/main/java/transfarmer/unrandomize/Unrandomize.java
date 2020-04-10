package transfarmer.unrandomize;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig.Target;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Predicate;

import static net.minecraft.world.gen.GenerationStep.Feature.UNDERGROUND_ORES;
import static net.minecraft.world.gen.feature.Feature.ORE;

public class Unrandomize implements ModInitializer {
    public static final String MOD_ID = "unrandomize";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static Unrandomize INSTANCE;

    @Override
    public void onInitialize() {
        LOGGER.info("loading unrandomize");
        System.out.println("loading unrandomize");

        INSTANCE = this;
    }

    public void generateVein(final Biome biome, final Predicate<Biome> predicate, final Block block, final int size, final int chunkVeins, final int min, final int max) {
        if (predicate.test(biome)) {
            biome.addFeature(UNDERGROUND_ORES, ORE.configure(new OreFeatureConfig(Target.NATURAL_STONE, block.getDefaultState(), size))
                    .createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(chunkVeins, 0, min, max)))
            );
        }
    }
}
