package transfarmer.deterministicloot.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import transfarmer.deterministicloot.Main;
import transfarmer.deterministicloot.util.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = Main.MOD_ID)
public class EntityDrops {
    public static final Map<Class<? extends EntityLivingBase>, Float> ENTITY_XP_DROPS = new HashMap<>();
    public static final Map<UUID, Map<Class<? extends EntityLivingBase>, Float>> ENTITY_XP_DROP_PROGRESS = new HashMap<>();

    @SubscribeEvent
    public static void onExperienceDrop(final LivingExperienceDropEvent event) {
        final EntityLivingBase entity = event.getEntityLiving();
        final Class<? extends EntityLivingBase> entityClass = entity.getClass();
        final Map<Class<? extends EntityLivingBase>, Float> progress;
        final EntityPlayer attackingPlayer = event.getAttackingPlayer();
        final Method getExperiencePoints;
        int dropped = 0;

        if (!(entity instanceof EntityPlayer)) {
            float average = 0;

            if (!ENTITY_XP_DROPS.containsKey(entityClass)) {
                try {
                    getExperiencePoints = ReflectionHelper.getNewestMethod(entity, "getExperiencePoints", EntityPlayer.class);
                    getExperiencePoints.setAccessible(true);
                    Main.LOGGER.warn(getExperiencePoints.getDeclaringClass());

                    final int iterations = 100000;
                    final int inaccuracy = 100;
                    final int significance = iterations / inaccuracy;

                    for (int i = 0; i < iterations; i++) {
                        average += (int) getExperiencePoints.invoke(entity, attackingPlayer);
                    }

                    getExperiencePoints.setAccessible(false);

                    average = (float) Math.round(average / significance) / inaccuracy;
                    ENTITY_XP_DROPS.put(entityClass, average);
                } catch (final IllegalAccessException | InvocationTargetException exception) {
                    exception.printStackTrace();
                }
            } else {
                average = ENTITY_XP_DROPS.get(entityClass);
            }

            if (attackingPlayer != null) {
                final UUID playerID = attackingPlayer.getPersistentID();

                if (!ENTITY_XP_DROP_PROGRESS.containsKey(playerID)) {
                    progress = new HashMap<>();
                    progress.put(entityClass, average);
                    ENTITY_XP_DROP_PROGRESS.put(playerID, progress);
                } else {
                    progress = ENTITY_XP_DROP_PROGRESS.get(playerID);

                    if (!progress.containsKey(entityClass)) {
                        progress.put(entityClass, average);
                    } else {
                        progress.put(entityClass, progress.get(entityClass) + average);
                    }
                }

                dropped = progress.get(entityClass).intValue();

                progress.put(entityClass, progress.get(entityClass) - dropped);
            } else {
                dropped = (int) average;
            }

            Main.LOGGER.warn(average);
        } else {
            getExperiencePoints = ReflectionHelper.getNewestMethod(entity, "getExperiencePoints", EntityPlayer.class);

            try {
                getExperiencePoints.setAccessible(true);
                dropped = (int) getExperiencePoints.invoke(entity, attackingPlayer);
                getExperiencePoints.setAccessible(false);
            } catch (final IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        }

        event.setDroppedExperience(dropped);
        Main.LOGGER.warn(event.getDroppedExperience());
    }
}
