package pl.worldtools.listeners;

import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobFreezeListener implements Listener {

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Cow || event.getEntity() instanceof Chicken) {
            event.getEntity().setAI(false);
        }
    }
}
