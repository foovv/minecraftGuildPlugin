package pl.worldtools;

import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.java.JavaPlugin;
import pl.worldtools.listeners.CowDropListener;
import pl.worldtools.listeners.MobSpawnBlocker;
import pl.worldtools.listeners.ChickenDropListener;
import pl.worldtools.listeners.MobFreezeListener;

public class WorldToolsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new CowDropListener(), this);
        getServer().getPluginManager().registerEvents(new MobSpawnBlocker(), this);
        getServer().getPluginManager().registerEvents(new ChickenDropListener(), this);
        getServer().getPluginManager().registerEvents(new MobFreezeListener(), this);

        // Set WorldBorder to 3000
        for (World world : getServer().getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                WorldBorder border = world.getWorldBorder();
                border.setSize(3000);
            }
        }

        getLogger().info("WorldToolsPlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("WorldToolsPlugin disabled!");
    }
}
