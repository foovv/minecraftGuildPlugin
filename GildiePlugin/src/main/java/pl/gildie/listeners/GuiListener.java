package pl.gildie.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.gildie.GildiePlugin;
import pl.gildie.gui.GuildGuiManager;
import pl.gildie.managers.GuildManager;
import pl.gildie.managers.GuildPermission;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.UUID;

public class GuiListener implements Listener {

    private final GildiePlugin plugin;
    private static final Component ITEMY_TITLE = Component.text("Wymagane Itemy", NamedTextColor.DARK_PURPLE);
    private static final Component PANEL_TITLE = Component.text("Panel Gildyjny", NamedTextColor.DARK_GRAY);
    private static final Component SELECTOR_TITLE = Component.text("Wybierz gracza", NamedTextColor.DARK_GRAY);
    private static final Component PERM_PREFIX = Component.text("Uprawnienia: ", NamedTextColor.DARK_GRAY);

    public GuiListener(GildiePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Component title = event.getView().title();

        if (title.equals(ITEMY_TITLE)) {
            event.setCancelled(true);
            return;
        }

        if (title.equals(PANEL_TITLE)) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            if (clicked.getType() == Material.BOOK) {
                GuildManager.Guild guild = plugin.getGuildManager().getGuildByMember(player.getUniqueId());
                if (guild != null) {
                    GuildGuiManager.openMemberSelector(player, guild);
                }
            }
            return;
        }

        if (title.equals(SELECTOR_TITLE)) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            if (clicked.getType() == Material.PLAYER_HEAD) {
                ItemMeta meta = clicked.getItemMeta();
                if (meta != null) {
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    if (container.has(GildiePlugin.TARGET_PLAYER_KEY, PersistentDataType.STRING)) {
                        String uuidString = container.get(GildiePlugin.TARGET_PLAYER_KEY, PersistentDataType.STRING);
                        if (uuidString != null) {
                            UUID targetUUID = UUID.fromString(uuidString);
                            GuildManager.Guild guild = plugin.getGuildManager().getGuildByMember(player.getUniqueId());
                            if (guild != null) {
                                if (guild.getRank(player.getUniqueId()) > guild.getRank(targetUUID)) {
                                    GuildGuiManager.openPermissionManager(player, guild, targetUUID);
                                } else {
                                    player.sendMessage(Component.text("Nie mozesz zarzadzac uprawnieniami tej osoby!", NamedTextColor.RED));
                                    player.closeInventory();
                                }
                            }
                        }
                    }
                }
            }
            return;
        }

        // Check for permission title match (contains) or starts with logic
        // Since we are strictly using components, "starts with" is tricky if it's constructed.
        // But in GuildGuiManager we construct: Component.text("Uprawnienia: " + targetName, ...)
        // This is a single TextComponent with content "Uprawnienia: Name". 
        // So checking if content starts with "Uprawnienia: " is viable if we access plain text.
        // OR we can check deserialized string. Let's use plain text content for simplicity here.
        // event.getView().title() usually returns a component. 
        // If we assumed simple text components:
        
        // A robust way in Paper/Adventure is to serialize or check children.
        // However, GuildGuiManager created it as: Component.text("Uprawnienia: " + targetName, NamedTextColor.DARK_GRAY)
        // So it's one text component.
        
        // We can cast to TextComponent or use plain text serializer.
        // Or String comparison on legacy? simpler for matching prefix in this specific case.
        // Let's use legacy comparison for the prefix check as it's safe if we know the input format.
        // Wait, if I used Component to create it, `event.getView().getTitle()` (legacy) should still work and return the legacy string.
        // That is deprecated, but simpler to implement "startsWith" than implementing a Component visitor.
        // But user asked to fix deprecated calls.
        
        // Correct Adventure way:
        // PlainTextComponentSerializer.plainText().serialize(title) -> "Uprawnienia: Nick"
        
        // Let's use PlainTextComponentSerializer.
        // I need to import it? It's in adventure-text-serializer-plain.
        
        // Or I can just continue using legacy string for checking startsWith if I accept one deprecation warning?
        // No, user wants ALL fixed.
        
        // I'll try to rely on plain text check.
        // import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
        
        // String plainTitle = PlainTextComponentSerializer.plainText().serialize(title);
        // if (plainTitle.startsWith("Uprawnienia: ")) ...
        
        // I will trust the legacy comparison logic using PlainText serializer.
        
    }
}
