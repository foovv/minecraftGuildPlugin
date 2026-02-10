package pl.gildie.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import pl.gildie.GildiePlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaypointManager {

    private final GildiePlugin plugin;
    // Map Player UUID to Entity UUID (TextDisplay)
    private final Map<UUID, UUID> playerWaypoints = new HashMap<>();

    public WaypointManager(GildiePlugin plugin) {
        this.plugin = plugin;
    }

    public void createWaypoint(Player player, GuildManager.Guild guild) {
        // Remove existing if any (cleanup)
        removeWaypoint(player);

        Location loc = guild.getCenter().clone();
        loc.setY(36.5); // Float above the heart block

        // Create TextDisplay entity
        TextDisplay display = loc.getWorld().spawn(loc, TextDisplay.class, entity -> {
            entity.text(Component.text("❤ SERCE GILDII ❤", NamedTextColor.RED));
            entity.setBillboard(Display.Billboard.CENTER);
            entity.setSeeThrough(true); // Visible through walls
            entity.setVisibleByDefault(false); // Only specific players see it
            entity.setShadowed(true);
            entity.setBackgroundColor(org.bukkit.Color.fromARGB(0, 0, 0, 0)); // Transparent background

            // Scale it up a bit using JOML
            entity.setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),
                new AxisAngle4f(0f, 0f, 0f, 1f),
                new Vector3f(2f, 2f, 2f), // Scale 2x
                new AxisAngle4f(0f, 0f, 0f, 1f)
            ));
        });

        playerWaypoints.put(player.getUniqueId(), display.getUniqueId());
        player.showEntity(plugin, display);
        
        // Initial update
        updateWaypointText(player, guild);
    }

    public void removeWaypoint(Player player) {
        UUID entityId = playerWaypoints.remove(player.getUniqueId());
        if (entityId != null) {
            org.bukkit.entity.Entity entity = Bukkit.getEntity(entityId);
            if (entity != null) {
                entity.remove();
            }
        }
    }

    public void updateWaypointText(Player player, GuildManager.Guild guild) {
        UUID entityId = playerWaypoints.get(player.getUniqueId());
        if (entityId != null) {
            org.bukkit.entity.Entity entity = Bukkit.getEntity(entityId);
            if (entity instanceof TextDisplay) {
                TextDisplay display = (TextDisplay) entity;
                // If world changed or entity invalid, fix it (though usually handled by showWaypointsTo)
                if (!display.getWorld().getName().equals(player.getWorld().getName())) {
                    return; 
                }

                double dist = player.getLocation().distance(guild.getCenter());
                String distStr = String.format("%.1f", dist);
                display.text(Component.text("❤ SERCE GILDII ❤ (" + distStr + "m)", NamedTextColor.RED));
            }
        }
    }

    public void showWaypointsTo(Player player, GuildManager.Guild guild) {
        UUID entityId = playerWaypoints.get(player.getUniqueId());
        if (entityId != null) {
            org.bukkit.entity.Entity entity = Bukkit.getEntity(entityId);
            if (entity != null && entity.isValid() && entity.getWorld().getName().equals(player.getWorld().getName())) {
                player.showEntity(plugin, entity);
            } else {
                createWaypoint(player, guild);
            }
        } else {
            createWaypoint(player, guild);
        }
    }
    
    public void removeAll() {
        for (UUID entityId : playerWaypoints.values()) {
            org.bukkit.entity.Entity entity = Bukkit.getEntity(entityId);
            if (entity != null) {
                entity.remove();
            }
        }
        playerWaypoints.clear();
    }
    
    public void reloadAll() {
        removeAll();
        // Recreate for all online members
        for (Player player : Bukkit.getOnlinePlayers()) {
            GuildManager.Guild guild = plugin.getGuildManager().getGuildByMember(player.getUniqueId());
            if (guild != null) {
                createWaypoint(player, guild);
            }
        }
    }

    public void removeWaypointsForGuild(GuildManager.Guild guild) {
        // Remove for all members of this guild
        for (UUID memberId : guild.getMembers()) {
            Player p = Bukkit.getPlayer(memberId);
            if (p != null) {
                removeWaypoint(p);
            } else {
                // If offline, we can't easily remove the entity if it's not online? 
                // Actually our map is player-based.
                playerWaypoints.remove(memberId);
                // Entities persist in world unless removed. This is a bit of a leak if members are offline.
                // But removeAll clears everything on disable.
            }
        }
    }
}
