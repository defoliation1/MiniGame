package pers.defoliation.minigame;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import pers.defoliation.minigame.game.GameState;
import pers.defoliation.minigame.location.LocationProvider;
import pers.defoliation.minigame.state.StateManager;

public class MiniGame extends JavaPlugin {

    public static MiniGame INSTANCE;

    public MiniGame() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(LocationProvider.class);
        StateManager.init();
    }
}
