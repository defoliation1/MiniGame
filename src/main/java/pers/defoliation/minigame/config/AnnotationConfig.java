package pers.defoliation.minigame.config;

import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Field;
import java.util.Arrays;

public class AnnotationConfig {

    public void load(Object dataObject, ConfigurationSection section) {
        for (Field field : dataObject.getClass().getFields()) {
            Config annotation = field.getAnnotation(Config.class);
            if (annotation != null) {
                if (section.contains(annotation.value())) {
                    Object o = section.get(annotation.value());
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
        for (Field field : dataObject.getClass().getFields()) {
            Config annotation = field.getAnnotation(Config.class);
            if (annotation != null) {
                try {
                    section.set(annotation.value(), field.get(dataObject));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setDefault(Object dataObject, ConfigurationSection section) {
        for (Field field : dataObject.getClass().getFields()) {
            Config annotation = field.getAnnotation(Config.class);
            if (annotation != null) {
                try {
                    section.addDefault(annotation.value(),field.get(dataObject));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
