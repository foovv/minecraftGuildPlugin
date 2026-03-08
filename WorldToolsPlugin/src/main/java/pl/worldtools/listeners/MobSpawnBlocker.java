package pl.worldtools.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.EnumSet;
import java.util.Set;

public class MobSpawnBlocker implements Listener {

    private static final Set<EntityType> BLOCKED_MOBS = EnumSet.of(
            EntityType.SHEEP,
            EntityType.ARMADILLO,
            EntityType.PIG
    );

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (BLOCKED_MOBS.contains(event.getEntityType())) {
            event.setCancelled(true);
        }
    }
}
