package pl.gildie.tasks;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.gildie.GildiePlugin;
import pl.gildie.managers.GuildManager;
import pl.gildie.managers.GuildManager.Guild;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuildTask extends BukkitRunnable {

    private final GildiePlugin plugin;
    private final Map<UUID, BossBar> activeBars;

    public GuildTask(GildiePlugin plugin) {
        this.plugin = plugin;
        this.activeBars = new HashMap<>();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            GuildManager gm = plugin.getGuildManager();
            Guild guild = gm.getGuildAt(player.getLocation());
            
            BossBar bar = activeBars.get(player.getUniqueId());

            if (guild != null) {
                // Check membership for color
                BarColor targetColor = BarColor.RED;
                org.bukkit.ChatColor chatColor = org.bukkit.ChatColor.RED;
                if (guild.isMember(player.getUniqueId())) {
                    targetColor = BarColor.GREEN;
                    chatColor = org.bukkit.ChatColor.GREEN;
                }
                
                String targetTitle = chatColor + guild.getTag();
                
                if (bar == null) {
                    bar = Bukkit.createBossBar(targetTitle, targetColor, BarStyle.SOLID);
                    bar.addPlayer(player);
                    activeBars.put(player.getUniqueId(), bar);
                    player.sendMessage(org.bukkit.ChatColor.GREEN + "Wkroczyles na teren gildii " + targetTitle);
                } else {
                    // Update color if needed
                    if (bar.getColor() != targetColor) {
                        bar.setColor(targetColor);
                    }
                    
                    if (!bar.getTitle().equals(targetTitle)) {
                        player.sendMessage(org.bukkit.ChatColor.RED + "Opusciles teren gildii " + bar.getTitle());
                        bar.setTitle(targetTitle);
                        player.sendMessage(org.bukkit.ChatColor.GREEN + "Wkroczyles na teren gildii " + targetTitle);
                    }
                }
                
                // Update progress based on distance
                double progress = guild.getProgress(player.getLocation());
                // Clamp progress just in case
                if (progress < 0.0) progress = 0.0;
                if (progress > 1.0) progress = 1.0;
                
                bar.setProgress(progress);
                
            } else {
                // Player is NOT in a guild region
                if (bar != null) {
                    player.sendMessage(org.bukkit.ChatColor.RED + "Opusciles teren gildii " + bar.getTitle());
                    bar.removePlayer(player);
                    activeBars.remove(player.getUniqueId());
                }
            }
        }
        
        // Clean up offline players from map (optional but good practice)
        activeBars.keySet().removeIf(uuid -> Bukkit.getPlayer(uuid) == null);
    }
}
