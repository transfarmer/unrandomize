package transfarmer.unrandomize.mixin.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.BonusChestFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mixin(BonusChestFeature.class)
public abstract class BonusChestFeatureMixin extends Feature<DefaultFeatureConfig> {
    public BonusChestFeatureMixin(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, final Random random, final BlockPos blockPos, final DefaultFeatureConfig config) {
        final ChunkPos chunkPos = new ChunkPos(blockPos);
        final List<Integer> list = IntStream.rangeClosed(chunkPos.getStartX(), chunkPos.getEndX()).boxed().collect(Collectors.toList());
        final List<Integer> list2 = IntStream.rangeClosed(chunkPos.getStartZ(), chunkPos.getEndZ()).boxed().collect(Collectors.toList());
        final BlockPos.Mutable pos = new BlockPos.Mutable();

        for (final int x : list) {
            for (final int z : list2) {
                final BlockPos blockPos2 = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);

                pos.set(x, 0, z);

                if (world.isAir(blockPos2) || world.getBlockState(blockPos2).getCollisionShape(world, blockPos2).isEmpty()) {
                    final BlockState blockState = Blocks.TORCH.getDefaultState();

                    world.setBlockState(blockPos2, Blocks.CHEST.getDefaultState(), 2);
                    LootableContainerBlockEntity.setLootTable(world, random, blockPos2, LootTables.SPAWN_BONUS_CHEST);

                    for (final Direction direction : Direction.Type.HORIZONTAL) {
                        BlockPos blockPos3 = blockPos2.offset(direction);
                        if (blockState.canPlaceAt(world, blockPos3)) {
                            world.setBlockState(blockPos3, blockState, 2);
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }
}
