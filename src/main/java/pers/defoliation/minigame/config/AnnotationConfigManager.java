package pers.defoliation.minigame.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pers.defoliation.minigame.MiniGame;
import pers.defoliation.minigame.conversation.Conversation;
import pers.defoliation.minigame.conversation.request.Request;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class AnnotationConfigManager {

    private Map<Class, BiFunction<String, Consumer<?>, Request<?>>> requestMap = new HashMap<>();

    public <T> void putCompletionRequest(Class<T> tClass, BiFunction<String, Consumer<T>, Request<?>> function) {
        requestMap.put(tClass, (BiFunction<String, Consumer<?>, Request<?>>) (Object) function);
    }

    public void load(Object dataObject, ConfigurationSection section) {
        for (Field field : dataObject.getClass().getDeclaredFields()) {
            Config annotation = field.getAnnotation(Config.class);
            if (annotation != null) {
                String configName = annotation.value();
                if (configName.isEmpty()) {
                    configName = field.getName();
                }
                if (section.contains(annotation.value())) {
                    field.setAccessible(true);
                    Object o = section.get(configName);
                    try {
                        field.set(dataObject, o);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void save(Object dataObject, ConfigurationSection section) {
        for (Field field : dataObject.getClass().getDeclaredFields()) {
            Config annotation = field.getAnnotation(Config.class);
            if (annotation != null) {
                String configName = annotation.value();
                if (configName.isEmpty()) {
                    configName = field.getName();
                }
                try {
                    field.setAccessible(true);
                    section.set(configName, field.get(dataObject));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setDefault(Object dataObject, ConfigurationSection section) {
        for (Field field : dataObject.getClass().getDeclaredFields()) {
            Config annotation = field.getAnnotation(Config.class);
            if (annotation != null) {
                String configName = annotation.value();
                if (configName.isEmpty()) {
                    configName = field.getName();
                }
                try {
                    field.setAccessible(true);
                    Object o = field.get(dataObject);
                    if (o != null)
                        section.addDefault(configName, o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void complete(Player player, Object object, String... keys) {
        if (keys == null || keys.length == 0) {
            List<String> list = new ArrayList<>();
            for (Field declaredField : object.getClass().getDeclaredFields()) {
                Config annotation = declaredField.getAnnotation(Config.class);
                if (annotation != null) {
                    String value = annotation.value();
                    if (value.isEmpty()) {
                        String name = declaredField.getName();
                        value = name;
                    }
                    try {
                        declaredField.setAccessible(true);
                        if (declaredField.get(object) != null)
                            list.add(value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (list.isEmpty())
                return;
            complete(player, object, list.toArray(new String[0]));
        }

        Class<?> aClass = object.getClass();

        Conversation conversation = new Conversation(MiniGame.INSTANCE);

        for (String key : keys) {
            try {
                Field declaredField = aClass.getDeclaredField(key);
                declaredField.setAccessible(true);
                Class<?> declaringClass = declaredField.getDeclaringClass();
                if (!requestMap.containsKey(declaringClass))
                    throw new IllegalArgumentException(declaringClass + " 没有注册");
                Request<?> request = requestMap.get(declaringClass).apply(key, o -> {
                    try {
                        declaredField.set(object, o);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

                conversation.addRequest(request);

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        conversation.start(player);

    }

}
