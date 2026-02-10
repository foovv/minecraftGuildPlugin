package pl.gildie.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.gildie.GildiePlugin;
import pl.gildie.managers.GuildManager;

public class CombatListener implements Listener {

    private final GildiePlugin plugin;

    public CombatListener(GildiePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild attackerGuild = gm.getGuildByMember(attacker.getUniqueId());
        GuildManager.Guild victimGuild = gm.getGuildByMember(victim.getUniqueId());

        // Check if both are in the same guild
        if (attackerGuild != null && victimGuild != null && attackerGuild.getTag().equals(victimGuild.getTag())) {
            if (attackerGuild.isPvpEnabled()) {
                // PvP is enabled - they can hit each other, but no damage and no combat tag
                event.setDamage(0);
                return;
            } else {
                // PvP is disabled - cancel event
                event.setCancelled(true);
                return;
            }
        }

        // Tag both players for combat
        plugin.getCombatManager().tagPlayer(attacker.getUniqueId());
        plugin.getCombatManager().tagPlayer(victim.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getCombatManager().isTagged(player.getUniqueId())) {
            // Kill the player
            player.setHealth(0);
            plugin.getCombatManager().removeTag(player.getUniqueId());
            
            // Broadcast death message
            Bukkit.broadcast(Component.text(player.getName() + " wylogowal sie podczas walki i zginal!", NamedTextColor.RED));
        }
    }
}
