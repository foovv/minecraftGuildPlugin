package pl.gildie.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GammaCommand implements CommandExecutor {

    private boolean nightVisionEnabled = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        nightVisionEnabled = !nightVisionEnabled;
        
        Bukkit.getLogger().info("[GildiePlugin] /gamma used by " + sender.getName() + ". Enabled: " + nightVisionEnabled);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (nightVisionEnabled) {
                // Use a large but not MAX_VALUE duration (e.g., 100,000 ticks ~ 1.4 hours)
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 0, false, false));
            } else {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
        }

        Component status = nightVisionEnabled ? Component.text("wlaczone", NamedTextColor.GREEN) : Component.text("wylaczone", NamedTextColor.RED);
        Bukkit.broadcast(Component.text("Widzenie w ciemnosci zostalo ", NamedTextColor.GOLD).append(status).append(Component.text(" dla wszystkich!", NamedTextColor.GOLD)));

        return true;
    }
}
