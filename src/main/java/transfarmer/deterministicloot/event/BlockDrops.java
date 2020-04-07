package transfarmer.deterministicloot.event;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import transfarmer.deterministicloot.Main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@EventBusSubscriber(modid = Main.MOD_ID)
public class BlockDrops {
    public static final Map<Block, Double> BLOCK_ITEM_DROPS = new HashMap<>();
    public static final Map<Block, Double> BLOCK_XP_DROPS = new HashMap<>();
    public static final Map<UUID, Map<Block, Double>> BLOCK_ITEM_DROP_PROGRESS = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onHarvestDrops(final HarvestDropsEvent event) {
        final EntityPlayer player = event.getHarvester();
        final List<ItemStack> drops = event.getDrops();
        final IBlockState blockState = event.getState();
        final Block block = blockState.getBlock();
        final int fortuneLevel = event.getFortuneLevel();
        double itemDrops = 0;
        double itemProgress = 0;
        double xpDrops = 0;
        drops.clear();

        if (!BLOCK_ITEM_DROPS.containsKey(block)) {
            final int iterations = 10000;

            for (int i = 0; i < iterations; i++) {
                itemDrops += block.quantityDropped(blockState, 0, new Random());
            }

            BLOCK_ITEM_DROPS.put(block, itemDrops /= iterations);
        } else {
            itemDrops = BLOCK_ITEM_DROPS.get(block);
        }

        if (!BLOCK_XP_DROPS.containsKey(block)) {
            final int iterations = 10000;

            for (int i = 0; i < iterations; i++) {
                xpDrops += block.getExpDrop(blockState, event.getWorld(), event.getPos(), event.getFortuneLevel());
            }

            BLOCK_XP_DROPS.put(block, xpDrops /= iterations);
        } else {
            xpDrops = BLOCK_XP_DROPS.get(block);
        }

        if (player != null) {
            final UUID playerID = player.getPersistentID();

            // initialize itemProgress
            if (!BLOCK_ITEM_DROP_PROGRESS.containsKey(playerID)) {
                final Map<Block, Double> data = new HashMap<>();
                data.put(block, itemProgress = 0);

                BLOCK_ITEM_DROP_PROGRESS.put(playerID, data);
            } else {
                 Map<Block, Double> data = BLOCK_ITEM_DROP_PROGRESS.get(playerID);

                if (!data.containsKey(block)) {
                    data = new HashMap<>();
                    data.put(block, itemProgress = 0);
                } else {
                    itemProgress = data.get(block);
                }
            }

            if (!OreDictionary.getOreName(Block.getStateId(blockState)).matches("^ore")) {
                itemProgress += 1F / (fortuneLevel + 2) + (fortuneLevel + 1) / 2F - 1;
            }

            BLOCK_ITEM_DROP_PROGRESS.get(playerID).put(block, itemProgress);
            block.dropXpOnBlockBreak(event.getWorld(), event.getPos(), (int) Math.round(xpDrops));
        }

        do {
            final int itemsDropped = (int) itemDrops;
            drops.add(new ItemStack(block, itemsDropped));

            if (itemProgress >= 1) {
                itemProgress--;

                BLOCK_ITEM_DROP_PROGRESS.get(player.getPersistentID()).put(block, itemProgress);
            }
        } while (itemProgress >= 1);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBreak(final BreakEvent event) {
        event.setExpToDrop(0);
    }
}
