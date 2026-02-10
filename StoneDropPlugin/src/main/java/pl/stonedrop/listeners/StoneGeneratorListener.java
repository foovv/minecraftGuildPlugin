package pl.stonedrop.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import pl.stonedrop.managers.StoneGeneratorManager;

public class StoneGeneratorListener implements Listener {

    private final StoneGeneratorManager generatorManager;

    public StoneGeneratorListener(StoneGeneratorManager generatorManager) {
        this.generatorManager = generatorManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.STONE) return;

        Player player = event.getPlayer();
        
        boolean enabled = generatorManager.toggleGenerator(player.getUniqueId());
        
        if (enabled) {
            player.sendMessage(Component.text("Tryb stoniarki wlaczony! (Kazdy wykopany kamien sie odnowi)", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Tryb stoniarki wylaczony!", NamedTextColor.RED));
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.STONE) return;

        if (generatorManager.isGeneratorEnabled(event.getPlayer().getUniqueId())) {
            generatorManager.scheduleRegeneration(block.getLocation());
        }
    }
}
