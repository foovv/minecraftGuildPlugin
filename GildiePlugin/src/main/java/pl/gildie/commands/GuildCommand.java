package pl.gildie.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.gildie.GildiePlugin;
import pl.gildie.gui.GuildGuiManager;
import pl.gildie.managers.GuildManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuildCommand implements CommandExecutor {

    private final GildiePlugin plugin;
    private final Map<UUID, Integer> pendingTeleports = new HashMap<>();
    private final Map<UUID, Long> pendingDeletions = new HashMap<>();

    public GuildCommand(GildiePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.getLogger().info("Command /g executed by " + sender.getName());
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Ta komenda jest tylko dla graczy!", NamedTextColor.RED));
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
                player.sendMessage(Component.text("Poprawne uzycie: /g zaloz <TAG> <NAZWA>", NamedTextColor.RED));
                return true;
            }
            createGuild(player, args[1], args[2]);
            return true;
        }
        

        if (args[0].equalsIgnoreCase("info")) {
            if (args.length < 2) {
                player.sendMessage(Component.text("Poprawne uzycie: /g info <TAG>", NamedTextColor.RED));
                return true;
            }
            showGuildInfo(player, args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("zapros")) {
            if (args.length < 2) {
                player.sendMessage(Component.text("Poprawne uzycie: /g zapros <NIKCJ>", NamedTextColor.RED));
                return true;
            }
            invitePlayer(player, args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("dolacz")) {
            if (args.length < 2) {
                 player.sendMessage(Component.text("Poprawne uzycie: /g dolacz <TAG>", NamedTextColor.RED));
                 return true;
            }
            joinGuild(player, args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("wyrzuc")) {
            if (args.length < 2) {
                player.sendMessage(Component.text("Poprawne uzycie: /g wyrzuc <NIKCJ>", NamedTextColor.RED));
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

        if (args[0].equalsIgnoreCase("dom")) {
            teleportHome(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("ustawdom")) {
            setHome(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("zastepca")) {
            if (args.length < 2) {
                player.sendMessage(Component.text("Poprawne uzycie: /g zastepca <NICK>", NamedTextColor.RED));
                return true;
            }
            assignDeputy(player, args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("pvp")) {
            toggleGuildPvp(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("panel")) {
            openGuildPanel(player);
            return true;
        }

        sendHelp(player);
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("========== ", NamedTextColor.GOLD).append(Component.text("GILDIE", NamedTextColor.YELLOW)).append(Component.text(" ==========", NamedTextColor.GOLD)));
        player.sendMessage(Component.text("/g itemy ", NamedTextColor.YELLOW).append(Component.text("- pokazuje wymagane itemy", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/g zaloz <TAG> <NAZWA> ", NamedTextColor.YELLOW).append(Component.text("- zaklada gildie", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/g info <TAG> ", NamedTextColor.YELLOW).append(Component.text("- informacje o gildii", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/g zapros <NICK> ", NamedTextColor.YELLOW).append(Component.text("- zaprasza gracza do gildii", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/g dolacz <TAG> ", NamedTextColor.YELLOW).append(Component.text("- dolacza do gildii", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/g wyrzuc <NICK> ", NamedTextColor.YELLOW).append(Component.text("- wyrzuca gracza z gildii", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/g opusc ", NamedTextColor.YELLOW).append(Component.text("- opuszcza gildie", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/g usun ", NamedTextColor.YELLOW).append(Component.text("- usuwa gildie (tylko lider)", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/g potwierdz ", NamedTextColor.YELLOW).append(Component.text("- potwierdza usuniecie gildii", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/g dom ", NamedTextColor.YELLOW).append(Component.text("- teleportuje do domu gildii", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/g ustawdom ", NamedTextColor.YELLOW).append(Component.text("- ustawia dom gildii (lider)", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/g zastepca <NICK> ", NamedTextColor.YELLOW).append(Component.text("- nadaje/odbiera zastepce (lider)", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/g pvp ", NamedTextColor.YELLOW).append(Component.text("- wlacza/wylacza PvP w gildii (lider)", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/g panel ", NamedTextColor.YELLOW).append(Component.text("- otwiera panel zarzadzania gildia", NamedTextColor.GRAY)));
    }

    private void showGuildInfo(Player player, String tag) {
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByTag(tag);

        if (guild == null) {
            player.sendMessage(Component.text("Gildia o tagu " + tag + " nie istnieje!", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("--------------------------------", NamedTextColor.DARK_GRAY));
        player.sendMessage(Component.text("Gildia: ", NamedTextColor.GOLD).append(Component.text(guild.getTag() + " - " + guild.getName(), NamedTextColor.YELLOW)));
        player.sendMessage(Component.text("Lider: ", NamedTextColor.GOLD).append(Component.text(org.bukkit.Bukkit.getOfflinePlayer(guild.getOwner()).getName(), NamedTextColor.WHITE)));
        
        List<UUID> deputyIds = guild.getDeputies();
        if (!deputyIds.isEmpty()) {
            StringBuilder deputies = new StringBuilder();
            for (UUID deputyId : deputyIds) {
                deputies.append(org.bukkit.Bukkit.getOfflinePlayer(deputyId).getName()).append(", ");
            }
            if (deputies.length() > 0) deputies.setLength(deputies.length() - 2);
            player.sendMessage(Component.text("Zastepcy: ", NamedTextColor.GOLD).append(Component.text(deputies.toString(), NamedTextColor.WHITE)));
        }

        StringBuilder members = new StringBuilder();
        for (java.util.UUID memberId : guild.getMembers()) {
            members.append(org.bukkit.Bukkit.getOfflinePlayer(memberId).getName()).append(", ");
        }
        if (members.length() > 0) members.setLength(members.length() - 2);

        player.sendMessage(Component.text("Czlonkowie: ", NamedTextColor.GOLD).append(Component.text(members.toString(), NamedTextColor.WHITE)));
        player.sendMessage(Component.text("Liczba czlonkow: ", NamedTextColor.GOLD).append(Component.text(guild.getMembers().size() + "/10", NamedTextColor.WHITE)));
        player.sendMessage(Component.text("--------------------------------", NamedTextColor.DARK_GRAY));
    }

    private void showItems(Player player) {
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, 27, Component.text("Wymagane Itemy", NamedTextColor.DARK_PURPLE));
        
        ItemStack diamonds = new ItemStack(Material.DIAMOND, 64);
        ItemStack emeralds = new ItemStack(Material.EMERALD, 64);
        
        inv.setItem(12, diamonds);
        inv.setItem(14, emeralds);
        
        player.openInventory(inv);
    }

    private void createGuild(Player player, String tag, String name) {
        GuildManager gm = plugin.getGuildManager();
        
        if (gm.getGuildByOwner(player.getUniqueId()) != null) {
            player.sendMessage(Component.text("Posiadasz juz gildie!", NamedTextColor.RED));
            return;
        }
        
        // Item check
        if (!player.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), 64) ||
            !player.getInventory().containsAtLeast(new ItemStack(Material.EMERALD), 64)) {
            player.sendMessage(Component.text("Nie posiadasz wymaganych przedmiotow! Sprawdz /g itemy", NamedTextColor.RED));
            return;
        }

        Location center = player.getLocation();
        if (gm.isRegionOverlapping(center)) {
            player.sendMessage(Component.text("Jestes zbyt blisko innej gildii!", NamedTextColor.RED));
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
        
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                heartLoc.clone().add(x, -3, z).getBlock().setType(Material.OBSIDIAN);
                
                for (int y = -2; y <= 5; y++) {
                    heartLoc.clone().add(x, y, z).getBlock().setType(Material.AIR);
                }
            }
        }
        
        heartLoc.getBlock().setType(Material.SPONGE);
        
        player.sendMessage(Component.text("Gildia " + name + " [" + tag + "] zostala zalozona!", NamedTextColor.GREEN));
        player.sendMessage(Component.text("Serce gildii znajduje sie na Y: 35. Zostales przeteleportowany!", NamedTextColor.GREEN));
        
        Location teleportLoc = heartLoc.clone().add(0.5, 1, 0.5);
        player.teleport(teleportLoc);
    }

    private void invitePlayer(Player player, String targetName) {
        Player target = org.bukkit.Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(Component.text("Gracz " + targetName + " nie jest online!", NamedTextColor.RED));
            return;
        }

        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByOwner(player.getUniqueId());
        
        if (guild == null || !guild.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Nie jestes liderem zadnej gildii!", NamedTextColor.RED));
            return;
        }

        if (guild.getMembers().size() >= 10) {
            player.sendMessage(Component.text("Gildia jest pelna! (Limit: 10 czlonkow)", NamedTextColor.RED));
            return;
        }
        
        if (gm.getGuildByMember(target.getUniqueId()) != null) {
            player.sendMessage(Component.text("Gracz posiada juz gildie!", NamedTextColor.RED));
            return;
        }

        gm.invitePlayer(target.getUniqueId(), guild.getTag());
        player.sendMessage(Component.text("Zaprosiles gracza " + target.getName() + " do gildii!", NamedTextColor.GREEN));
        target.sendMessage(Component.text("Zostales zaproszony do gildii " + guild.getTag() + " [" + guild.getName() + "]", NamedTextColor.GREEN));
        target.sendMessage(Component.text("Wpisz /g dolacz " + guild.getTag() + " aby dolaczyc!", NamedTextColor.GREEN));
    }

    private void joinGuild(Player player, String tag) {
        GuildManager gm = plugin.getGuildManager();
        String pendingTag = gm.getPendingInvite(player.getUniqueId());
        
        if (pendingTag == null || !pendingTag.equalsIgnoreCase(tag)) {
            player.sendMessage(Component.text("Nie posiadasz zaproszenia do tej gildii!", NamedTextColor.RED));
            return;
        }
        
        GuildManager.Guild guild = gm.getGuildByTag(pendingTag);
        if (guild == null) {
            player.sendMessage(Component.text("Ta gildia juz nie istnieje!", NamedTextColor.RED));
            gm.removeInvite(player.getUniqueId());
            return;
        }
        
        if (gm.getGuildByMember(player.getUniqueId()) != null) {
            player.sendMessage(Component.text("Masz juz gildie!", NamedTextColor.RED));
             gm.removeInvite(player.getUniqueId());
            return;
        }

        if (guild.getMembers().size() >= 10) {
            player.sendMessage(Component.text("Gildia jest pelna! Nie mozesz do niej dolaczyc.", NamedTextColor.RED));
            gm.removeInvite(player.getUniqueId());
            return;
        }
        
        guild.addMember(player.getUniqueId());
        gm.removeInvite(player.getUniqueId());
        
        player.sendMessage(Component.text("Dolaczyles do gildii " + guild.getTag() + "!", NamedTextColor.GREEN));
        
        Player owner = org.bukkit.Bukkit.getPlayer(guild.getOwner());
        if (owner != null) {
            owner.sendMessage(Component.text("Gracz " + player.getName() + " dolaczyl do gildii!", NamedTextColor.GREEN));
        }
    }

    private void kickPlayer(Player player, String targetName) {
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByOwner(player.getUniqueId());
        
        if (guild == null) {
             player.sendMessage(Component.text("Nie jestes liderem gildii!", NamedTextColor.RED));
             return;
        }
        
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
            player.sendMessage(Component.text("Gracz " + targetName + " nie jest w twojej gildii!", NamedTextColor.RED));
            return;
        }
        
        if (targetUUID.equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Nie mozesz wyrzucic samego siebie! Uzyj /g usun (jesli chcesz usunac) lub opusc", NamedTextColor.RED));
            return;
        }
        
        guild.removeMember(targetUUID);
        player.sendMessage(Component.text("Wyrzuciles gracza " + realName + " z gildii!", NamedTextColor.GREEN));
        
        Player targetCallback = org.bukkit.Bukkit.getPlayer(targetUUID);
        if (targetCallback != null) {
            targetCallback.sendMessage(Component.text("Zostales wyrzucony z gildii " + guild.getTag() + "!", NamedTextColor.RED));
        }
    }
    
    private void leaveGuild(Player player) {
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByMember(player.getUniqueId());
        
        if (guild == null) {
            player.sendMessage(Component.text("Nie posiadasz gildii!", NamedTextColor.RED));
            return;
        }
        
        if (guild.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Jako lider nie mozesz opuscic gildii! Musisz ja rozwiazac (TODO) lub oddac lidera.", NamedTextColor.RED));
            return;
        }
        
        guild.removeMember(player.getUniqueId());
        player.sendMessage(Component.text("Opusciles gildie " + guild.getTag() + "!", NamedTextColor.GREEN));
        
        Player owner = org.bukkit.Bukkit.getPlayer(guild.getOwner());
        if (owner != null) {
            owner.sendMessage(Component.text("Gracz " + player.getName() + " opuscil gildie!", NamedTextColor.RED));
        }
    }

    private void deleteGuild(Player player) {
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByMember(player.getUniqueId());
        
        if (guild == null) {
            player.sendMessage(Component.text("Nie posiadasz gildii!", NamedTextColor.RED));
            return;
        }
        
        if (!guild.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Tylko lider moze usunac gildie!", NamedTextColor.RED));
            return;
        }
        
        pendingDeletions.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage(Component.text("Czy na pewno chcesz usunac gildie? Wpisz ", NamedTextColor.RED).append(Component.text("/g potwierdz", NamedTextColor.YELLOW)).append(Component.text(" aby potwierdzic.", NamedTextColor.RED)));
        player.sendMessage(Component.text("Masz 30 sekund na potwierdzenie.", NamedTextColor.GRAY));
    }

    private void confirmDelete(Player player) {
        if (!pendingDeletions.containsKey(player.getUniqueId())) {
            player.sendMessage(Component.text("Musisz najpierw wpisac /g usun aby rozpoczac procedure usuwania.", NamedTextColor.RED));
            return;
        }
        
        long time = pendingDeletions.get(player.getUniqueId());
        if (System.currentTimeMillis() - time > 30000) {
            pendingDeletions.remove(player.getUniqueId());
            player.sendMessage(Component.text("Czas na potwierdzenie minal. Wpisz /g usun ponownie.", NamedTextColor.RED));
            return;
        }
        
        pendingDeletions.remove(player.getUniqueId());
        
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByMember(player.getUniqueId());
        
        if (guild == null) {
             player.sendMessage(Component.text("Blad! Nie posiadasz gildii (czyzbys ja opuscil w miedzyczasie?).", NamedTextColor.RED));
             return;
        }
        
        if (!guild.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Nie jestes juz liderem!", NamedTextColor.RED));
            return;
        }

        plugin.getLogger().info("Deleting guild: " + guild.getTag());
        gm.deleteGuild(guild);
        
        player.sendMessage(Component.text("Gildia zostala usunieta!", NamedTextColor.GREEN));
        org.bukkit.Bukkit.broadcast(Component.text("Gildia " + guild.getTag() + " zostala rozwiazana przez " + player.getName() + "!", NamedTextColor.RED));
    }

    private void teleportHome(Player player) {
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild playerGuild = gm.getGuildByMember(player.getUniqueId());
        
        if (playerGuild == null) {
            player.sendMessage(Component.text("Nie posiadasz gildii!", NamedTextColor.RED));
            return;
        }
        
        if (pendingTeleports.containsKey(player.getUniqueId())) {
            org.bukkit.Bukkit.getScheduler().cancelTask(pendingTeleports.get(player.getUniqueId()));
            pendingTeleports.remove(player.getUniqueId());
        }
        
        GuildManager.Guild guildAtLocation = gm.getGuildAt(player.getLocation());
        boolean onEnemyTerritory = guildAtLocation != null && !guildAtLocation.isMember(player.getUniqueId());
        
        int delay = onEnemyTerritory ? 30 : 10;
        
        if (onEnemyTerritory) {
            player.sendMessage(Component.text("Jestes na terenie wrogiej gildii! Teleportacja za " + delay + " sekund...", NamedTextColor.RED));
        } else {
            player.sendMessage(Component.text("Teleportacja do domu gildii za " + delay + " sekund...", NamedTextColor.GREEN));
        }
        player.sendMessage(Component.text("Nie ruszaj sie!", NamedTextColor.GRAY));
        
        Location startLoc = player.getLocation().clone();
        Location targetLoc = playerGuild.getHome();
        
        int taskId = org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            pendingTeleports.remove(player.getUniqueId());
            
            if (player.getLocation().distanceSquared(startLoc) > 1) {
                player.sendMessage(Component.text("Teleportacja anulowana - ruszyles sie!", NamedTextColor.RED));
                return;
            }
            
            player.teleport(targetLoc);
            player.sendMessage(Component.text("Zostales przeteleportowany do domu gildii!", NamedTextColor.GREEN));
        }, delay * 20L);
        
        pendingTeleports.put(player.getUniqueId(), taskId);
    }

    private void setHome(Player player) {
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByMember(player.getUniqueId());
        
        if (guild == null) {
            player.sendMessage(Component.text("Nie posiadasz gildii!", NamedTextColor.RED));
            return;
        }
        
        if (!guild.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Tylko lider moze ustawic dom gildii!", NamedTextColor.RED));
            return;
        }
        
        if (!guild.isInside(player.getLocation())) {
            player.sendMessage(Component.text("Musisz byc na terenie swojej gildii!", NamedTextColor.RED));
            return;
        }
        
        guild.setHome(player.getLocation());
        gm.saveGuilds();
        player.sendMessage(Component.text("Dom gildii zostal ustawiony na twoja aktualna lokalizacje!", NamedTextColor.GREEN));
    }

    private void assignDeputy(Player player, String targetName) {
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByOwner(player.getUniqueId());

        if (guild == null) {
            player.sendMessage(Component.text("Nie jestes liderem gildii!", NamedTextColor.RED));
            return;
        }

        UUID targetUUID = null;
        String realName = targetName;
        for (UUID memberId : guild.getMembers()) {
            org.bukkit.OfflinePlayer op = org.bukkit.Bukkit.getOfflinePlayer(memberId);
            if (op.getName() != null && op.getName().equalsIgnoreCase(targetName)) {
                targetUUID = memberId;
                realName = op.getName();
                break;
            }
        }

        if (targetUUID == null) {
            player.sendMessage(Component.text("Ten gracz nie jest czlonkiem Twojej gildii!", NamedTextColor.RED));
            return;
        }

        if (targetUUID.equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Nie mozesz nadac zastepcy samemu sobie!", NamedTextColor.RED));
            return;
        }

        if (guild.isDeputy(targetUUID)) {
            guild.removeDeputy(targetUUID);
            gm.saveGuilds();
            player.sendMessage(Component.text("Odebrales range zastepcy graczowi " + realName + ".", NamedTextColor.GREEN));
            Player targetPlayer = org.bukkit.Bukkit.getPlayer(targetUUID);
            if (targetPlayer != null) {
                targetPlayer.sendMessage(Component.text("Twoja ranga zastepcy w gildii " + guild.getTag() + " zostala odebrana.", NamedTextColor.RED));
            }
        } else {
            if (guild.getDeputies().size() >= 2) {
                player.sendMessage(Component.text("Twoja gildia moze miec maksymalnie 2 zastepcow!", NamedTextColor.RED));
                return;
            }
            guild.addDeputy(targetUUID);
            gm.saveGuilds();
            player.sendMessage(Component.text("Nadales range zastepcy graczowi " + realName + ".", NamedTextColor.GREEN));
            Player targetPlayer = org.bukkit.Bukkit.getPlayer(targetUUID);
            if (targetPlayer != null) {
                targetPlayer.sendMessage(Component.text("Zostales mianowany zastepca w gildii " + guild.getTag() + "!", NamedTextColor.GREEN));
            }
        }
    }

    private void toggleGuildPvp(Player player) {
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByMember(player.getUniqueId());

        if (guild == null) {
            player.sendMessage(Component.text("Nie posiadasz gildii!", NamedTextColor.RED));
            return;
        }

        if (!guild.getOwner().equals(player.getUniqueId()) && !guild.isDeputy(player.getUniqueId())) {
            player.sendMessage(Component.text("Nie jestes liderem ani zastepca gildii!", NamedTextColor.RED));
            return;
        }

        guild.togglePvp();
        gm.saveGuilds();

        Component status = guild.isPvpEnabled() ? Component.text("wlaczony", NamedTextColor.GREEN).append(Component.text(" (bez dmg/logouta)")) : Component.text("wylaczony", NamedTextColor.RED);
        player.sendMessage(Component.text("PvP w gildii zostalo: ", NamedTextColor.GOLD).append(status));
        
        for (UUID memberId : guild.getMembers()) {
            Player p = org.bukkit.Bukkit.getPlayer(memberId);
            if (p != null && !p.equals(player)) {
                p.sendMessage(Component.text("PvP w gildii zostalo: ", NamedTextColor.GOLD).append(status));
            }
        }
    }

    private void openGuildPanel(Player player) {
        GuildManager gm = plugin.getGuildManager();
        GuildManager.Guild guild = gm.getGuildByMember(player.getUniqueId());

        if (guild == null) {
            player.sendMessage(Component.text("Nie posiadasz gildii!", NamedTextColor.RED));
            return;
        }

        if (!guild.getOwner().equals(player.getUniqueId()) && !guild.isDeputy(player.getUniqueId())) {
            player.sendMessage(Component.text("Nie jestes liderem ani zastepca gildii!", NamedTextColor.RED));
            return;
        }

        GuildGuiManager.openMainPanel(player, guild);
    }
}
