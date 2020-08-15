package pers.defoliation.minigame.conversation.request;

import com.google.common.collect.Sets;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pers.defoliation.minigame.util.ChatUtils;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class RequestChoice extends RequestBase<Integer> {

    public static RequestChoice newRequestChoice() {
        return new RequestChoice();
    }

    public static RequestChoice create() {
        return new RequestChoice();
    }

    private final Listener listener = new RequestListener();
    private final Set<String> items = Sets.newLinkedHashSet();

    private String[] bakedItems;
    private Integer result;

    public Set<String> getItems() {
        return items;
    }

    public RequestChoice addItem(@Nonnull String item) {
        Validate.notEmpty(item);
        getItems().add(item);
        return this;
    }

    public RequestChoice addItem(@Nonnull String... items) {
        Validate.noNullElements(items);
        for (String item : items)
            addItem(item);
        return this;
    }

    public RequestChoice addItem(@Nonnull Collection<String> items) {
        Validate.noNullElements(items);
        items.forEach(this::addItem);
        return this;
    }

    @Override
    public void start() {
        Validate.notEmpty(items);
        super.start();
        bakedItems = getItems().toArray(new String[getItems().size()]);
        Bukkit.getPluginManager().registerEvents(listener, getConversation().getPlugin());
        setStarted(true);
        sendChoiceMessage();
    }

    @Override
    public void dispose() {
        super.dispose();
        AsyncPlayerChatEvent.getHandlerList().unregister(listener);
        PlayerCommandPreprocessEvent.getHandlerList().unregister(listener);
    }

    @Override
    public void reset() {
        result = null;
    }

    @Override
    public Optional<Integer> getResult() {
        if (!isCompleted())
            return Optional.empty();
        return Optional.of(result);
    }

    private void sendChoiceMessage() {
        Player player = getConversation().getPlayer();
        ChatUtils.sendCleanScreen(player);
        ChatUtils.sendSplitter(player);
        ChatUtils.sendEmpty(player);
        sendPrompt();
        ChatUtils.sendEmpty(player);

        ComponentBuilder builder = new ComponentBuilder("");
        for (String item : bakedItems)
            builder.append("[" + item + "]").bold(true).event(new ClickEvent(Action.RUN_COMMAND, "/" + item)).append(" ");
        player.spigot().sendMessage(builder.create());

        ChatUtils.sendEmpty(player);
        ChatUtils.sendSplitter(player);
    }

    private int getIndex(String item) {
        return ArrayUtils.indexOf(bakedItems, item);
    }

    private class RequestListener implements Listener {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onChat(AsyncPlayerChatEvent event) {
            if (!event.getPlayer().equals(getConversation().getPlayer()))
                return;

            event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onCommand(PlayerCommandPreprocessEvent event) {
            if (!event.getPlayer().equals(getConversation().getPlayer()))
                return;

            event.setCancelled(true);

            int invalidate = getIndex(event.getMessage().substring(1));

            if (invalidate < 0)
                return;

            if (!validate(invalidate))
                return;

            result = invalidate;
            setCompleted(true);
        }
    }
}
