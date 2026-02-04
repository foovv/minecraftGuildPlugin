package pl.gildie.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    private final Map<UUID, Long> combatTags = new HashMap<>();
    private static final long COMBAT_DURATION_MS = 30000; // 30 seconds

    public void tagPlayer(UUID uuid) {
        combatTags.put(uuid, System.currentTimeMillis() + COMBAT_DURATION_MS);
    }

    public boolean isTagged(UUID uuid) {
        Long expiry = combatTags.get(uuid);
        if (expiry == null) return false;
        if (System.currentTimeMillis() > expiry) {
            combatTags.remove(uuid);
            return false;
        }
        return true;
    }

    public int getRemainingSeconds(UUID uuid) {
        Long expiry = combatTags.get(uuid);
        if (expiry == null) return 0;
        long remaining = expiry - System.currentTimeMillis();
        if (remaining <= 0) {
            combatTags.remove(uuid);
            return 0;
        }
        return (int) Math.ceil(remaining / 1000.0);
    }

    public void removeTag(UUID uuid) {
        combatTags.remove(uuid);
    }

    public Map<UUID, Long> getCombatTags() {
        return combatTags;
    }
}
