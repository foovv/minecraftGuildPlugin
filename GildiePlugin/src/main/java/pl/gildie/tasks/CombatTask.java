package pl.gildie.tasks;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.gildie.GildiePlugin;
import pl.gildie.managers.CombatManager;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CombatTask extends BukkitRunnable {

    private final GildiePlugin plugin;

    public CombatTask(GildiePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        CombatManager cm = plugin.getCombatManager();
        Map<UUID, Long> tags = cm.getCombatTags();

        Iterator<Map.Entry<UUID, Long>> iterator = tags.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Long> entry = iterator.next();
            UUID uuid = entry.getKey();
            Player player = Bukkit.getPlayer(uuid);

            if (player == null || !player.isOnline()) {
                iterator.remove();
                continue;
            }

            int remaining = cm.getRemainingSeconds(uuid);

            if (remaining <= 0) {
                // Tag expired
                iterator.remove();
                player.sendActionBar(Component.text("§aMozesz sie bezpiecznie wylogowac"));
            } else {
                // Show countdown
                player.sendActionBar(Component.text("§cAntyLogout: §f" + remaining + "s"));
            }
        }
    }
}
