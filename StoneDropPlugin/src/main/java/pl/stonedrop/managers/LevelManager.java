package pl.stonedrop.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

public class LevelManager {

    private final JavaPlugin plugin;
    private final NamespacedKey LEVEL_KEY;
    private final NamespacedKey XP_KEY;

    public LevelManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.LEVEL_KEY = new NamespacedKey(plugin, "level");
        this.XP_KEY = new NamespacedKey(plugin, "xp");
    }

    public int getLevel(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.getOrDefault(LEVEL_KEY, PersistentDataType.INTEGER, 1);
    }

    public void setLevel(Player player, int level) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(LEVEL_KEY, PersistentDataType.INTEGER, level);
    }

    public int getXp(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.getOrDefault(XP_KEY, PersistentDataType.INTEGER, 0);
    }

    public void setXp(Player player, int xp) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(XP_KEY, PersistentDataType.INTEGER, xp);
    }

    public int getRequiredXp(int level) {
        return level * 150;
    }

    public void addXp(Player player, int amount) {
        int currentXp = getXp(player);
        int currentLevel = getLevel(player);
        int newXp = currentXp + amount;
        
        int requiredXp = getRequiredXp(currentLevel);

        if (newXp >= requiredXp) {
            newXp -= requiredXp;
            currentLevel++;
            setLevel(player, currentLevel);
            
            player.sendMessage(Component.text("Awansowales na poziom " + currentLevel + "!", NamedTextColor.GOLD));
            
            Title title = Title.title(
                Component.text("AWANS!", NamedTextColor.GOLD),
                Component.text("Poziom: " + currentLevel, NamedTextColor.YELLOW),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3500), Duration.ofMillis(1000))
            );
            player.showTitle(title);
        }
        
        setXp(player, newXp);
    }
}
