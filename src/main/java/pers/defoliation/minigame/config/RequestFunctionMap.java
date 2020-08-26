package pers.defoliation.minigame.config;

import pers.defoliation.minigame.conversation.request.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class RequestFunctionMap {

    private Map<Class, BiFunction<String, Consumer<?>, Request<?>>> requestMap = new HashMap<>();

    public <T> RequestFunctionMap putCompletionRequest(Class<T> tClass, BiFunction<String, Consumer<T>, Request<?>> function) {
        requestMap.put(tClass, (BiFunction<String, Consumer<?>, Request<?>>) (Object) function);
        return this;
    }

    public Map<Class, BiFunction<String, Consumer<?>, Request<?>>> getRequestMap() {
        return requestMap;
    }



}
