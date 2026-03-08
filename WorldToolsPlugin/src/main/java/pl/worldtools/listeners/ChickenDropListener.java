package pl.worldtools.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ChickenDropListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Chicken) {
            // 50% chance to drop arrows
            if (random.nextInt(100) < 50) {
                // amount from 1 to 4
                int amount = random.nextInt(4) + 1;
                event.getDrops().add(new ItemStack(Material.ARROW, amount));
            }
        }
    }
}
