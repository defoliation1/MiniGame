package pers.defoliation.minigame.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import pers.defoliation.minigame.player.GamePlayer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 如果需要使用，则自己注册
 */
public class SpectateListener implements Listener {

    private HashMap<Class, Function<Event, Player>> event2PlayerMap = new HashMap<>();

    public boolean active = true;

    public SpectateListener() {
        registerEvent2Player(PlayerEvent.class, PlayerEvent::getPlayer);
        registerEvent2Player(BlockPlaceEvent.class, BlockPlaceEvent::getPlayer);
        registerEvent2Player(BlockBreakEvent.class, BlockBreakEvent::getPlayer);
        registerEvent2Player(InventoryOpenEvent.class, event -> (Player) event.getPlayer());
        registerEvent2Player(InventoryClickEvent.class, event -> (Player) event.getWhoClicked());
    }


    public <T extends Event> void registerEvent2Player(Class<T> tClass, Function<T, Player> playerFunction) {
        event2PlayerMap.put(tClass, (Function<Event, Player>) playerFunction);
    }

    public <T extends Event> SpectateListener addHandle(JavaPlugin plugin, Class<T> event, Consumer<T> consumer) {
        return addHandle(plugin, event, consumer, false);
    }

    public <T extends Event> SpectateListener addHandle(JavaPlugin plugin, Class<T> event, Consumer<T> consumer, boolean ignoreCancel) {
        getEventListeners(event).register(new RegisteredListener(new Listener() {
        }, (listener, event1) -> {
            if (active)
                if (!(event1 instanceof Cancellable) || !((Cancellable) event1).isCancelled() || !ignoreCancel) {
                    Function<Event, Player> function = getFunction(event1.getClass());
                    if (function == null)
                        return;
                    Player apply = function.apply(event1);
                    if (apply != null && apply.isOnline()) {
                        GamePlayer gamePlayer = GamePlayer.getGamePlayer(apply);
                        if (gamePlayer.isSpectator()) {
                            consumer.accept((T) event1);
                        }
                    }
                }
        }, EventPriority.NORMAL, plugin, ignoreCancel));
        return this;
    }

    private Function<Event, Player> getFunction(Class clazz) {
        if (clazz == null || clazz.equals(Object.class))
            return null;
        if (event2PlayerMap.containsKey(clazz))
            return event2PlayerMap.get(clazz);
        return getFunction(clazz.getSuperclass());
    }

    public static <T extends Event> Consumer<T> cancel() {
        return event -> {
            if (event instanceof Cancellable)
                ((Cancellable) event).setCancelled(true);
        };
    }

    private HandlerList getEventListeners(Class<? extends Event> type) {
        try {
            Method method = this.getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke((Object) null);
        } catch (Exception var3) {
            throw new IllegalPluginAccessException(var3.toString());
        }
    }

    private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException var2) {
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Event.class) && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return this.getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlerList method required!");
            }
        }
    }

}
