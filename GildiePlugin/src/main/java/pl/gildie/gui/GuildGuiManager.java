package pl.gildie.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.gildie.managers.GuildManager;
import pl.gildie.managers.GuildPermission;
import pl.gildie.GildiePlugin;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildGuiManager {

    private static final Component PANEL_TITLE = Component.text("Panel Gildyjny", NamedTextColor.DARK_GRAY);
    private static final Component SELECTOR_TITLE = Component.text("Wybierz gracza", NamedTextColor.DARK_GRAY);

    public static void openMainPanel(Player player, GuildManager.Guild guild) {
        Inventory inv = Bukkit.createInventory(null, 27, PANEL_TITLE);

        ItemStack permissionsItem = new ItemStack(Material.BOOK);
        ItemMeta meta = permissionsItem.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Permisje", NamedTextColor.YELLOW));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Kliknij, aby zarzadzac uprawnieniami", NamedTextColor.GRAY));
            meta.lore(lore);
            permissionsItem.setItemMeta(meta);
        }

        inv.setItem(13, permissionsItem);
        player.openInventory(inv);
    }

    public static void openMemberSelector(Player player, GuildManager.Guild guild) {
        Inventory inv = Bukkit.createInventory(null, 54, SELECTOR_TITLE);

        int playerRank = guild.getRank(player.getUniqueId());

        for (UUID memberId : guild.getMembers()) {
            if (guild.getRank(memberId) >= playerRank) continue;

            OfflinePlayer op = Bukkit.getOfflinePlayer(memberId);
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta == null) continue;
            
            meta.setOwningPlayer(op);
            meta.displayName(Component.text(op.getName() != null ? op.getName() : "Gracz", NamedTextColor.GREEN));
            
            // Store UUID in PDC
            meta.getPersistentDataContainer().set(GildiePlugin.TARGET_PLAYER_KEY, PersistentDataType.STRING, memberId.toString());

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Dynamic", NamedTextColor.DARK_GRAY));
            meta.lore(lore);
            head.setItemMeta(meta);
            inv.addItem(head);
        }

        player.openInventory(inv);
    }

    public static void openPermissionManager(Player player, GuildManager.Guild guild, UUID targetUUID) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
        String targetName = target.getName() != null ? target.getName() : "Gracz";
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Uprawnienia: " + targetName, NamedTextColor.DARK_GRAY));

        // Slots for permissions
        inv.setItem(10, createPermissionItem(guild, targetUUID, GuildPermission.MINE_STONE, Material.STONE));
        inv.setItem(12, createPermissionItem(guild, targetUUID, GuildPermission.MINE_OBSIDIAN, Material.OBSIDIAN));
        inv.setItem(14, createPermissionItem(guild, targetUUID, GuildPermission.POUR_WATER, Material.WATER_BUCKET));
        inv.setItem(16, createPermissionItem(guild, targetUUID, GuildPermission.POUR_LAVA, Material.LAVA_BUCKET));

        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.displayName(Component.text("Powrot", NamedTextColor.RED));
            
            backMeta.getPersistentDataContainer().set(GildiePlugin.TARGET_PLAYER_KEY, PersistentDataType.STRING, targetUUID.toString());
            back.setItemMeta(backMeta);
        }
        inv.setItem(26, back);

        player.openInventory(inv);
    }

    private static ItemStack createPermissionItem(GuildManager.Guild guild, UUID targetUUID, GuildPermission perm, Material icon) {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        
        boolean has = guild.hasPermission(targetUUID, perm);
        meta.displayName(Component.text(perm.getName(), NamedTextColor.YELLOW));
        
        // Store UUID in PDC
        meta.getPersistentDataContainer().set(GildiePlugin.TARGET_PLAYER_KEY, PersistentDataType.STRING, targetUUID.toString());

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Status: ", NamedTextColor.GRAY).append(has ? Component.text("WLACZONE", NamedTextColor.GREEN) : Component.text("WYLACZONE", NamedTextColor.RED)));
        lore.add(Component.empty());
        lore.add(Component.text("Kliknij, aby " + (has ? "wylaczyc" : "wlaczyc"), NamedTextColor.YELLOW));
        meta.lore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
}
