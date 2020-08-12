package pers.defoliation.minigame.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pers.defoliation.minigame.player.GamePlayer;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 如果需要使用，则自己注册
 */
public class SpectateEventHandler extends MiniGameEventHandler {

    private HashMap<Class, Function<Event, Player>> event2PlayerMap = new HashMap<>();

    public SpectateEventHandler(JavaPlugin plugin) {
        super(plugin);
        registerEvent2Player(PlayerEvent.class, PlayerEvent::getPlayer);
        registerEvent2Player(BlockPlaceEvent.class, BlockPlaceEvent::getPlayer);
        registerEvent2Player(BlockBreakEvent.class, BlockBreakEvent::getPlayer);
        registerEvent2Player(InventoryOpenEvent.class, event -> (Player) event.getPlayer());
        registerEvent2Player(InventoryClickEvent.class, event -> (Player) event.getWhoClicked());
    }


    public <T extends Event> void registerEvent2Player(Class<T> tClass, Function<T, Player> playerFunction) {
        event2PlayerMap.put(tClass, (Function<Event, Player>) playerFunction);
    }

    private Function<Event, Player> getFunction(Class clazz) {
        if (clazz == null || clazz.equals(Object.class))
            return null;
        if (event2PlayerMap.containsKey(clazz))
            return event2PlayerMap.get(clazz);
        return getFunction(clazz.getSuperclass());
    }

    @Override
    protected <T extends Event> void acceptEvent(Event event, Consumer<T> consumer) {
        Function<Event, Player> function = getFunction(event.getClass());
        if (function == null)
            return;
        Player apply = function.apply(event);
        if (apply == null || !apply.isOnline())
            return;
        if (GamePlayer.getGamePlayer(apply).isSpectator())
            super.acceptEvent(event, consumer);
    }
}
