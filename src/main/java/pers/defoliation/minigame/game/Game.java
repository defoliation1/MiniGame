package pers.defoliation.minigame.game;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pers.defoliation.minigame.config.GameConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Deprecated
public abstract class Game extends AbstractGame{

    private final String gameName;
    private GameConfigurationSection config;

    //在游戏中产生的数据，用于scoreboard等
    private HashMap<String, Object> gameData = new HashMap<>();

    public Game(String gameName) {
        super(gameName);
        this.gameName = gameName;
    }

    public GameConfigurationSection getGameConfig() {
        if (config != null)
            return config;
        config = new GameConfigurationSection(new YamlConfiguration());
        loadConfig();
        return config;
    }

    public void loadConfig() {
        try {
            ((YamlConfiguration) config.getSection()).load(getGameConfigFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private File getGameConfigFile() {
        File gameConfigFile = new File(getDataFolder(), "game" + File.pathSeparator + gameName + ".yml");
        if (!gameConfigFile.exists()) {
            gameConfigFile.getParentFile().mkdirs();
            try {
                gameConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return gameConfigFile;
    }

    public void saveConfig() {
        try {
            ((YamlConfiguration) config.getSection()).save(getGameConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultConfig() {
    }

    public abstract File getDataFolder();

    public abstract GameState preparing(AtomicInteger time);

    public abstract GameState waiting(AtomicInteger time);

    public abstract GameState running(AtomicInteger time);

    public abstract GameState ended(AtomicInteger time);

    public abstract boolean canJoin(Player player);

    public abstract void join(Player player);

    public abstract void leave(Player player);

    public <T> T getData(String key) {
        return (T) gameData.get(key);
    }

    public void putData(String key, Object value) {
        this.gameData.put(key, value);
    }

    public void clearData(){
        this.gameData.clear();
    }

    public HashMap<String, Object> getGameData() {
        return gameData;
    }
}
