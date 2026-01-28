package com.example.guiplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class GuiPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("GuiPlugin enabled!");
        // Register event listener
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("GuiPlugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("g")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            Player player = (Player) sender;

            // Check for subcommand "itemy"
            if (args.length > 0 && args[0].equalsIgnoreCase("itemy")) {
                // Create a 27-slot inventory (3 rows)
                Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GRAY + "Itemy na gildie");

                // Create a diamond with custom name "test"
                ItemStack diamond = new ItemStack(Material.DIAMOND);
                ItemMeta meta = diamond.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ChatColor.AQUA + "test");
                    diamond.setItemMeta(meta);
                }

                // Place the diamond in the center (slot 13 - middle of 3 rows)
                gui.setItem(13, diamond);

                // Open the GUI for the player
                player.openInventory(gui);

                return true;
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /g itemy");
                return true;
            }
        }
        return false;
    }

    /**
     * Prevent players from clicking items out of the GUI
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        // Check if this is our GUI by title
        if (view.getTitle().equals(ChatColor.GRAY + "Itemy na gildie")) {
            // Cancel the event to prevent item movement
            event.setCancelled(true);
        }
    }

    /**
     * Prevent players from dragging items into or out of the GUI
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryView view = event.getView();
        // Check if this is our GUI by title
        if (view.getTitle().equals(ChatColor.GRAY + "Itemy na gildie")) {
            // Cancel the event to prevent item dragging
            event.setCancelled(true);
        }
    }
}
