package pl.stonedrop.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import pl.stonedrop.commands.DropCommand;
import pl.stonedrop.managers.DropManager;
import pl.stonedrop.managers.DropSettings;

public class GuiListener implements Listener {

    private final DropManager dropManager;

    public GuiListener(DropManager dropManager) {
        this.dropManager = dropManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(event.getView().title()).equals("Drop z kamienia")) {
            event.setCancelled(true);
            
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();
            DropSettings settings = dropManager.getSettings(player);
            
            int slot = event.getRawSlot();
            if (slot == 12) {
                settings.setIronEnabled(!settings.isIronEnabled());
                DropCommand.openDropGui(player, dropManager);
            } else if (slot == 14) {
                settings.setGoldEnabled(!settings.isGoldEnabled());
                DropCommand.openDropGui(player, dropManager);
            } else if (slot == 26) {
                settings.setCobbleEnabled(!settings.isCobbleEnabled());
                DropCommand.openDropGui(player, dropManager);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(event.getView().title()).equals("Drop z kamienia")) {
            event.setCancelled(true);
        }
    }
}
