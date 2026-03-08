package pl.cobblex.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class RepairCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tylko gracz moze uzyc tej komendy!");
            return true;
        }

        Player player = (Player) sender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(Component.text("Musisz trzymac przedmiot w rece!", NamedTextColor.RED));
            return true;
        }

        if (!itemInHand.getType().name().endsWith("_PICKAXE")) {
            player.sendMessage(Component.text("Mozesz naprawic tylko kilof!", NamedTextColor.RED));
            return true;
        }

        ItemMeta meta = itemInHand.getItemMeta();
        if (!(meta instanceof Damageable)) {
            player.sendMessage(Component.text("Tego przedmiotu nie da sie naprawic!", NamedTextColor.RED));
            return true;
        }

        Damageable damageable = (Damageable) meta;
        if (!damageable.hasDamage()) {
            player.sendMessage(Component.text("Ten kilof jest juz w pelni naprawiony!", NamedTextColor.RED));
            return true;
        }

        if (player.getLevel() < 15) {
            player.sendMessage(Component.text("Potrzebujesz 15 poziomu doswiadczenia, by naprawic kilof!", NamedTextColor.RED));
            return true;
        }

        // Deduct exactly 15 levels
        player.setLevel(player.getLevel() - 15);

        // Repair item
        damageable.setDamage(0);
        itemInHand.setItemMeta(meta);

        player.sendMessage(Component.text("Twoj kilof zostal naprawiony!", NamedTextColor.GREEN));
        return true;
    }
}
