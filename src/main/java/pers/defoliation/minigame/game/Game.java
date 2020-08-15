package pers.defoliation.minigame.game;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import pers.defoliation.minigame.group.GamePlayerGroup;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Game {

    private String gameName;
    private YamlConfiguration config;

    public Game(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    public YamlConfiguration getGameConfig() {
        if (config != null)
            return config;
        config = new YamlConfiguration();
        loadConfig();
        return config;
    }

    public void loadConfig() {
        try {
            config.load(getGameConfigFile());
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
            config.save(getGameConfigFile());
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

    public abstract GamePlayerGroup getGroup();

    public abstract void getConfigConversation();

}
