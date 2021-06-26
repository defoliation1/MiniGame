package pers.defoliation.minigame.config;

import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pers.defoliation.minigame.MiniGame;
import pers.defoliation.minigame.conversation.Conversation;
import pers.defoliation.minigame.conversation.request.Request;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class GameConfigurationSection implements ConfigurationSection {

    private ConfigurationSection section;

    private HashMap<String, RequestWrapper> requestMap = new HashMap<>();

    public GameConfigurationSection(ConfigurationSection section) {
        this.section = section;
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public GameConfigurationSection request(String key, Function<Consumer<Object>, Request<?>> function) {
        requestMap.put(key, new RequestWrapper(function, notEmpty()));
        return this;
    }

    public GameConfigurationSection request(String key, Function<Consumer<Object>, Request<?>> function, Function<Object, Boolean> isComplete) {
        requestMap.put(key, new RequestWrapper(function, isComplete));
        return this;
    }

    private static Function<Object, Boolean> notEmpty() {
        return o -> o != null;
    }

    private class RequestWrapper {
        Function<Consumer<Object>, Request<?>> function;
        Function<Object, Boolean> isComplete;

        public RequestWrapper(Function<Consumer<Object>, Request<?>> function, Function<Object, Boolean> isComplete) {
            this.function = function;
            this.isComplete = isComplete;
        }
    }

    public void completeRequest(Player player) {
        completeRequest(player, getNeedCompleteRequest());
    }

    public void completeRequest(Player player, List<String> list) {
        Conversation conversation = new Conversation(MiniGame.INSTANCE);
        for (String s : list) {
            if (requestMap.containsKey(s)) {
                RequestWrapper requestWrapper = requestMap.get(s);
                if (!requestWrapper.isComplete.apply(get(s)))
                    conversation.addRequest(requestWrapper.function.apply(o -> set(s, o)));
            }
        }
        conversation.start(player);
    }

    public List<String> getNeedCompleteRequest() {
        List<String> list = new ArrayList<>();
        for (String s : requestMap.keySet()) {
            if (!requestMap.get(s).isComplete.apply(get(s)))
                list.add(s);
        }
        return list;
    }

    public boolean allRequestIsComplete() {
        for (String s : requestMap.keySet()) {
            if (!requestMap.get(s).isComplete.apply(get(s))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Set<String> getKeys(boolean b) {
        return section.getKeys(b);
    }

    @Override
    public Map<String, Object> getValues(boolean b) {
        return section.getValues(b);
    }

    @Override
    public boolean contains(String s) {
        return section.contains(s);
    }

    @Override
    public boolean contains(String s, boolean b) {
        return section.contains(s, b);
    }

    @Override
    public boolean isSet(String s) {
        return section.isSet(s);
    }

    @Override
    public String getCurrentPath() {
        return section.getCurrentPath();
    }

    @Override
    public String getName() {
        return section.getName();
    }

    @Override
    public Configuration getRoot() {
        return section.getRoot();
    }

    @Override
    public ConfigurationSection getParent() {
        return section.getParent();
    }

    @Override
    public Object get(String s) {
        return section.get(s);
    }

    @Override
    public Object get(String s, Object o) {
        return section.get(s, o);
    }

    @Override
    public void set(String s, Object o) {
        section.set(s, o);
    }

    @Override
    public ConfigurationSection createSection(String s) {
        return section.createSection(s);
    }

    @Override
    public ConfigurationSection createSection(String s, Map<?, ?> map) {
        return section.createSection(s, map);
    }

    @Override
    public String getString(String s) {
        return section.getString(s);
    }

    @Override
    public String getString(String s, String s1) {
        return section.getString(s, s1);
    }

    @Override
    public boolean isString(String s) {
        return section.isString(s);
    }

    @Override
    public int getInt(String s) {
        return section.getInt(s);
    }

    @Override
    public int getInt(String s, int i) {
        return section.getInt(s, i);
    }

    @Override
    public boolean isInt(String s) {
        return section.isInt(s);
    }

    @Override
    public boolean getBoolean(String s) {
        return section.getBoolean(s);
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        return section.getBoolean(s, b);
    }

    @Override
    public boolean isBoolean(String s) {
        return section.isBoolean(s);
    }

    @Override
    public double getDouble(String s) {
        return section.getDouble(s);
    }

    @Override
    public double getDouble(String s, double v) {
        return section.getDouble(s, v);
    }

    @Override
    public boolean isDouble(String s) {
        return section.isDouble(s);
    }

    @Override
    public long getLong(String s) {
        return section.getLong(s);
    }

    @Override
    public long getLong(String s, long l) {
        return section.getLong(s, l);
    }

    @Override
    public boolean isLong(String s) {
        return section.isLong(s);
    }

    @Override
    public List<?> getList(String s) {
        return section.getList(s);
    }

    @Override
    public List<?> getList(String s, List<?> list) {
        return section.getList(s, list);
    }

    @Override
    public boolean isList(String s) {
        return section.isList(s);
    }

    @Override
    public List<String> getStringList(String s) {
        return section.getStringList(s);
    }

    @Override
    public List<Integer> getIntegerList(String s) {
        return section.getIntegerList(s);
    }

    @Override
    public List<Boolean> getBooleanList(String s) {
        return section.getBooleanList(s);
    }

    @Override
    public List<Double> getDoubleList(String s) {
        return section.getDoubleList(s);
    }

    @Override
    public List<Float> getFloatList(String s) {
        return section.getFloatList(s);
    }

    @Override
    public List<Long> getLongList(String s) {
        return section.getLongList(s);
    }

    @Override
    public List<Byte> getByteList(String s) {
        return section.getByteList(s);
    }

    @Override
    public List<Character> getCharacterList(String s) {
        return section.getCharacterList(s);
    }

    @Override
    public List<Short> getShortList(String s) {
        return section.getShortList(s);
    }

    @Override
    public List<Map<?, ?>> getMapList(String s) {
        return section.getMapList(s);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(String s, Class<T> aClass) {
        return section.getSerializable(s, aClass);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(String s, Class<T> aClass, T t) {
        return section.getSerializable(s, aClass, t);
    }

    @Override
    public Vector getVector(String s) {
        return section.getVector(s);
    }

    @Override
    public Vector getVector(String s, Vector vector) {
        return section.getVector(s, vector);
    }

    @Override
    public boolean isVector(String s) {
        return section.isVector(s);
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String s) {
        return section.getOfflinePlayer(s);
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String s, OfflinePlayer offlinePlayer) {
        return section.getOfflinePlayer(s, offlinePlayer);
    }

    @Override
    public boolean isOfflinePlayer(String s) {
        return section.isOfflinePlayer(s);
    }

    @Override
    public ItemStack getItemStack(String s) {
        return section.getItemStack(s);
    }

    @Override
    public ItemStack getItemStack(String s, ItemStack itemStack) {
        return section.getItemStack(s, itemStack);
    }

    @Override
    public boolean isItemStack(String s) {
        return section.isItemStack(s);
    }

    @Override
    public Color getColor(String s) {
        return section.getColor(s);
    }

    @Override
    public Color getColor(String s, Color color) {
        return section.getColor(s, color);
    }

    @Override
    public boolean isColor(String s) {
        return section.isColor(s);
    }

    @Override
    public ConfigurationSection getConfigurationSection(String s) {
        return new GameConfigurationSection(section.getConfigurationSection(s));
    }

    @Override
    public boolean isConfigurationSection(String s) {
        return section.isConfigurationSection(s);
    }

    @Override
    public ConfigurationSection getDefaultSection() {
        return new GameConfigurationSection(section.getDefaultSection());
    }

    @Override
    public void addDefault(String s, Object o) {
        section.addDefault(s, o);
    }
}
