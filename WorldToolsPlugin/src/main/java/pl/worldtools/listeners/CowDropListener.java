package pl.worldtools.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class CowDropListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Cow) {
            // Remove vanilla targeted drops first
            event.getDrops().removeIf(item -> item.getType() == Material.LEATHER || item.getType() == Material.PAPER || item.getType() == Material.BOOK);

            // Add custom drops
            // 100% chance for leather
            event.getDrops().add(new ItemStack(Material.LEATHER, 1));

            // 50% chance for paper
            if (random.nextInt(100) < 50) {
                event.getDrops().add(new ItemStack(Material.PAPER, 1));
            }

            // 10% chance for book
            if (random.nextInt(100) < 10) {
                event.getDrops().add(new ItemStack(Material.BOOK, 1));
            }
        }
    }
}
