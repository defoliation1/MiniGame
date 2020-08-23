package pers.defoliation.minigame.config;

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

    public <T> ConfigRequest<T> request(Class<T> clazz, String name) {
        if (requestMap.containsKey(clazz))
            throw new IllegalArgumentException(clazz.getName() + " 未注册");
        if (configRequestList.stream().filter(configRequest -> configRequest.getName().equals(name)).findAny().isPresent())
            throw new IllegalArgumentException(name + " 已经存在");
        return new ConfigRequest<>(clazz, name);
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
        configRequestList.stream().filter(configRequest -> configRequest.name.equals(requestName)).findAny().ifPresent(configRequest -> {
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

        private String name;

        private Function<T, Boolean> checkFunction = t -> t != null;

        private T result;

        private Consumer<T> onMeetConsumer = t -> {
        };

        private Consumer<Request<T>> requestBuild = t -> {
        };

        private ConfigRequest(Class<T> requestClass, String name) {
            this.requestClass = requestClass;
            this.name = name;
        }

        public ConfigRequest<T> onMeet(Consumer<T> consumer) {
            onMeetConsumer = consumer;
            return this;
        }

        public ConfigRequest<T> setDefaultResult(T result) {
            this.result = result;
            return this;
        }

        public ConfigRequest<T> setCheckFunction(Function<T, Boolean> function) {
            checkFunction = function;
            return this;
        }

        public ConfigRequest<T> setRequestBuild(Consumer<Request<T>> requestBuild) {
            this.requestBuild = requestBuild;
            return this;
        }

        public boolean check() {
            return checkFunction.apply(result);
        }

        public void setUpRequest() {
            configRequestList.add(this);
        }

        public Request getRequest() {
            Request request = requestMap.get(requestClass).get().setOnComplete(o -> onMeetConsumer.accept(((Request<T>) o).getResult().get()));
            requestBuild.accept(request);
            return request;
        }

        public String getName() {
            return name;
        }
    }

}
