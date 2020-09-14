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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class MiniGameEventHandler {

    private JavaPlugin plugin;
    private static HashMap<Class, Function<Event, Player>> event2PlayerMap = new HashMap<>();

    private boolean enable = true;

    static {
        registerEvent2Player(PlayerEvent.class, PlayerEvent::getPlayer);
        registerEvent2Player(BlockPlaceEvent.class, BlockPlaceEvent::getPlayer);
        registerEvent2Player(BlockBreakEvent.class, BlockBreakEvent::getPlayer);
        registerEvent2Player(InventoryOpenEvent.class, event -> (Player) event.getPlayer());
        registerEvent2Player(InventoryClickEvent.class, event -> (Player) event.getWhoClicked());
    }

    public MiniGameEventHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static <T extends Event> void registerEvent2Player(Class<T> tClass, Function<T, Player> playerFunction) {
        event2PlayerMap.put(tClass, (Function<Event, Player>) playerFunction);
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public static Player getPlayerByEvent(Event event) {
        Function<Event, Player> function = getFunction(event.getClass());
        if (function == null)
            return null;
        return function.apply(event);
    }

    private static Function<Event, Player> getFunction(Class clazz) {
        if (clazz == null || clazz.equals(Object.class))
            return null;
        if (event2PlayerMap.containsKey(clazz))
            return event2PlayerMap.get(clazz);
        return getFunction(clazz.getSuperclass());
    }


    public <T extends Event> MiniGameEventHandler addHandle(Class<T> event, Consumer<T> consumer) {
        return addHandle(event, consumer, ignoreCancel());
    }

    public <T extends Event> MiniGameEventHandler addHandle(Class<T> event, Consumer<T> consumer, Function<T, Boolean> ignore) {
        getEventListeners(event).register(new RegisteredListener(new Listener() {
        }, (listener, event1) -> {
            if (enable && !ignore.apply((T) event1)) {
                acceptEvent(event1, consumer);
            }
        }, EventPriority.NORMAL, plugin, false));
        return this;
    }

    protected <T extends Event> void acceptEvent(Event event, Consumer<T> consumer) {
        consumer.accept((T) event);
    }

    protected <T extends Event> void acceptEvent(Event event, Player player, BiConsumer<Player, T> consumer) {
        consumer.accept(player, (T) event);
    }

    public static <T extends Event> Function<T, Boolean> ignoreCancel() {
        return event -> event instanceof Cancellable ? ((Cancellable) event).isCancelled() : false;
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
