package pers.defoliation.minigame.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public abstract class AbstractGame {

    private final String gameName;
    //在游戏中产生的数据，用于scoreboard等
    private HashMap<String, Object> gameData = new HashMap<>();
    private GameState gameState = GameState.PREPARING;

    private int gameTick;

    private BukkitTask task;

    public AbstractGame(String gameName) {
        this.gameName = gameName;
    }

    public void start() {
        changeState(GameState.PREPARING);
    }

    public void changeState(GameState state) {
        gameState = state;
        switch (state) {
            case PREPARING:
                onPreparing();
                break;
            case WAITING:
                onWaiting();
                break;
            case RUNNING:
                onRunning();
                break;
            case ENDED:
                onEnded();
                break;
        }
    }

    public final GameState getGameState() {
        return gameState;
    }

    public abstract JavaPlugin getPlugin();

    public void onPreparing() {
        gameTick = 0;
        if (task != null) {
            task.cancel();
            task = Bukkit.getScheduler().runTaskTimer(getPlugin(), this::tick, 1, 1);
        }
    }

    public final int getGameTick() {
        return gameTick;
    }

    public final String getGameName() {
        return gameName;
    }

    private void tick() {
        gameTick++;
        switch (gameState) {
            case PREPARING:
                preparing();
                break;
            case WAITING:
                waiting();
                break;
            case RUNNING:
                running();
                break;
            case ENDED:
                ended();
                break;
            default:
                task.cancel();
                throw new IllegalStateException();
        }
    }

    public void preparing() {

    }

    public void onWaiting() {

    }

    public void waiting() {

    }

    public void onRunning() {

    }

    public void running() {

    }

    public void onEnded() {

    }

    public void ended() {

    }

    public void unload() {
        if (task != null)
            task.cancel();
    }

    public abstract boolean canJoin(Player player);

    public abstract void join(Player player);

    public abstract void leave(Player player);

    public <T> T getData(String key) {
        return (T) gameData.get(key);
    }

    public void putData(String key, Object value) {
        this.gameData.put(key, value);
    }

    public void clearData() {
        this.gameData.clear();
    }

    public HashMap<String, Object> getGameData() {
        return gameData;
    }
}
