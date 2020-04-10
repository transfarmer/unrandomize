package transfarmer.unrandomize.mixin.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.DungeonFeature;
import net.minecraft.world.gen.feature.Feature;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;

@Mixin(DungeonFeature.class)
public class DungeonFeatureMixin extends Feature<DefaultFeatureConfig> {
    @Shadow
    @Final
    private static final Logger LOGGER = null;
    @Shadow
    @Final
    private static final BlockState AIR = null;

    public DungeonFeatureMixin(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }

    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random rand, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        int i = 3;
        int j = 2;
        int k = -j - 1;
        int l = j + 1;
        int m = -1;
        int n = 4;
        int o = 3;
        int p = -o - 1;
        int q = o + 1;
        int r = 0;

        int v;
        int w;
        int x;
        BlockPos blockPos3;
        for (v = k; v <= l; ++v) {
            for (w = -1; w <= 4; ++w) {
                for (x = p; x <= q; ++x) {
                    blockPos3 = blockPos.add(v, w, x);
                    Material material = iWorld.getBlockState(blockPos3).getMaterial();
                    boolean bl = material.isSolid();
                    if (w == -1 && !bl) {
                        return false;
                    }

                    if (w == 4 && !bl) {
                        return false;
                    }

                    if ((v == k || v == l || x == p || x == q) && w == 0 && iWorld.isAir(blockPos3) && iWorld.isAir(blockPos3.up())) {
                        ++r;
                    }
                }
            }
        }

        if (r >= 1 && r <= 5) {
            int cobblestone = 0;

            for (v = k; v <= l; ++v) {
                for (w = 3; w >= -1; --w) {
                    for (x = p; x <= q; ++x) {
                        blockPos3 = blockPos.add(v, w, x);
                        if (v != k && w != -1 && x != p && v != l && w != 4 && x != q) {
                            if (iWorld.getBlockState(blockPos3).getBlock() != Blocks.CHEST) {
                                iWorld.setBlockState(blockPos3, AIR, 2);
                            }
                        } else if (blockPos3.getY() >= 0 && !iWorld.getBlockState(blockPos3.down()).getMaterial().isSolid()) {
                            iWorld.setBlockState(blockPos3, AIR, 2);
                        } else if (iWorld.getBlockState(blockPos3).getMaterial().isSolid() && iWorld.getBlockState(blockPos3).getBlock() != Blocks.CHEST) {
                            if (w == -1 && cobblestone % 4 != 0) {
                                iWorld.setBlockState(blockPos3, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
                            } else {
                                iWorld.setBlockState(blockPos3, Blocks.COBBLESTONE.getDefaultState(), 2);
                            }

                            cobblestone++;
                        }
                    }
                }
            }

            int counter = 0;

            for (v = 0; v < 2; ++v) {
                for (w = 0; w < 3; ++w) {
                    x = blockPos.getX() + random.nextInt(j * 2 + 1) - j;
                    int ab = blockPos.getY();
                    int ac = blockPos.getZ() + random.nextInt(o * 2 + 1) - o;
                    BlockPos blockPos4 = new BlockPos(x, ab, ac);
                    if (iWorld.isAir(blockPos4)) {
                        int ad = 0;
                        Iterator var23 = Direction.Type.HORIZONTAL.iterator();

                        while (var23.hasNext()) {
                            Direction direction = (Direction) var23.next();
                            if (iWorld.getBlockState(blockPos4.offset(direction)).getMaterial().isSolid()) {
                                ++ad;
                            }
                        }

                        if (ad == 1) {
                            iWorld.setBlockState(blockPos4, StructurePiece.method_14916(iWorld, blockPos4, Blocks.CHEST.getDefaultState()), 2);
                            LootableContainerBlockEntity.setLootTable(iWorld, random, blockPos4, LootTables.SIMPLE_DUNGEON_CHEST);
                            break;
                        }
                    }
                }
            }

            iWorld.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
            BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
            if (blockEntity instanceof MobSpawnerBlockEntity) {
                ((MobSpawnerBlockEntity) blockEntity).getLogic().setEntityId(this.getMobSpawnerEntity(random));
            } else {
                LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", blockPos.getX(), blockPos.getY(), blockPos.getZ());
            }

            return true;
        } else {
            return false;
        }
    }
}
