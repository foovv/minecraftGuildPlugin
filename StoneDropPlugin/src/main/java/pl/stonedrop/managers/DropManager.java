package pl.stonedrop.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DropManager {

    private final Map<UUID, DropSettings> playerSettings = new HashMap<>();

    public DropSettings getSettings(Player player) {
        return playerSettings.computeIfAbsent(player.getUniqueId(), k -> new DropSettings());
    }

    public void removeSettings(Player player) {
        playerSettings.remove(player.getUniqueId());
    }
}
