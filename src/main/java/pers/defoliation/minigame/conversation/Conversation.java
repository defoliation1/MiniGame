package pers.defoliation.minigame.conversation;

import com.google.common.collect.Lists;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import pers.defoliation.minigame.conversation.request.Request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

import static pers.defoliation.minigame.conversation.ConversationState.*;

public class Conversation {

    public static Conversation newConversation(@Nonnull Plugin plugin) {
        return new Conversation(plugin);
    }

    private final Plugin plugin;

    private final List<Request<?>> requests = Lists.newLinkedList();

    private final Listener listener = new ConversationListener();

    private ConversationState state = UNSTARTED;

    private Player player;

    private Request<?> currentRequest;

    private Consumer<Conversation> onCancel;
    private Consumer<Conversation> onComplete;

    public Conversation(@Nonnull Plugin plugin) {
        Validate.notNull(plugin);
        this.plugin = plugin;
    }

    @Nonnull
    public final Plugin getPlugin() {
        return plugin;
    }

    @Nullable
    public final Player getPlayer() {
        return player;
    }

    /**
     * 添加请求
     */
    public Conversation addRequest(Request<?> request) {
        requests.add(request);
        return this;
    }

    public Conversation insertRequest(Request<?>... request) {
        for (int i = request.length - 1; i >= 0; i--) {
            requests.add(0, request[i]);
        }
        return this;
    }

    /**
     * 添加请求
     */
    public Conversation addRequest(Request<?>... requests) {
        for (Request<?> request : requests)
            addRequest(request);
        return this;
    }

    /**
     * 获取当前请求
     */
    @Nullable
    public Request<?> getCurrentRequest() {
        return currentRequest;
    }

    /**
     * 请求数量
     */
    public int size() {
        return requests.size();
    }

    /**
     * 请求队列是否为空
     */
    public boolean isEmpty() {
        return requests.isEmpty();
    }

    /**
     * 开始交流
     */
    public Conversation start(@Nonnull Player player) {
        if (isStarted())
            return this;

        if (isEmpty())
            return this;

        Validate.notNull(player);
        this.player = player;

        if (!player.isOnline())
            return this;

        if (isCompleted() || isCancelled())
            reset();

        state = STARTED;

        Bukkit.getPluginManager().registerEvents(listener, getPlugin());

        currentRequest = requests.remove(0);
        currentRequest.setConversation(this);
        currentRequest.start();
        return this;
    }

    /**
     * 取消交流
     */
    public void cancel() {
        if (isStopped())
            return;

        state = CANCELLED;

        if (currentRequest != null && !currentRequest.isCompleted())
            currentRequest.dispose();

        dispose();
        if (onCancel != null)
            onCancel.accept(this);
    }

    /**
     * 重置交流
     */
    public void reset() {
        cancel();

        state = UNSTARTED;

        for (Request<?> request : requests)
            request.reset();
    }

    /**
     * 注：内部方法，不建议直接调用<br>
     * inner method<br>
     */
    public void next() {
        if (isStopped())
            return;

        if (!isStarted())
            return;

        if (!currentRequest.isCompleted())
            return;

        if (requests.isEmpty()) {
            dispose();
            if (onComplete != null)
                onComplete.accept(this);
            return;
        }

        currentRequest = requests.remove(0);
        currentRequest.setConversation(this);
        currentRequest.start();
    }

    protected void dispose() {
        PlayerQuitEvent.getHandlerList().unregister(listener);
    }

    /**
     * 交流是否已开始
     */
    public boolean isStarted() {
        return state != UNSTARTED;
    }

    /**
     * 交流是否已取消
     */
    public boolean isCancelled() {
        return state == CANCELLED;
    }

    /**
     * 交流是否已完成
     */
    public boolean isCompleted() {
        return state == COMPLETED;
    }

    /**
     * 交流是否已停止
     */
    public boolean isStopped() {
        return isCompleted() || isCancelled();
    }

    /**
     * 获取交流取消监听器
     */
    @Nullable
    public Consumer<Conversation> getOnCancel() {
        return onCancel;
    }

    /**
     * 设置交流取消监听器
     */
    public Conversation setOnCancel(Consumer<Conversation> onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    /**
     * 获取交流完成监听器
     */
    @Nullable
    public Consumer<Conversation> getOnComplete() {
        return onComplete;
    }

    /**
     * 设置交流完成监听器
     */
    public Conversation setOnComplete(Consumer<Conversation> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    private class ConversationListener implements Listener {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onQuit(PlayerQuitEvent event) {
            if (!event.getPlayer().equals(getPlayer()))
                return;

            cancel();
        }
    }
}
