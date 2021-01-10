package pers.defoliation.minigame;

import org.bukkit.plugin.java.JavaPlugin;
import pers.defoliation.minigame.map.WorldTempManager;
import pers.defoliation.minigame.state.StateManager;

import java.io.File;

public class MiniGame extends JavaPlugin {

    public static MiniGame INSTANCE;

    public static final boolean AGENT_ENABLE = false;

    public MiniGame() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists())
            dataFolder.mkdirs();
        StateManager.init();
        WorldTempManager.init();
    }
}
