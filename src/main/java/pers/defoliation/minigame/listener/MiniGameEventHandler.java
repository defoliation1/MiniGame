package pers.defoliation.minigame.listener;

import org.bukkit.event.*;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;

public class MiniGameEventHandler {

    private JavaPlugin plugin;

    public MiniGameEventHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public <T extends Event> MiniGameEventHandler addHandle(Class<T> event, Consumer<T> consumer) {
        return addHandle(event, consumer, ignoreCancel());
    }

    public <T extends Event> MiniGameEventHandler addHandle(Class<T> event, Consumer<T> consumer, Function<T, Boolean> ignore) {
        getEventListeners(event).register(new RegisteredListener(new Listener() {
        }, (listener, event1) -> {
            if (!ignore.apply((T) event1)) {
                acceptEvent(event1, consumer);
            }
        }, EventPriority.NORMAL, plugin, false));
        return this;
    }

    protected <T extends Event> void acceptEvent(Event event, Consumer<T> consumer){
        consumer.accept((T) event);
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
