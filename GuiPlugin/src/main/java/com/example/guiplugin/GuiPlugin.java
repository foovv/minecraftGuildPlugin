package com.example.guiplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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

    private static final Component GUI_TITLE = Component.text("Itemy na gildie", NamedTextColor.GRAY);

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
        if (command.getName().equalsIgnoreCase("testg")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Only players can use this command!", NamedTextColor.RED));
                return true;
            }

            Player player = (Player) sender;

            // Check for subcommand "itemy"
            if (args.length > 0 && args[0].equalsIgnoreCase("itemy")) {
                // Create a 27-slot inventory (3 rows)
                Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);

                // Create a diamond with custom name "test"
                ItemStack diamond = new ItemStack(Material.DIAMOND);
                ItemMeta meta = diamond.getItemMeta();
                if (meta != null) {
                    meta.displayName(Component.text("test", NamedTextColor.AQUA));
                    diamond.setItemMeta(meta);
                }

                // Place the diamond in the center (slot 13 - middle of 3 rows)
                gui.setItem(13, diamond);

                // Open the GUI for the player
                player.openInventory(gui);

                return true;
            } else {
                player.sendMessage(Component.text("Usage: /g itemy", NamedTextColor.RED));
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
        if (event.getView().title().equals(GUI_TITLE)) {
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
        if (event.getView().title().equals(GUI_TITLE)) {
            // Cancel the event to prevent item dragging
            event.setCancelled(true);
        }
    }
}
