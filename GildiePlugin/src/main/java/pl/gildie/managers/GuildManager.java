package pl.gildie.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.UUID;

public class GuildManager {

    private final JavaPlugin plugin;
    private List<Guild> guilds;
    private final File guildsFile;
    private final Gson gson;

    public GuildManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.guilds = new ArrayList<>();
        this.guildsFile = new File(plugin.getDataFolder(), "guilds.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadGuilds();
    }


    public Guild getGuildAt(Location loc) {
        for (Guild guild : guilds) {
            if (guild.isInside(loc)) {
                return guild;
            }
        }
        return null;
    }
    
    public Guild getGuildByOwner(UUID owner) {
        for (Guild guild : guilds) {
            if (guild.getOwner().equals(owner)) {
                return guild;
            }
        }
        return null;
    }
    
    public boolean isRegionOverlapping(Location center) {
        int radius = 50; // 50x50 region means +/- 25 blocks from center
        // But the user said "50x50", usually means radius 25. Let's assume radius 25.
        // Or if it means 50 radius, then 100x100. Let's assume 50x50 area total, so radius 25.
        
        int checkRadius = 25; 

        for (Guild guild : guilds) {
            Location gLoc = guild.getCenter();
            if (!gLoc.getWorld().getName().equals(center.getWorld().getName())) continue;
            
            double distance = gLoc.distance(center);
            if (distance < (checkRadius * 2)) { // Simple distance check, can be more precise box check
                 return true;
            }
        }
        return false;
    }

