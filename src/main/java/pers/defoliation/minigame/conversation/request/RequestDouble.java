package pers.defoliation.minigame.conversation.request;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Optional;

public class RequestDouble extends RequestBase<Double> {

    public static RequestDouble newRequestDouble() {
        return new RequestDouble();
    }

    public static RequestDouble create() {
        return new RequestDouble();
    }

    private final Listener listener = new RequestListener();

    private Double result;

    @Override
    public void start() {
        super.start();
        Bukkit.getPluginManager().registerEvents(listener, getConversation().getPlugin());
        setStarted(true);
        sendPrompt();
    }

    @Override
    public void dispose() {
        super.dispose();
        AsyncPlayerChatEvent.getHandlerList().unregister(listener);
        PlayerCommandPreprocessEvent.getHandlerList().unregister(listener);
    }

    @Override
    public Optional<Double> getResult() {
        if (!isCompleted())
            return Optional.empty();
        return Optional.of(result);
    }

    @Override
    public void reset() {
        result = null;
    }

    private class RequestListener implements Listener {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onChat(AsyncPlayerChatEvent event) {
            if (!event.getPlayer().equals(getConversation().getPlayer()))
                return;

            event.setCancelled(true);

            try {
                Double invalidate = Double.valueOf(event.getMessage());

                if (!validate(invalidate))
                    return;

                result = invalidate;
                Bukkit.getScheduler().runTask(getConversation().getPlugin(), () -> setCompleted(true));
            } catch (NumberFormatException ignored) {
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onCommand(PlayerCommandPreprocessEvent event) {
            if (!event.getPlayer().equals(getConversation().getPlayer()))
                return;

            event.setCancelled(true);

            try {
                Double invalidate = Double.valueOf(event.getMessage());

                if (!validate(invalidate))
                    return;

                result = invalidate;
                setCompleted(true);
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
