package pl.stonedrop;

import org.bukkit.plugin.java.JavaPlugin;
import pl.stonedrop.commands.DropCommand;
import pl.stonedrop.listeners.BlockBreakListener;
import pl.stonedrop.listeners.GuiListener;

public class StoneDropPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("drop").setExecutor(new DropCommand());
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        
        getLogger().info("StoneDropPlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("StoneDropPlugin disabled!");
    }
}
