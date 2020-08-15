package pers.defoliation.minigame.conversation.request;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class RequestBlock extends RequestBase<Block> {

    public static RequestBlock newRequestBlock() {
        return new RequestBlock();
    }

    public static RequestBlock create() {
        return new RequestBlock();
    }

    private final Listener listener = new RequestListener();

    private Block result;

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
        PlayerInteractEvent.getHandlerList().unregister(listener);
    }

    @Override
    public void reset() {
        result = null;
    }

    @Override
    public Optional<Block> getResult() {
        if (!isCompleted())
            return Optional.empty();
        return Optional.of(result);
    }

    private class RequestListener implements Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInteract(PlayerInteractEvent event) {
            if (!event.getPlayer().equals(getConversation().getPlayer()))
                return;

            event.setCancelled(true);

            Block invalidate = event.getClickedBlock();

            if (!validate(invalidate))
                return;

            result = invalidate;
            setCompleted(true);
        }
    }
}
