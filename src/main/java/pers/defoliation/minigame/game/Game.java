package pers.defoliation.minigame.game;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pers.defoliation.minigame.config.GameConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Game {

    private String gameName;
    private GameConfigurationSection config;

    public Game(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
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

}
