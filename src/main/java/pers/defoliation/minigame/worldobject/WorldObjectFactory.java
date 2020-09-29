package pers.defoliation.minigame.worldobject;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class WorldObjectFactory {

    private static HashMap<String, WorldObjectManager> map = new HashMap<>();

    public static WorldObjectManager getWorldObjectManager(JavaPlugin javaPlugin, World world) {
        return map.computeIfAbsent(world.getName(), s -> new WorldObjectManager(javaPlugin, world));
    }

}
