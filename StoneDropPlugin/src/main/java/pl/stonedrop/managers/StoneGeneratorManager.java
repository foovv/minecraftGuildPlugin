package pl.stonedrop.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class StoneGeneratorManager {

    private final JavaPlugin plugin;
    private final Set<java.util.UUID> enabledPlayers = new HashSet<>();

    public StoneGeneratorManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean toggleGenerator(java.util.UUID playerId) {
        if (enabledPlayers.contains(playerId)) {
            enabledPlayers.remove(playerId);
            return false; // Disabled
        } else {
            enabledPlayers.add(playerId);
            return true; // Enabled
        }
    }

    public boolean isGeneratorEnabled(java.util.UUID playerId) {
        return enabledPlayers.contains(playerId);
    }

    public void scheduleRegeneration(Location location) {
        // 30 ticks = 1.5 seconds
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Material type = location.getBlock().getType();
            if (type == Material.AIR || type == Material.CAVE_AIR || type == Material.VOID_AIR) {
                location.getBlock().setType(Material.STONE);
            }
        }, 30L);
    }
}
