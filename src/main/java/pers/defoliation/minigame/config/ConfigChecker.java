package pers.defoliation.minigame.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class ConfigChecker {

    private HashMap<String, Function<Object, Boolean>> map = new HashMap<>();

    public ConfigChecker checkEmpty(String... key) {
        for (String s : key) {
            check(s, emptyFunction());
        }
        return this;
    }

    public ConfigChecker check(String key, Function<Object, Boolean> checkFunction) {
        map.put(key, checkFunction);
        return this;
    }

    public boolean allComplete(ConfigurationSection section) {
        for (String s : map.keySet()) {
            if (!map.get(s).apply(section.get(s))) {
                return false;
            }
        }
        return true;
    }

    public List<String> getUncompleted(ConfigurationSection section) {
        List<String> list = new ArrayList<>();
        for (String s : map.keySet()) {
            if (!map.get(s).apply(section.get(s))) {
                list.add(s);
            }
        }
        return list;
    }

    private static Function<Object, Boolean> emptyFunction() {
        return o -> o != null;
    }

}
