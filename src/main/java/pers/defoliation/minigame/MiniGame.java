package pers.defoliation.minigame;

import org.bukkit.plugin.java.JavaPlugin;
import pers.defoliation.minigame.state.StateManager;

public class MiniGame extends JavaPlugin {

    public static MiniGame INSTANCE;

    public static final boolean AGENT_ENABLE = false;

    public MiniGame() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        StateManager.init();
    }
}
