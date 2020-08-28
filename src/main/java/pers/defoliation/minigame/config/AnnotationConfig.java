package pers.defoliation.minigame.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pers.defoliation.minigame.MiniGame;
import pers.defoliation.minigame.conversation.Conversation;
import pers.defoliation.minigame.conversation.request.Request;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class AnnotationConfig {

    public static void load(Object dataObject, ConfigurationSection section) {
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

    public static void save(Object dataObject, ConfigurationSection section) {
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

}
