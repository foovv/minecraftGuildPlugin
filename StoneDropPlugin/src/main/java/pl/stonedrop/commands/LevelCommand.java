package pl.stonedrop.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.stonedrop.managers.LevelManager;

public class LevelCommand implements CommandExecutor {

    private final LevelManager levelManager;

    public LevelCommand(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Komenda tylko dla graczy!"));
            return true;
        }

        Player player = (Player) sender;
        int level = levelManager.getLevel(player);
        int xp = levelManager.getXp(player);
        int required = levelManager.getRequiredXp(level);
        
        int percent = (int) (((double) xp / required) * 100);

        player.sendMessage(Component.text("----------- ", NamedTextColor.DARK_GRAY)
            .append(Component.text("POZIOM KOPANIA", NamedTextColor.GOLD))
            .append(Component.text(" -----------", NamedTextColor.DARK_GRAY)));
            
        player.sendMessage(Component.text("Poziom: ", NamedTextColor.GRAY)
            .append(Component.text(level, NamedTextColor.YELLOW)));
            
        player.sendMessage(Component.text("Postep: ", NamedTextColor.GRAY)
            .append(Component.text(xp, NamedTextColor.YELLOW))
            .append(Component.text(" / ", NamedTextColor.GRAY))
            .append(Component.text(required, NamedTextColor.GOLD))
            .append(Component.text(" (" + percent + "%)", NamedTextColor.GRAY)));
            
        player.sendMessage(Component.text("--------------------------------------", NamedTextColor.DARK_GRAY));

        return true;
    }
}
