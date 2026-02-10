package pl.gildie.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import pl.gildie.GildiePlugin;
import pl.gildie.managers.GuildManager;
import pl.gildie.managers.GuildManager.Guild;
import pl.gildie.managers.GuildPermission;

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
            // Heart protection
            if (event.getBlock().getType() == Material.SPONGE && event.getBlock().getLocation().getBlockY() == 35) {
                event.setCancelled(true);
                player.sendMessage(Component.text("Nie mozesz zniszczyc serca gildii!", NamedTextColor.RED));
                return;
            }

            if (guild.getOwner().equals(player.getUniqueId())) return;

            if (guild.isMember(player.getUniqueId())) {
                Material type = event.getBlock().getType();
                if (type == Material.STONE && guild.hasPermission(player.getUniqueId(), GuildPermission.MINE_STONE)) {
                    return;
                }
                if (type == Material.OBSIDIAN && guild.hasPermission(player.getUniqueId(), GuildPermission.MINE_OBSIDIAN)) {
                    return;
                }
                
                event.setCancelled(true);
                player.sendMessage(Component.text("Nie posiadasz uprawnien do niszczenia tego bloku!", NamedTextColor.RED));
                return;
            } else {
                event.setCancelled(true);
                player.sendMessage(Component.text("To teren wrogiej gildii: [" + guild.getTag() + "]", NamedTextColor.RED));
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        GuildManager gm = plugin.getGuildManager();
        Guild guild = gm.getGuildAt(event.getBlock().getLocation());

        if (guild != null) {
            if (guild.getOwner().equals(player.getUniqueId())) return;

            if (guild.isMember(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(Component.text("Nie posiadasz uprawnien do budowania tutaj!", NamedTextColor.RED));
            } else {
                event.setCancelled(true);
                player.sendMessage(Component.text("To teren wrogiej gildii: [" + guild.getTag() + "]", NamedTextColor.RED));
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        GuildManager gm = plugin.getGuildManager();
        Guild guild = gm.getGuildAt(event.getBlockClicked().getLocation());

        if (guild != null) {
            if (guild.getOwner().equals(player.getUniqueId())) return;

            if (guild.isMember(player.getUniqueId())) {
                Material bucket = event.getBucket();
                if (bucket == Material.WATER_BUCKET && guild.hasPermission(player.getUniqueId(), GuildPermission.POUR_WATER)) {
                    return;
                }
                if (bucket == Material.LAVA_BUCKET && guild.hasPermission(player.getUniqueId(), GuildPermission.POUR_LAVA)) {
                    return;
                }

                event.setCancelled(true);
                player.sendMessage(Component.text("Nie posiadasz uprawnien do wylewania tego plynu!", NamedTextColor.RED));
            } else {
                event.setCancelled(true);
                player.sendMessage(Component.text("To teren wrogiej gildii: [" + guild.getTag() + "]", NamedTextColor.RED));
            }
        }
    }
}
