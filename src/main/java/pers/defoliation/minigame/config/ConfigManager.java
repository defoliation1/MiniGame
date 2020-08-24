package pers.defoliation.minigame.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pers.defoliation.minigame.MiniGame;
import pers.defoliation.minigame.conversation.Conversation;
import pers.defoliation.minigame.conversation.request.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigManager {

    private Map<Class, Supplier<Request>> requestMap = new HashMap<>();

    private List<ConfigRequest> configRequestList = new ArrayList<>();

    private ConfigurationSection configurationSection;

    public ConfigManager(ConfigurationSection configurationSection) {
        this.configurationSection = configurationSection;
    }

    public <T> ConfigRequest<T> request(Class<T> clazz, String key) {
        if (requestMap.containsKey(clazz))
            throw new IllegalArgumentException(clazz.getName() + " 未注册");
        if (configRequestList.stream().filter(configRequest -> configRequest.getKey().equals(key)).findAny().isPresent())
            throw new IllegalArgumentException(key + " 已经存在");
        return new ConfigRequest<>(clazz, key);
    }

    public void multiRequest() {

    }

    public void meetAllRequest(Player player) {
        Conversation conversation = new Conversation(MiniGame.INSTANCE);
        for (ConfigRequest configRequest : configRequestList) {
            if (!configRequest.check()) {
                conversation.addRequest(configRequest.getRequest());
            }
        }
        conversation.start(player);
    }

    public void executeRequest(Player player, String requestName) {
        configRequestList.stream().filter(configRequest -> configRequest.key.equals(requestName)).findAny().ifPresent(configRequest -> {
            Conversation conversation = new Conversation(MiniGame.INSTANCE);
            conversation.addRequest(configRequest.getRequest());
            conversation.start(player);
        });
    }

    public boolean allRequestIsMeet() {
        return configRequestList.stream().filter(configRequest -> !configRequest.check()).findAny().isPresent();
    }


    public class ConfigRequest<T> {

        private Class<T> requestClass;

        private String key;

        private Function<T, Boolean> checkFunction = t -> t != null;

        private T result;

        private Consumer<T> onMeetConsumer = t -> {
        };

        private String startMessage;

        private int timeOut = 120;

        private String onTimeOutMessage = "设置时间已过请重新设置";

        private ConfigRequest(Class<T> requestClass, String key) {
            this.requestClass = requestClass;
            this.key = key;
            if (configurationSection.contains(key)) {
                result = (T) configurationSection.get(key);
            }
        }

        public ConfigRequest<T> onMeet(Consumer<T> consumer) {
            onMeetConsumer = consumer;
            return this;
        }

        public ConfigRequest<T> setCheckFunction(Function<T, Boolean> function) {
            checkFunction = function;
            return this;
        }

        public ConfigRequest<T> setStartMessage(String startMessage) {
            this.startMessage = startMessage;
            return this;
        }

        public ConfigRequest<T> setTimeOut(int timeOut) {
            this.timeOut = timeOut;
            return this;
        }

        public ConfigRequest<T> setTimeOutMessage(String onTimeOutMessage) {
            this.onTimeOutMessage = onTimeOutMessage;
            return this;
        }

        public boolean check() {
            return checkFunction.apply(result);
        }

        public void setUpRequest() {
            configRequestList.add(this);
        }

        public Request getRequest() {
            Request<T> request = requestMap.get(requestClass).get()
                    .setOnComplete(o -> onMeetConsumer.accept(((Request<T>) o).getResult().get()))
                    .setTimeoutMessage(onTimeOutMessage)
                    .setTimeout(timeOut)
                    .setOnStart(request1 -> ((Request<T>) request1).getConversation().getPlayer().sendMessage(startMessage));
            return request;
        }

        public String getKey() {
            return key;
        }
    }

}
