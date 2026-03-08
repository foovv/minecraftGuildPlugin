package pl.cobblex.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class CxCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tylko gracz moze uzyc tej komendy!");
            return true;
        }

        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();

        int cobbleCount = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == Material.COBBLESTONE) {
                cobbleCount += item.getAmount();
            }
        }

        int requiredAmount = 12 * 64; // 768

        if (cobbleCount < requiredAmount) {
            player.sendMessage(Component.text("Nie wystarczająco cobbl'a! (Wymagane: 12 stacków)", NamedTextColor.RED));
            return true;
        }

        // Remove cobblestone
        int removed = 0;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() == Material.COBBLESTONE) {
                int amount = item.getAmount();
                if (removed + amount <= requiredAmount) {
                    removed += amount;
                    inventory.setItem(i, null);
                } else {
                    int toRemove = requiredAmount - removed;
                    item.setAmount(amount - toRemove);
                    removed += toRemove;
                }
                if (removed >= requiredAmount) break;
            }
        }

        // Give CobbleX item
        ItemStack cobbleX = new ItemStack(Material.COBBLESTONE);
        ItemMeta meta = cobbleX.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Cobblex", NamedTextColor.GRAY));
            meta.addEnchant(Enchantment.UNBREAKING, 10, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            cobbleX.setItemMeta(meta);
        }

        player.getInventory().addItem(cobbleX);
        player.sendMessage(Component.text("Otrzymales CobbleX!", NamedTextColor.GREEN));

        return true;
    }
}
