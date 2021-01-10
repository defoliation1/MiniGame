package pers.defoliation.minigame.map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import pers.defoliation.minigame.MiniGame;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class WorldTempManager {

    public static final WorldTempManager INSTANCE = new WorldTempManager();

    private AtomicInteger atomicInteger = new AtomicInteger();

    public WorldTempManager() {
        for (File file : new File(System.getProperty("user.dir")).listFiles()) {
            if (file.getName().startsWith("temp-")) {
                Bukkit.getScheduler().runTask(MiniGame.INSTANCE, () -> deleteWorld(file.getName(), true));
            }
        }
    }

    public void createTemp(World copyWorld, Consumer<World> onTempCreate) {
        String newWorldName = "temp-" + copyWorld.getName() + "-" + atomicInteger.incrementAndGet();
        copyWorld(copyWorld.getName(), newWorldName, "uid.dat");
        onTempCreate.accept(loadWorld(newWorldName));
    }

    private void copyWorld(String worldName, String newLocation, String... ignore) {
        ArrayList<String> ignored = new ArrayList<>(Arrays.asList(ignore));
        for (File file : new File(worldName).listFiles()) {
            if (!ignored.contains(file.getName())) {
                if (file.isDirectory()) {
                    new File(newLocation + "\\" + file.getName()).mkdirs();
                    copyWorld(worldName + "\\" + file.getName(), newLocation + "\\" + file.getName() + "\\", ignore);
                } else {
                    try {
                        InputStream in = new FileInputStream(file.getAbsolutePath());
                        OutputStream out = new FileOutputStream(newLocation.endsWith("\\")
                                ? newLocation + file.getName() : newLocation + "\\" + file.getName());
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = in.read(buffer)) > 0)
                            out.write(buffer, 0, length);
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean deleteWorld(String worldName, boolean loaded) {
        if (loaded) {
            unloadWorld(worldName);
        }
        File path = new File(worldName);
        if (path.exists()) {
            for (File file : path.listFiles()) {
                if (file.isDirectory()) {
                    deleteWorld(worldName + "\\" + file.getName(), false);
                } else {
                    file.delete();
                }
            }
        }
        return (path.delete());
    }

    public boolean unloadWorld(String worldName) {
        if (Bukkit.getWorld(worldName) != null) {
            Bukkit.getServer().unloadWorld(Bukkit.getWorld(worldName), true);
            return true;
        }
        return false;
    }

    public World loadWorld(String worldName) {
        WorldCreator worldCreater = new WorldCreator(worldName);
        Bukkit.getServer().createWorld(worldCreater);
        return Bukkit.getWorld(worldName);
    }

    public static void init() {
    }

}
