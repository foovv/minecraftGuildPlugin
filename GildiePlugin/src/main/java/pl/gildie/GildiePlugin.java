package pl.gildie;

import org.bukkit.plugin.java.JavaPlugin;
import pl.gildie.commands.GuildCommand;
import pl.gildie.listeners.RegionListener;
import pl.gildie.managers.GuildManager;

public class GildiePlugin extends JavaPlugin {

    private GuildManager guildManager;
    private static GildiePlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        this.guildManager = new GuildManager(this);
        
        getCommand("g").setExecutor(new GuildCommand(this));
        getServer().getPluginManager().registerEvents(new RegionListener(this), this);
        getServer().getPluginManager().registerEvents(new pl.gildie.listeners.GuiListener(), this);
        
        new pl.gildie.tasks.GuildTask(this).runTaskTimer(this, 20L, 5L);

        getLogger().info("Plugin Gildie zostal wlaczony!");
    }

    @Override
    public void onDisable() {
        if (guildManager != null) {
            guildManager.saveGuilds();
        }
        getLogger().info("Plugin Gildie zostal wylaczony!");
    }
    
    public GuildManager getGuildManager() {
        return guildManager;
    }
    
    public static GildiePlugin getInstance() {
        return instance;
    }
}
