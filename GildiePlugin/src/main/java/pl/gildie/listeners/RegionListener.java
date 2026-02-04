package pl.gildie.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.gildie.GildiePlugin;
import pl.gildie.managers.GuildManager;
import pl.gildie.managers.GuildManager.Guild;

public class RegionListener implements Listener {

    private final GildiePlugin plugin;

    public RegionListener(GildiePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        GuildManager gm = plugin.getGuildManager();
        Guild guild = gm.getGuildAt(event.getBlock().getLocation());

        if (guild != null) {
            if (!guild.getOwner().equals(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "To teren wrogiej gildii: [" + guild.getTag() + "]");
                return;
            }
        }
        
        // Heart protection
        if (event.getBlock().getType() == Material.SPONGE && event.getBlock().getLocation().getBlockY() == 35) {
             // Check if it IS a guild heart (simple check by location match with known guilds or just protect all sponges at 35 in guild areas)
             if (guild != null) {
                 event.setCancelled(true);
                 player.sendMessage(ChatColor.RED + "Nie mozesz zniszczyc serca gildii!");
             }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        GuildManager gm = plugin.getGuildManager();
        Guild guild = gm.getGuildAt(event.getBlock().getLocation());

        if (guild != null) {
            if (!guild.getOwner().equals(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "To teren wrogiej gildii: [" + guild.getTag() + "]");
            }
        }
    }
}
