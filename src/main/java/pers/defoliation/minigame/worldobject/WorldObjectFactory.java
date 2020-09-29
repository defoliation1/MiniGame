package pers.defoliation.minigame.worldobject;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldObjectFactory {

    private static HashMap<String, WorldObjectManager> map = new HashMap<>();

    private static List<WorldObjectSupplier> suppliers = new ArrayList<>();


    public static WorldObjectManager getWorldObjectManager(JavaPlugin javaPlugin, World world) {
        return map.computeIfAbsent(world.getName(), s -> new WorldObjectManager(javaPlugin, world));
    }

    public static void addSupplier(WorldObjectSupplier supplier) {
        suppliers.add(supplier);
    }

    public static List<WorldObjectSupplier> getSuppliers() {
        return suppliers;
    }
}
