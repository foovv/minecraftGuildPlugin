package pl.stonedrop.listeners;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import pl.stonedrop.managers.LevelManager;

import java.util.Random;

public class BlockBreakListener implements Listener {

    private final Random random = new Random();
    private final LevelManager levelManager;

    public BlockBreakListener(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getType() == Material.STONE) {
            // Give XP directly to player (Vanilla) - keeping this? Or replacing with custom XP?
            // User requested "level kopania" (mining level). Let's keep vanilla XP as is for now, or maybe remove it?
            // "kazdy wykopany kamien = 1 punkt"
            
            levelManager.addXp(player, 1);
            
            int xp = random.nextInt(3) + 1; // 1-3 XP
            player.giveExp(xp);
            
            // Give Cobblestone directly to player
            ItemStack cobble = new ItemStack(Material.COBBLESTONE);
            java.util.Map<Integer, ItemStack> leftoverCobble = player.getInventory().addItem(cobble);
            if (!leftoverCobble.isEmpty()) {
                block.getWorld().dropItemNaturally(block.getLocation(), cobble);
            }
            
            // Prevent natural cobblestone drop
            event.setDropItems(false);

            // Get Fortune level from player's tool
            int fortuneLevel = 0;
            ItemStack tool = player.getInventory().getItemInMainHand();
            if (tool != null && tool.hasItemMeta()) {
                fortuneLevel = tool.getEnchantmentLevel(Enchantment.FORTUNE);
            }

            // Roll for Iron Ore (2%)
            if (random.nextDouble() * 100 <= 2.0) {
                int amount = calculateDropAmount(fortuneLevel);
                ItemStack iron = new ItemStack(Material.IRON_ORE, amount);
                java.util.Map<Integer, ItemStack> leftover = player.getInventory().addItem(iron);
                if (!leftover.isEmpty()) {
                    for (ItemStack left : leftover.values()) {
                        block.getWorld().dropItemNaturally(block.getLocation(), left);
                    }
                }
                levelManager.addXp(player, 2);
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§7[§6Drop§7] §fRuda Zelaza §7x" + amount + " §8(§a+2 exp§8)"));
            }

            // Roll for Gold Ore (1%)
            if (random.nextDouble() * 100 <= 1.0) {
                int amount = calculateDropAmount(fortuneLevel);
                ItemStack gold = new ItemStack(Material.GOLD_ORE, amount);
                java.util.Map<Integer, ItemStack> leftover = player.getInventory().addItem(gold);
                if (!leftover.isEmpty()) {
                    for (ItemStack left : leftover.values()) {
                        block.getWorld().dropItemNaturally(block.getLocation(), left);
                    }
                }
                levelManager.addXp(player, 3);
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§7[§6Drop§7] §eRuda Zlota §7x" + amount + " §8(§a+3 exp§8)"));
            }
        }
    }


    /**
     * Calculate drop amount based on Fortune level.
     * Fortune 0: always 1
     * Fortune 1: 1-2
     * Fortune 2: 1-3
     * Fortune 3: 1-5 (max)
     */
    private int calculateDropAmount(int fortuneLevel) {
        if (fortuneLevel <= 0) {
            return 1;
        }
        int maxAmount = Math.min(1 + fortuneLevel + (fortuneLevel >= 3 ? 1 : 0), 5);
        return random.nextInt(maxAmount) + 1;
    }
}
