package pers.defoliation.minigame.worldobject;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pers.defoliation.minigame.conversation.request.setup.ChatSetup;
import pers.defoliation.minigame.conversation.request.setup.RequestWithInfoSupplier;
import pers.defoliation.minigame.ui.RequestWithInfo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class WorldObjectManager implements RequestWithInfoSupplier {

    private World world;
    private File configFile;
    private HashMap<String, WorldObject> worldObjectHashMap = new HashMap<>();

    public WorldObjectManager(JavaPlugin plugin, World world) {
        this.world = world;
        configFile = new File(plugin.getDataFolder(), world.getName());
        if (configFile.exists()) {
            for (File file : configFile.listFiles()) {
                YamlConfiguration yamlConfiguration = new YamlConfiguration();
                try {
                    yamlConfiguration.load(file);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                String className = yamlConfiguration.getString("==");
                try {
                    WorldObject object = (WorldObject) Class.forName(className).newInstance();
                    object.deserialize(yamlConfiguration);
                    worldObjectHashMap.put(object.getName(), object);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else
            configFile.mkdirs();
    }

    public void save(WorldObject object) {
        File file = new File(configFile, object.getName() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        object.serialize(yamlConfiguration);
        yamlConfiguration.set("==", object.getClass().getName());
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<WorldObject> getNearLocationWorldObject(Location location, double distance) {
        return worldObjectHashMap.values().stream().filter(object -> object.getMainLocation().distance(location) < distance).collect(Collectors.toList());
    }

    public List<WorldObject> getWorldObjects() {
        return worldObjectHashMap.values().stream().collect(Collectors.toList());
    }

    public WorldObject getWorldObjectByName(String name) {
        return worldObjectHashMap.get(name);
    }

    public void removeWorldObject(String name) {
        worldObjectHashMap.remove(name);
    }

    public void removeWorldObject(WorldObject worldObject) {
        worldObjectHashMap.remove(worldObject.getName());
    }

    @Override
    public List<RequestWithInfo> getRequestWithInfos() {
        return worldObjectHashMap.values().stream().map(WorldObjectManager::object2Request).collect(Collectors.toList());
    }

    private static RequestWithInfo object2Request(WorldObject worldObject) {
        ChatSetup chatSetup = new ChatSetup(worldObject.getName() + " 设置");
        chatSetup.addRequest(worldObject.getRequestWithInfos());
        RequestWithInfo requestWithInfo = new RequestWithInfo(chatSetup, Material.STONE, () -> worldObject.getName(), () -> worldObject.getInfo());
        return requestWithInfo;
    }

}
