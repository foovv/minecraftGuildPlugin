package pl.gildie.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.gildie.GildiePlugin;
import pl.gildie.managers.GuildManager;

import java.util.ArrayList;
import java.util.List;

public class GuildCommand implements CommandExecutor {

    private final GildiePlugin plugin;

    public GuildCommand(GildiePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Ta komenda jest tylko dla graczy!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("itemy")) {
            showItems(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("zaloz")) {
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Poprawne uzycie: /g zaloz <TAG> <NAZWA>");
                return true;
            }
            createGuild(player, args[1], args[2]);
            return true;
        }
        

        if (args[0].equalsIgnoreCase("info")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Poprawne uzycie: /g info <TAG>");
                return true;
            }
            showGuildInfo(player, args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("zapros")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Poprawne uzycie: /g zapros <NIKCJ>");
                return true;
            }
            invitePlayer(player, args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("dolacz")) {
            if (args.length < 2) {
                // Check if only one invite pending, otherwise require tag? For now, simplistic: one invite per player or require tag.
                // Simplified: /g dolacz <TAG> (or if user has 1 invite, auto-join? Let's stick to explicit TAG for safety or just TAG for now)
                 player.sendMessage(ChatColor.RED + "Poprawne uzycie: /g dolacz <TAG>");
                 return true;
            }
            joinGuild(player, args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("wyrzuc")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Poprawne uzycie: /g wyrzuc <NIKCJ>");
                return true;
            }
            kickPlayer(player, args[1]);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("opusc")) {
            leaveGuild(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("usun")) {
            deleteGuild(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("potwierdz")) {
            confirmDelete(player);
            return true;
        }

        sendHelp(player);
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "========== " + ChatColor.YELLOW + "GILDIE" + ChatColor.GOLD + " ==========");
        player.sendMessage(ChatColor.YELLOW + "/g " + ChatColor.GRAY + "- wyswietla pomoc");
        player.sendMessage(ChatColor.YELLOW + "/g itemy " + ChatColor.GRAY + "- pokazuje wymagane itemy");
        player.sendMessage(ChatColor.YELLOW + "/g zaloz <TAG> <NAZWA> " + ChatColor.GRAY + "- zakłada gildię");
        player.sendMessage(ChatColor.YELLOW + "/g info <TAG> " + ChatColor.GRAY + "- informacje o gildii");
        player.sendMessage(ChatColor.YELLOW + "/g zapros <NICK> " + ChatColor.GRAY + "- zaprasza gracza do gildii");
        player.sendMessage(ChatColor.YELLOW + "/g dolacz <TAG> " + ChatColor.GRAY + "- dolacza do gildii");
        player.sendMessage(ChatColor.YELLOW + "/g wyrzuc <NICK> " + ChatColor.GRAY + "- wyrzuca gracza z gildii");
        player.sendMessage(ChatColor.YELLOW + "/g opusc " + ChatColor.GRAY + "- opuszcza gildię");
        player.sendMessage(ChatColor.YELLOW + "/g usun " + ChatColor.GRAY + "- usuwa gildię (tylko lider)");
        player.sendMessage(ChatColor.YELLOW + "/g potwierdz " + ChatColor.GRAY + "- potwierdza usuniecie gildii");
    }

    private void showGuildInfo(Player player, String tag) {
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByTag(tag);

        if (guild == null) {
            player.sendMessage(ChatColor.RED + "Gildia o tagu " + tag + " nie istnieje!");
            return;
        }

        player.sendMessage(ChatColor.DARK_GRAY + "--------------------------------");
        player.sendMessage(ChatColor.GOLD + "Gildia: " + ChatColor.YELLOW + guild.getTag() + " - " + guild.getName());
        player.sendMessage(ChatColor.GOLD + "Lider: " + ChatColor.WHITE + org.bukkit.Bukkit.getOfflinePlayer(guild.getOwner()).getName());
        
        StringBuilder members = new StringBuilder();
        for (java.util.UUID memberId : guild.getMembers()) {
            members.append(org.bukkit.Bukkit.getOfflinePlayer(memberId).getName()).append(", ");
        }
        if (members.length() > 0) members.setLength(members.length() - 2);

        player.sendMessage(ChatColor.GOLD + "Członkowie: " + ChatColor.WHITE + members.toString());
        player.sendMessage(ChatColor.GOLD + "Liczba członków: " + ChatColor.WHITE + guild.getMembers().size());
        player.sendMessage(ChatColor.DARK_GRAY + "--------------------------------");
    }

    private void showItems(Player player) {
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "Wymagane Itemy");
        
