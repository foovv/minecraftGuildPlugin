package pl.gildie.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import pl.gildie.GildiePlugin;
import pl.gildie.managers.GuildManager;

public class WaypointListener implements Listener {

    private final GildiePlugin plugin;

    public WaypointListener(GildiePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GuildManager.Guild guild = plugin.getGuildManager().getGuildByMember(player.getUniqueId());
        
        if (guild != null && plugin.getWaypointManager() != null) {
            // Delay slightly to ensure client is ready
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    plugin.getWaypointManager().showWaypointsTo(player, guild);
                }
            }, 20L);
        }
    }
    
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
         Player player = event.getPlayer();
         GuildManager.Guild guild = plugin.getGuildManager().getGuildByMember(player.getUniqueId());
         
         if (guild != null && plugin.getWaypointManager() != null) {
             // If player moves to the world where guild is, show it
             if (guild.getCenter().getWorld().getName().equals(player.getWorld().getName())) {
                 plugin.getWaypointManager().showWaypointsTo(player, guild);
             } else {
                 plugin.getWaypointManager().removeWaypoint(player);
             }
         }
    }
}
