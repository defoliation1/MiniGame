package pers.defoliation.minigame;

import org.bukkit.plugin.java.JavaPlugin;
import pers.defoliation.minigame.state.StateManager;

public class MiniGame extends JavaPlugin {

    public static MiniGame INSTANCE;

    public MiniGame() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        StateManager.init();
        StateManager.create(GameState.ENDED);
    }
}