        ItemStack diamonds = new ItemStack(Material.DIAMOND, 64);
        ItemStack emeralds = new ItemStack(Material.EMERALD, 64);
        
        // Slots: 0-8, 9-17, 18-26
        // Center of middle row (9-17) is 13.
        // Let's put them at 12 and 14 for symmetry around center.
        inv.setItem(12, diamonds);
        inv.setItem(14, emeralds);
        
        player.openInventory(inv);
    }

    private void createGuild(Player player, String tag, String name) {
        GuildManager gm = plugin.getGuildManager();
        
        if (gm.getGuildByOwner(player.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + "Posiadasz juz gildie!");
            return;
        }
        
        // Item check
        if (!player.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), 64) ||
            !player.getInventory().containsAtLeast(new ItemStack(Material.EMERALD), 64)) {
            player.sendMessage(ChatColor.RED + "Nie posiadasz wymaganych przedmiotow! Sprawdz /g itemy");
            return;
        }

        Location center = player.getLocation();
        if (gm.isRegionOverlapping(center)) {
            player.sendMessage(ChatColor.RED + "Jestes zbyt blisko innej gildii!");
            return;
        }

        // Take items
        player.getInventory().removeItem(new ItemStack(Material.DIAMOND, 64));
        player.getInventory().removeItem(new ItemStack(Material.EMERALD, 64));

        // Create Guild
        gm.createGuild(tag, name, player.getUniqueId(), center);
        
        // Spawn Heart and Room
        Location heartLoc = center.clone();
        heartLoc.setY(35);
        
        // Clear 5x5 area (radius 2)
        // Height: 5 up (36-40), 2 down (33-34), plus center (35).
        // Floor at Y-3 (32)
        
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                // Obsidian Floor
                heartLoc.clone().add(x, -3, z).getBlock().setType(Material.OBSIDIAN);
                
                // Air Room
                for (int y = -2; y <= 5; y++) {
                    // Don't remove the sponge location yet (will be set after)
                    // Actually we can set air everywhere then set sponge.
                    heartLoc.clone().add(x, y, z).getBlock().setType(Material.AIR);
                }
            }
        }
        
        heartLoc.getBlock().setType(Material.SPONGE);
        
        player.sendMessage(ChatColor.GREEN + "Gildia " + name + " [" + tag + "] zostala zalozona!");
        player.sendMessage(ChatColor.GREEN + "Serce gildii znajduje sie na Y: 35. Zostales przeteleportowany!");
        
        // Teleport player to center of the room (on top of sponge's neighbor... wait, sponge is at 35/0. Teleport to 36?)
        Location teleportLoc = heartLoc.clone().add(0.5, 1, 0.5);
        player.teleport(teleportLoc);
    }

    private void invitePlayer(Player player, String targetName) {
        Player target = org.bukkit.Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Gracz " + targetName + " nie jest online!");
            return;
        }

        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByOwner(player.getUniqueId());
        
        // Only owner can invite for now
        if (guild == null || !guild.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Nie jestes liderem zadnej gildii!");
            return;
        }
        
        if (gm.getGuildByMember(target.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + "Gracz posiada juz gildie!");
            return;
        }

        gm.invitePlayer(target.getUniqueId(), guild.getTag());
        player.sendMessage(ChatColor.GREEN + "Zaprosiles gracza " + target.getName() + " do gildii!");
        target.sendMessage(ChatColor.GREEN + "Zostales zaproszony do gildii " + guild.getTag() + " [" + guild.getName() + "]");
        target.sendMessage(ChatColor.GREEN + "Wpisz /g dolacz " + guild.getTag() + " aby dolaczyc!");
    }

    private void joinGuild(Player player, String tag) {
        GuildManager gm = plugin.getGuildManager();
        String pendingTag = gm.getPendingInvite(player.getUniqueId());
        
        if (pendingTag == null || !pendingTag.equalsIgnoreCase(tag)) {
            player.sendMessage(ChatColor.RED + "Nie posiadasz zaproszenia do tej gildii!");
            return;
        }
        
        GuildManager.Guild guild = gm.getGuildByTag(pendingTag);
        if (guild == null) {
            player.sendMessage(ChatColor.RED + "Ta gildia juz nie istnieje!");
            gm.removeInvite(player.getUniqueId());
            return;
        }
        
        if (gm.getGuildByMember(player.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + "Masz juz gildie!");
             gm.removeInvite(player.getUniqueId());
            return;
        }
        
        guild.addMember(player.getUniqueId());
        gm.removeInvite(player.getUniqueId());
        
        player.sendMessage(ChatColor.GREEN + "Dolaczyles do gildii " + guild.getTag() + "!");
        
        Player owner = org.bukkit.Bukkit.getPlayer(guild.getOwner());
        if (owner != null) {
            owner.sendMessage(ChatColor.GREEN + "Gracz " + player.getName() + " dolaczyl do gildii!");
        }
    }

    private void kickPlayer(Player player, String targetName) {
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByOwner(player.getUniqueId());
        
        if (guild == null) {
             player.sendMessage(ChatColor.RED + "Nie jestes liderem gildii!");
             return;
        }
        
        // Try to find UUID of target (even if offline)
        // For simplicity using Bukkit.getOfflinePlayer(name) - deprecated but easiest without UUID fetcher.
        // Or loop members.
        java.util.UUID targetUUID = null;
        String realName = targetName;
        
        for (java.util.UUID memberId : guild.getMembers()) {
             org.bukkit.OfflinePlayer op = org.bukkit.Bukkit.getOfflinePlayer(memberId);
             if (op.getName() != null && op.getName().equalsIgnoreCase(targetName)) {
                 targetUUID = memberId;
                 realName = op.getName();
                 break;
             }
        }
        
        if (targetUUID == null) {
            player.sendMessage(ChatColor.RED + "Gracz " + targetName + " nie jest w twojej gildii!");
            return;
        }
        
        if (targetUUID.equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Nie mozesz wyrzucic samego siebie! Uzyj /g usun (jesli chcesz usunac) lub opusc");
            return; // Or logic for disband?
        }
        
        guild.removeMember(targetUUID);
        player.sendMessage(ChatColor.GREEN + "Wyrzuciles gracza " + realName + " z gildii!");
        
        Player targetCallback = org.bukkit.Bukkit.getPlayer(targetUUID);
        if (targetCallback != null) {
            targetCallback.sendMessage(ChatColor.RED + "Zostales wyrzucony z gildii " + guild.getTag() + "!");
        }
    }
    
    private void leaveGuild(Player player) {
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByMember(player.getUniqueId());
        
        if (guild == null) {
            player.sendMessage(ChatColor.RED + "Nie posiadasz gildii!");
            return;
        }
        
        if (guild.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Jako lider nie mozesz opuscic gildii! Musisz ja rozwiazac (TODO) lub oddac lidera.");
            return;
        }
        
        guild.removeMember(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Opusciles gildie " + guild.getTag() + "!");
        
        Player owner = org.bukkit.Bukkit.getPlayer(guild.getOwner());
        if (owner != null) {
            owner.sendMessage(ChatColor.RED + "Gracz " + player.getName() + " opuscil gildie!");
        }
    }
    private final java.util.Map<java.util.UUID, Long> pendingDeletions = new java.util.HashMap<>();

    private void deleteGuild(Player player) {
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByMember(player.getUniqueId());
        
        if (guild == null) {
            player.sendMessage(ChatColor.RED + "Nie posiadasz gildii!");
            return;
        }
        
        if (!guild.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Tylko lider moze usunac gildie!");
            return;
        }
        
        pendingDeletions.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage(ChatColor.RED + "Czy na pewno chcesz usunac gildie? Wpisz " + ChatColor.YELLOW + "/g potwierdz" + ChatColor.RED + " aby potwierdzic.");
        player.sendMessage(ChatColor.GRAY + "Masz 30 sekund na potwierdzenie.");
    }

    private void confirmDelete(Player player) {
        if (!pendingDeletions.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Musisz najpierw wpisac /g usun aby rozpoczac procedure usuwania.");
            return;
        }
        
        long time = pendingDeletions.get(player.getUniqueId());
        if (System.currentTimeMillis() - time > 30000) {
            pendingDeletions.remove(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "Czas na potwierdzenie minal. Wpisz /g usun ponownie.");
            return;
        }
        
        pendingDeletions.remove(player.getUniqueId());
        
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByMember(player.getUniqueId());
        
        if (guild == null) {
             player.sendMessage(ChatColor.RED + "Blad! Nie posiadasz gildii (czyzbys ja opuscil w miedzyczasie?).");
             return;
        }
        
        if (!guild.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Nie jestes juz liderem!");
            return;
        }

        plugin.getLogger().info("Deleting guild: " + guild.getTag());
        gm.deleteGuild(guild);
        
        player.sendMessage(ChatColor.GREEN + "Gildia zostala usunieta!");
        org.bukkit.Bukkit.broadcastMessage(ChatColor.RED + "Gildia " + guild.getTag() + " zostala rozwiazana przez " + player.getName() + "!");
    }
}
