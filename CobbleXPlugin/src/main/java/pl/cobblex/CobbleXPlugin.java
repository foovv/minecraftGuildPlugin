package pl.cobblex;

import org.bukkit.plugin.java.JavaPlugin;
import pl.cobblex.commands.CxCommand;
import pl.cobblex.listeners.CxListener;

import pl.cobblex.commands.RepairCommand;

public class CobbleXPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("cx").setExecutor(new CxCommand());
        getCommand("naprawkilof").setExecutor(new RepairCommand());
        getServer().getPluginManager().registerEvents(new CxListener(), this);
        getLogger().info("CobbleXPlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CobbleXPlugin disabled!");
    }
}
