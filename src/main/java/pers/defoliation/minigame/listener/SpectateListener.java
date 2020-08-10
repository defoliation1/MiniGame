package pers.defoliation.minigame.listener;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import pers.defoliation.minigame.player.GamePlayer;

import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN;

public class SpectateListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        GamePlayer gamePlayer = GamePlayer.getGamePlayer(player);
        if (gamePlayer.isSpectator() && e.getCause() != PLUGIN)
            e.setCancelled(true);
    }

    @EventHandler
    public void interact(PlayerInteractEvent interactEvent) {
        Player player = (Player) interactEvent.getPlayer();
        GamePlayer gamePlayer = GamePlayer.getGamePlayer(player);
        if (!gamePlayer.isSpectator()) {
            return;
        }
        int itemInHandIndex = ((CraftInventoryPlayer) player.getInventory()).getInventory().itemInHandIndex;
        interactEvent.setCancelled(true);
        if ((interactEvent.getAction() == Action.RIGHT_CLICK_AIR || interactEvent.getAction() == Action.RIGHT_CLICK_BLOCK)
                && interactEvent.getItem() != null) {
            slotCommand(player, itemInHandIndex);
        }
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent placeEvent) {
        Player player = (Player) placeEvent.getPlayer();
        GamePlayer gamePlayer = GamePlayer.getGamePlayer(player);
        if (!gamePlayer.isSpectator()) {
            return;
        }
        placeEvent.setCancelled(true);
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent breakEvent) {
        Player player = (Player) breakEvent.getPlayer();
        GamePlayer gamePlayer = GamePlayer.getGamePlayer(player);
        if (!gamePlayer.isSpectator()) {
            return;
        }
        breakEvent.setCancelled(true);
    }
/*
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        GameMap gameMap = MatchManager.get().getSpectatorMap(player);
        if (gameMap == null) {
            return;
        }
        gameMap.getSpectators().remove(player.getUniqueId());
        gameMap.getAlivePlayers().remove(player);
        gameMap.getAllPlayers().remove(player);
        MatchManager.get().removeSpectator(player);
    }*/

    @EventHandler
    public void openInventory(InventoryOpenEvent openEvent) {
        GamePlayer gamePlayer = GamePlayer.getGamePlayer(openEvent.getPlayer().getName());
        if (!gamePlayer.isSpectator()) {
            return;
        }
        openEvent.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        GamePlayer gamePlayer = GamePlayer.getGamePlayer(player);
        if (!gamePlayer.isSpectator()) {
            return;
        }
        e.setCancelled(true);
    }

    private void slotCommand(Player player, int slot) {
    /*    List<String> strings = MatchManager.get().spectateCommands.get(slot);
        if (strings != null) {
            strings.stream().map(s -> s.replace("%player%", player.getName()))
                    .forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
        }*/
    }

/*    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = (Player) e.getPlayer();
        GameMap gameMap = MatchManager.get().getSpectatorMap(player);
        if (gameMap == null) {
            return;
        }
        if (player.getLocation().getY() < 0) {
            CoordLoc spectateSpawn = gameMap.getSpectateSpawn();
            player.teleport(new Location(gameMap.getCurrentWorld(), spectateSpawn.getX(), spectateSpawn.getY(), spectateSpawn.getZ()), END_PORTAL);
        }
    }*/

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = (Player) event.getPlayer();
        GamePlayer gamePlayer = GamePlayer.getGamePlayer(player);
        if (!gamePlayer.isSpectator()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = (Player) event.getPlayer();
        GamePlayer gamePlayer = GamePlayer.getGamePlayer(player);
        if (!gamePlayer.isSpectator()) {
            return;
        }
        event.setCancelled(true);
    }

}