    public void saveGuilds() {
        try (Writer writer = new FileWriter(guildsFile)) {
            gson.toJson(guilds, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGuilds() {
        if (!guildsFile.exists()) return;
        try (Reader reader = new FileReader(guildsFile)) {
            Type listType = new TypeToken<ArrayList<Guild>>(){}.getType();
            guilds = gson.fromJson(reader, listType);
            if (guilds == null) guilds = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Guild getGuildByTag(String tag) {
        for (Guild guild : guilds) {
            if (guild.getTag().equalsIgnoreCase(tag)) {
                return guild;
            }
        }
        return null;
    }

    public static class Guild {
        private String tag;
        private String name;
        private UUID owner;
        private List<UUID> members;
        private List<UUID> deputies;
        private double x, y, z;
        private String world;
        // Home location (defaults to heart location if not set)
        private Double homeX, homeY, homeZ;
        // Guild PvP toggle (friendly fire without damage/antilogout)
        private boolean pvpEnabled = false;
        // Permissions for members
        private Map<UUID, Set<GuildPermission>> playerPermissions;

        public Guild(String tag, String name, UUID owner, Location center) {
            this.tag = tag;
            this.name = name;
            this.owner = owner;
            this.members = new ArrayList<>();
            this.members.add(owner);
            this.deputies = new ArrayList<>();
            this.x = center.getX();
            this.y = center.getY();
            this.z = center.getZ();
            this.world = center.getWorld().getName();
            // Home defaults to heart (Y=36, one block above sponge)
            this.homeX = null;
            this.homeY = null;
            this.homeZ = null;
            this.playerPermissions = new HashMap<>();
        }
        
        public Location getHome() {
            World w = Bukkit.getWorld(world);
            if (homeX != null && homeY != null && homeZ != null) {
                return new Location(w, homeX, homeY, homeZ);
            }
            // Default: heart location (Y=36, above sponge at Y=35)
            return new Location(w, x, 36, z);
        }
        
        public void setHome(Location loc) {
            this.homeX = loc.getX();
            this.homeY = loc.getY();
            this.homeZ = loc.getZ();
        }
        
        public boolean isInside(Location loc) {
            if (!loc.getWorld().getName().equals(world)) return false;
            // 50x50 area, assuming center is in middle. +/- 25.
            double minX = x - 25;
            double maxX = x + 25;
            double minZ = z - 25;
            double maxZ = z + 25;
            
            return loc.getX() >= minX && loc.getX() <= maxX && loc.getZ() >= minZ && loc.getZ() <= maxZ;
        }
        
        // Returns 0.0 to 1.0 (1.0 at center, 0.0 at edge)
        public double getProgress(Location loc) {
             if (!isInside(loc)) return 0.0;
             double dist = Math.max(Math.abs(loc.getX() - x), Math.abs(loc.getZ() - z));
             // Max possible dist is 25.
             // If dist is 0, progress is 1.0.
             // If dist is 25, progress is 0.0.
             return 1.0 - (dist / 25.0);
        }

        public Location getCenter() {
            return new Location(Bukkit.getWorld(world), x, y, z);
        }

        public UUID getOwner() {
            return owner;
        }
        
        public List<UUID> getMembers() {
            if (members == null) members = new ArrayList<>();
            // Legacy fix: ensure owner is member
             if (!members.contains(owner)) {
                 members.add(owner);
             }
            return members;
        }

        public List<UUID> getDeputies() {
            if (deputies == null) deputies = new ArrayList<>();
            return deputies;
        }

        public boolean isDeputy(UUID uuid) {
            return getDeputies().contains(uuid);
        }

        public void addDeputy(UUID uuid) {
            if (!getDeputies().contains(uuid)) {
                getDeputies().add(uuid);
            }
        }

        public void removeDeputy(UUID uuid) {
            getDeputies().remove(uuid);
        }
        
        public String getTag() { return tag; }
        public String getName() { return name; }

        public boolean isPvpEnabled() { return pvpEnabled; }
        public void togglePvp() { this.pvpEnabled = !this.pvpEnabled; }

        public void addMember(UUID uuid) {
            getMembers().add(uuid);
        }

        public void removeMember(UUID uuid) {
            getMembers().remove(uuid);
            // Also remove from deputies if they were one
            removeDeputy(uuid);
        }

        public boolean isMember(UUID uuid) {
            return getMembers().contains(uuid);
        }

        public boolean hasPermission(UUID uuid, GuildPermission perm) {
            if (uuid.equals(owner)) return true;
            if (playerPermissions == null) playerPermissions = new HashMap<>();
            Set<GuildPermission> perms = playerPermissions.get(uuid);
            return perms != null && perms.contains(perm);
        }

        public void togglePermission(UUID uuid, GuildPermission perm) {
            if (playerPermissions == null) playerPermissions = new HashMap<>();
            Set<GuildPermission> perms = playerPermissions.computeIfAbsent(uuid, k -> new HashSet<>());
            if (perms.contains(perm)) {
                perms.remove(perm);
            } else {
                perms.add(perm);
            }
        }

        public int getRank(UUID uuid) {
            if (uuid.equals(owner)) return 3;
            if (isDeputy(uuid)) return 2;
            if (isMember(uuid)) return 1;
            return 0;
        }
    }

    // Map<TargetPlayerUUID, GuildTag>
    private final java.util.Map<UUID, String> pendingInvites = new java.util.HashMap<>();

    public void invitePlayer(UUID target, String info) {
        pendingInvites.put(target, info);
    }
    
    public String getPendingInvite(UUID target) {
        return pendingInvites.get(target);
    }
    
    public void removeInvite(UUID target) {
        pendingInvites.remove(target);
    }

    public Guild getGuildByMember(UUID uuid) {
        for (Guild guild : guilds) {
            if (guild.isMember(uuid)) {
                return guild;
            }
        }
        return null;
    }

    private WaypointManager waypointManager;

    public void setWaypointManager(WaypointManager waypointManager) {
        this.waypointManager = waypointManager;
    }

    public void createGuild(String tag, String name, UUID owner, Location center) {
        Guild guild = new Guild(tag, name, owner, center);
        guilds.add(guild);
        saveGuilds();
        
        if (this.waypointManager != null) {
            Player ownerPlayer = Bukkit.getPlayer(owner);
            if (ownerPlayer != null) {
                this.waypointManager.createWaypoint(ownerPlayer, guild);
            }
        }
    }

    public void deleteGuild(Guild guild) {
        // Remove Heart
        Location heartLoc = guild.getCenter().clone();
        heartLoc.setY(35);
        heartLoc.getBlock().setType(Material.AIR);
        
        if (this.waypointManager != null) {
            this.waypointManager.removeWaypointsForGuild(guild);
        }
        
        guilds.remove(guild);
        saveGuilds();
    }
    
    public List<Guild> getAllGuilds() {
        return new ArrayList<>(guilds);
    }
}
