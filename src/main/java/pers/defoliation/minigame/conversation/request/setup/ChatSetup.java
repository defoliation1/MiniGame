package pers.defoliation.minigame.conversation.request.setup;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pers.defoliation.minigame.conversation.Conversation;
import pers.defoliation.minigame.conversation.request.Request;
import pers.defoliation.minigame.conversation.request.RequestBase;
import pers.defoliation.minigame.ui.RequestWithInfo;
import pers.defoliation.minigame.util.ChatUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ChatSetup extends RequestBase implements Setup {

    private List<RequestWithInfo> requestList = new ArrayList<>();

    private String title;
    private String bottom;

    private final Listener listener = new RequestListener();
    private Consumer<Conversation> quitConsumer = c -> c.cancel();

    public ChatSetup(String title, String bottom) {
        this.title = title;
        this.bottom = bottom;
    }

    public ChatSetup addRequest(RequestWithInfo... requestWithInfos) {
        for (RequestWithInfo requestWithInfo : requestWithInfos) {
            requestList.add(requestWithInfo);
        }
        return this;
    }

    public ChatSetup addRequest(Collection<RequestWithInfo> requestWithInfos) {
        requestList.addAll(requestWithInfos);
        return this;
    }

    @Override
    public void start() {
        super.start();
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

    private void sendChoiceMessage() {
        Player player = getConversation().getPlayer();
        ChatUtils.sendCleanScreen(player);
        StringBuilder titleLine = new StringBuilder("--------------------------------------");
        int i = titleLine.length() / 2 - title.length() - 2;
        for (; i < title.length(); i++) {
            titleLine.setCharAt(i, title.charAt(i));
        }
        TextComponent topLine = new TextComponent(titleLine.toString());
        TextComponent quit = new TextComponent("X");
        quit.setColor(ChatColor.RED);
        quit.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quit"));
        topLine.addExtra(quit);

        player.spigot().sendMessage(topLine);

        String left = "| ";
        String middle = " | ";
        String right = " |";

        if (!requestList.isEmpty()) {
            TextComponent line = new TextComponent(left);
            for (int j = 0; j < requestList.size(); j++) {
                RequestWithInfo requestWithInfo = requestList.get(j);
                TextComponent request = new TextComponent(requestWithInfo.getTitle());
                request.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + j));
                request.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, requestWithInfo.getDesc().stream().map(s -> new TextComponent(s)).collect(Collectors.toList()).toArray(new BaseComponent[0])));
                line.addExtra(request);
                if (line.toPlainText().length() + right.length() > titleLine.length() - 4) {
                    line.addExtra(right);
                    player.spigot().sendMessage(line);
                    line = new TextComponent(left);
                } else {
                    line.addExtra(middle);
                }
            }
        }

        if (bottom != null) {
            player.sendMessage(bottom);
        }
        TextComponent bottomLine = new TextComponent(titleLine + "-");
        player.spigot().sendMessage(bottomLine);
    }


    @Override
    public void reset() {

    }

    @Override
    public Optional getResult() {
        return Optional.empty();
    }

    @Override
    public void setOnQuit(Consumer<Conversation> quit) {
        quitConsumer = quit;
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

            if ("/quit".equals(event.getMessage())) {
                quitConsumer.accept(getConversation());
                setCompleted(true);
            } else {
                try {
                    int index = Integer.valueOf(event.getMessage().substring(1));
                    if (index >= requestList.size())
                        return;

                    RequestWithInfo requestWithInfo = requestList.get(index);
                    if (requestWithInfo.getRequest() instanceof Setup) {
                        ((Setup) requestWithInfo.getRequest()).setOnQuit(c -> c.insertRequest(ChatSetup.this));
                    } else {
                        Request request = requestWithInfo.getRequest();
                        request.setOnEnd(r -> request.getConversation().insertRequest(ChatSetup.this));
                        getConversation().insertRequest(request);
                    }
                    setCompleted(true);
                } catch (NumberFormatException e) {
                }
            }
        }
    }
}
