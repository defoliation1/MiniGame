package pers.defoliation.minigame.worldobject;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class WorldObjectManager {

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

}
