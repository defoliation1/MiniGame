package pers.defoliation.minigame.game;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pers.defoliation.minigame.state.StateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class GameManager<T extends Game> implements Listener {

    private static final Random random = new Random(System.currentTimeMillis());
    private List<T> games = new ArrayList<>();

    private T playingGame;
    private T waitingGame;

    public GameManager(JavaPlugin plugin) {

        Bukkit.getPluginManager().registerEvents(this, plugin);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if ((waitingGame == null || getGameState(waitingGame) != GameState.WAITING) && !games.isEmpty()) {
                List<T> collect = games.stream().filter(game -> getGameState(game) == GameState.WAITING).collect(Collectors.toList());
                if (!collect.isEmpty()) {
                    waitingGame = collect.get(random.nextInt(collect.size()));
                }
            }
        }, 10, 20);

    }

    @EventHandler
    public void serverPing(ServerListPingEvent event) {
        GameState state;
        if (playingGame == null) {
            state = getGameState(waitingGame);
        } else {
            state = getGameState(playingGame);
        }
        if (state == null) {
            event.setMotd("ERROR");
        } else {
            event.setMotd(state.name());
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent playerJoinEvent) {
        if (getPlayingGame() == null) {
            if (getWaitingGame() == null) {
                playerJoinEvent.getPlayer().kickPlayer("服务器错误-没有准备中的游戏");
            } else {
                if (getGameState(getWaitingGame()) != GameState.WAITING || !getWaitingGame().canJoin(playerJoinEvent.getPlayer())) {
                    playerJoinEvent.getPlayer().kickPlayer("加入游戏错误");
                    return;
                }
                getWaitingGame().join(playerJoinEvent.getPlayer());
            }
        }
    }

    protected abstract GameState getGameState(T game);

    public void addGame(T game) {
        games.add(game);
        StateManager.addInstance(GameState.PREPARING, game);
    }

    public T getWaitingGame() {
        return waitingGame;
    }

    public T getPlayingGame() {
        return playingGame;
    }

    public void setPlayingGame(T playingGame) {
        this.playingGame = playingGame;
    }
}
