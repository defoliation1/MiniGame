package pers.defoliation.minigame.config;

import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Field;

public class AnnotationConfig {

    public static void load(Object dataObject, ConfigurationSection section) {
        for (Field field : dataObject.getClass().getFields()) {
            Config annotation = field.getAnnotation(Config.class);
            if (annotation != null) {
                String configName = annotation.value();
                if(configName.isEmpty()){
                    configName = field.getName();
                }
                if (section.contains(annotation.value())) {
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
        for (Field field : dataObject.getClass().getFields()) {
            Config annotation = field.getAnnotation(Config.class);
            if (annotation != null) {
                String configName = annotation.value();
                if(configName.isEmpty()){
                    configName = field.getName();
                }
                try {
                    section.set(configName, field.get(dataObject));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void setDefault(Object dataObject, ConfigurationSection section) {
        for (Field field : dataObject.getClass().getFields()) {
            Config annotation = field.getAnnotation(Config.class);
            if (annotation != null) {
                String configName = annotation.value();
                if(configName.isEmpty()){
                    configName = field.getName();
                }
                try {
                    section.addDefault(configName, field.get(dataObject));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
