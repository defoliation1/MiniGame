package pers.defoliation.minigame;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pers.defoliation.minigame.listener.SpectateListener;

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
