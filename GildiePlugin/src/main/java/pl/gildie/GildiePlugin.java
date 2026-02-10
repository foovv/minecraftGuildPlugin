package pl.gildie;

import org.bukkit.plugin.java.JavaPlugin;
import pl.gildie.commands.GuildCommand;
import pl.gildie.listeners.CombatListener;
import pl.gildie.listeners.RegionListener;
import pl.gildie.managers.CombatManager;
import pl.gildie.managers.GuildManager;

public class GildiePlugin extends JavaPlugin {

    private GuildManager guildManager;
    private CombatManager combatManager;
    private pl.gildie.managers.WaypointManager waypointManager;
    private static GildiePlugin instance;
    public static org.bukkit.NamespacedKey TARGET_PLAYER_KEY;

    @Override
    public void onEnable() {
        instance = this;
        TARGET_PLAYER_KEY = new org.bukkit.NamespacedKey(this, "target_player");
        saveDefaultConfig();
        
        this.guildManager = new GuildManager(this);
        this.combatManager = new CombatManager();
        this.waypointManager = new pl.gildie.managers.WaypointManager(this);
        
        this.guildManager.setWaypointManager(this.waypointManager);
        
        // Load waypoints (delayed to ensure worlds are loaded if needed, though onEnable is usually fine)
        getServer().getScheduler().runTask(this, () -> {
            this.waypointManager.reloadAll();
        });
        
        getCommand("g").setExecutor(new GuildCommand(this));
        getCommand("gamma").setExecutor(new pl.gildie.commands.GammaCommand());
        getServer().getPluginManager().registerEvents(new RegionListener(this), this);
        getServer().getPluginManager().registerEvents(new pl.gildie.listeners.GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new pl.gildie.listeners.WaypointListener(this), this);
        
        new pl.gildie.tasks.GuildTask(this).runTaskTimer(this, 20L, 5L);
        new pl.gildie.tasks.CombatTask(this).runTaskTimer(this, 20L, 20L); // Every second

        getLogger().info("Plugin Gildie zostal wlaczony!");
    }

    @Override
    public void onDisable() {
        if (guildManager != null) {
            guildManager.saveGuilds();
        }
        if (waypointManager != null) {
            waypointManager.removeAll();
        }
        getLogger().info("Plugin Gildie zostal wylaczony!");
    }
    
    public GuildManager getGuildManager() {
        return guildManager;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }
    
    public pl.gildie.managers.WaypointManager getWaypointManager() {
        return waypointManager;
    }
    
    public static GildiePlugin getInstance() {
        return instance;
    }
}
