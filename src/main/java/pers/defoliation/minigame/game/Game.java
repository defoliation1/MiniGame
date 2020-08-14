package pers.defoliation.minigame.game;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import pers.defoliation.minigame.group.GamePlayerGroup;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Game {

    private String gameName;

    public Game(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    public YamlConfiguration getGameConfig() {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        File gameConfigFile = getGameConfigFile();
        if (!gameConfigFile.exists()) {
            gameConfigFile.getParentFile().mkdirs();
            try {
                gameConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            yamlConfiguration.load(gameConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return yamlConfiguration;
    }

    private File getGameConfigFile() {
        return new File(getDataFolder(), "game" + File.pathSeparator + gameName + ".yml");
    }

    public abstract File getDataFolder();

    public abstract GameState preparing(AtomicInteger time);

    public abstract GameState waiting(AtomicInteger time);

    public abstract GameState running(AtomicInteger time);

    public abstract GameState ended(AtomicInteger time);

    public abstract GamePlayerGroup getGroup();

}
