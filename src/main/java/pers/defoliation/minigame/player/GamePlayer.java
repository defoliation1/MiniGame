package pers.defoliation.minigame.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pers.defoliation.minigame.game.Game;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GamePlayer {

    private static final Map<String, GamePlayer> map = Collections.synchronizedMap(new HashMap<>());

    private String playerName;

    private Game game;
    private boolean isSpectator;

    private HashMap<String, Object> playerData = new HashMap<>();

    private GamePlayer(String playerName) {
        this.playerName = playerName;
    }

    public String getName() {
        return playerName;
    }

    public boolean isOnline() {
        return getPlayer() != null && getPlayer().isOnline();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerName);
    }

    public Game playingGame() {
        return game;
    }

    public void setPlayingGame(Game game) {
        this.game = game;
    }

    public static GamePlayer getGamePlayer(String playerName) {
        return map.computeIfAbsent(playerName, GamePlayer::new);
    }

    public static GamePlayer getGamePlayer(OfflinePlayer player) {
        return getGamePlayer(player.getName());
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public boolean isEffective() {
        return isOnline() && !isSpectator() && game != null;
    }

    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
    }

    public <T> T getData(String key) {
        return (T) playerData.get(key);
    }

    public HashMap<String, Object> getPlayerData() {
        return playerData;
    }

    public void setData(String key, Object value) {
        playerData.put(key, value);
    }

    public void clearData() {
        playerData.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePlayer that = (GamePlayer) o;
        return isSpectator == that.isSpectator &&
                Objects.equals(playerName, that.playerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerName, isSpectator);
    }
}
