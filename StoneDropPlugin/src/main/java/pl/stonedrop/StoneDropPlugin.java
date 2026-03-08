package pl.stonedrop;

import org.bukkit.plugin.java.JavaPlugin;
import pl.stonedrop.commands.DropCommand;
import pl.stonedrop.managers.StoneGeneratorManager;
import pl.stonedrop.listeners.StoneGeneratorListener;

import pl.stonedrop.listeners.BlockBreakListener;
import pl.stonedrop.listeners.GuiListener;
import pl.stonedrop.managers.DropManager;
import pl.stonedrop.managers.LevelManager;
import pl.stonedrop.commands.LevelCommand;

public class StoneDropPlugin extends JavaPlugin {

    private StoneGeneratorManager stoneGeneratorManager;
    private LevelManager levelManager;
    private DropManager dropManager;

    @Override
    public void onEnable() {
        this.stoneGeneratorManager = new StoneGeneratorManager(this);
        this.levelManager = new LevelManager(this);
        this.dropManager = new DropManager();
        
        getCommand("drop").setExecutor(new DropCommand(dropManager));
        getCommand("lvl").setExecutor(new LevelCommand(levelManager));
        
        getServer().getPluginManager().registerEvents(new GuiListener(dropManager), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(levelManager, dropManager), this);
        getServer().getPluginManager().registerEvents(new StoneGeneratorListener(stoneGeneratorManager), this);
        
        getLogger().info("StoneDropPlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("StoneDropPlugin disabled!");
    }
}
