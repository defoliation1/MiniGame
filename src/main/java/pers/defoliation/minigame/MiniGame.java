package pers.defoliation.minigame;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pers.defoliation.minigame.map.WorldTempManager;
import pers.defoliation.minigame.state.StateManager;
import pers.defoliation.minigame.util.CacheUtils;

import java.io.File;

public class MiniGame extends JavaPlugin {

    public static MiniGame INSTANCE;

    public static final boolean AGENT_ENABLE = false;

    public MiniGame() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        CacheUtils.initCacheUtils();
        File dataFolder = getDataFolder();
        if (!dataFolder.exists())
            dataFolder.mkdirs();
        StateManager.init();
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        if (plugin != null)
            WorldTempManager.init();
    }
}
