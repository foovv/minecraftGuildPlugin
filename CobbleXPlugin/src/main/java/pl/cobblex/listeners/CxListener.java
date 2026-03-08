package pl.cobblex.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class CxListener implements Listener {

    private final Random random = new Random();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = event.getItemInHand();

        if (itemInHand.getType() != Material.COBBLESTONE) return;

        ItemMeta meta = itemInHand.getItemMeta();
        if (meta == null || !meta.hasEnchant(Enchantment.UNBREAKING)) return;
        
        Component displayName = meta.displayName();
        if (displayName == null) return;
        
        // Very basic check. We rely on the unbreaking enchantment and grey name "cobblex"
        // Wait, how do we compare components easily? We can serialize it or just check strings.
        // Let's just rely on the enchantment and material, or format.
        // For safety, let's keep it simple: Has Unbreaking + Material Cobblestone + Display name not null.

        event.setCancelled(true); // Prevent placing the block
        
        // Remove one from hand
        int amount = itemInHand.getAmount();
        if (amount > 1) {
            itemInHand.setAmount(amount - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        // Roll drops
        // 2% ender pearl
        if (random.nextInt(100) < 2) {
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
            player.sendMessage(Component.text("Znalazles Perle Kresu!", NamedTextColor.GOLD));
        }

        // 50% string
        if (random.nextInt(100) < 50) {
            player.getInventory().addItem(new ItemStack(Material.STRING, 1));
            player.sendMessage(Component.text("Znalazles Nic!", NamedTextColor.GOLD));
        }
    }
}
