package pers.defoliation.minigame.map;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import pers.defoliation.minigame.MiniGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class WorldTempManager {

    public static final WorldTempManager INSTANCE = new WorldTempManager();

    private MVWorldManager mvWorldManager;

    private AtomicInteger atomicInteger = new AtomicInteger();

    private HashMap<String, Consumer<World>> createTask = new HashMap<>();

    public WorldTempManager() {
        MultiverseCore plugin = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        mvWorldManager = plugin.getMVWorldManager();
        Bukkit.getScheduler().runTask(MiniGame.INSTANCE, () -> {
            List<MultiverseWorld> removeWorld = new ArrayList<>();
            for (MultiverseWorld mvWorld : mvWorldManager.getMVWorlds()) {
                if (mvWorld.getName().startsWith("temp-")) {
                    removeWorld.add(mvWorld);
                }
            }
            for (MultiverseWorld multiverseWorld : removeWorld) {
                mvWorldManager.deleteWorld(multiverseWorld.getName(), true, true);
            }
        });
        Bukkit.getScheduler().runTaskTimer(MiniGame.INSTANCE, () -> {
            List<String> remove = new ArrayList<>();
            for (Map.Entry<String, Consumer<World>> stringConsumerEntry : createTask.entrySet()) {
                World world = Bukkit.getWorld(stringConsumerEntry.getKey());
                if (world != null) {
                    stringConsumerEntry.getValue().accept(world);
                    remove.add(stringConsumerEntry.getKey());
                }
            }
            remove.forEach(createTask::remove);
        }, 10, 10);
    }

    public void createTemp(World copyWorld, Consumer<World> onTempCreate) {
        String newWorldName = "temp-" + copyWorld.getName() + "-" + atomicInteger.incrementAndGet();
        if (!mvWorldManager.cloneWorld(copyWorld.getName(), newWorldName)) {
            mvWorldManager.loadWorld(newWorldName);
        }
        createTask.put(newWorldName, onTempCreate);
    }

    public boolean isTempWorld(World world) {
        return world.getName().startsWith("temp-");
    }

    public boolean isMasterWorld(World world, World tempWorld) {
        String name = tempWorld.getName();
        if (name.length() > 5) {
            return name.substring(5).startsWith(world.getName());
        }
        return false;
    }

    public void deleteWorld(String worldName, boolean loaded) {
        mvWorldManager.deleteWorld(worldName, true, true);
    }

    public static void init() {
    }

}
