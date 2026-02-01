package pl.stonedrop.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class BlockBreakListener implements Listener {

    private final Random random = new Random();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getType() == Material.STONE) {
            // Give XP directly to player
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

            // Roll for Iron Ore (2%)
            if (random.nextDouble() * 100 <= 2.0) {
                ItemStack iron = new ItemStack(Material.IRON_ORE);
                java.util.Map<Integer, ItemStack> leftover = player.getInventory().addItem(iron);
                if (!leftover.isEmpty()) {
                    block.getWorld().dropItemNaturally(block.getLocation(), iron);
                }
                player.sendMessage("§7[§6Drop§7] §aTrafiles na Rude Zelaza! (2%) §7(Trafia do EQ)");
            }

            // Roll for Gold Ore (1%)
            if (random.nextDouble() * 100 <= 1.0) {
                ItemStack gold = new ItemStack(Material.GOLD_ORE);
                java.util.Map<Integer, ItemStack> leftover = player.getInventory().addItem(gold);
                if (!leftover.isEmpty()) {
                    block.getWorld().dropItemNaturally(block.getLocation(), gold);
                }
                player.sendMessage("§7[§6Drop§7] §eTrafiles na Rude Zlota! (1%) §7(Trafia do EQ)");
            }
        }
    }
}
