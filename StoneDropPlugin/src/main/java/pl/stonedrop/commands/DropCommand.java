package pl.stonedrop.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.stonedrop.managers.DropManager;
import pl.stonedrop.managers.DropSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DropCommand implements CommandExecutor {

    private final DropManager dropManager;

    public DropCommand(DropManager dropManager) {
        this.dropManager = dropManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tylko dla graczy!");
            return true;
        }

        Player player = (Player) sender;
        openDropGui(player, dropManager);
        return true;
    }

    public static void openDropGui(Player player, DropManager dropManager) {
        Inventory inv = Bukkit.createInventory(null, 27, "\u00A78Drop z kamienia");
        DropSettings settings = dropManager.getSettings(player);

        // Iron Ore
        inv.setItem(12, createGuiItem(Material.IRON_ORE, "\u00A7fRuda Zelaza", settings.isIronEnabled(), "\u00A7eSzansa: \u00A7a2%"));
        
        // Gold Ore
        inv.setItem(14, createGuiItem(Material.GOLD_ORE, "\u00A76Ruda Zlota", settings.isGoldEnabled(), "\u00A7eSzansa: \u00A7a1%"));

        // Cobble
        inv.setItem(26, createGuiItem(Material.COBBLESTONE, "\u00A77Cobblestone", settings.isCobbleEnabled(), "\u00A7eSzansa: \u00A7a100%"));

        player.openInventory(inv);
    }

    private static ItemStack createGuiItem(Material material, String name, boolean enabled, String... customLore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> lore = new ArrayList<>(Arrays.asList(customLore));
            if (enabled) {
                lore.add("\u00A7aDrop jest wlaczony");
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                lore.add("\u00A7cDrop jest wylaczony");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
