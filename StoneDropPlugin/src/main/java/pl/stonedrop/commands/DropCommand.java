package pl.stonedrop.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class DropCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tylko dla graczy!");
            return true;
        }

        Player player = (Player) sender;
        openDropGui(player);
        return true;
    }

    private void openDropGui(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§6Drop z kamienia");

        // Iron Ore
        inv.setItem(12, createGuiItem(Material.IRON_ORE, "§fRuda Zelaza", "§eSzansa: §a2%"));
        
        // Gold Ore
        inv.setItem(14, createGuiItem(Material.GOLD_ORE, "§6Ruda Zlota", "§eSzansa: §a1%"));

        player.openInventory(inv);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}
