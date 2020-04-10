package transfarmer.unrandomize.mixin.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(LootableContainerBlockEntity.class)
public abstract class LootableContainerBlockEntityMixin extends LockableContainerBlockEntity {
    protected LootableContainerBlockEntityMixin(final BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    /** @author transfarmer */
    @Overwrite
    public static void setLootTable(final BlockView worldView, final Random random, final BlockPos pos, final Identifier id) {
        BlockEntity blockEntity = worldView.getBlockEntity(pos);

        if (blockEntity instanceof LootableContainerBlockEntity) {
            ((LootableContainerBlockEntity) blockEntity).setLootTable(id, 1);
        }
    }
}
