package pers.defoliation.minigame.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pers.defoliation.minigame.game.Game;

import java.util.HashMap;
import java.util.Objects;

public class GamePlayer {

    private static final HashMap<String, GamePlayer> map = new HashMap<>();

    private String playerName;

    private Game game;
    private boolean isSpectator;

    private GamePlayer(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isOnline() {
        return Bukkit.getOfflinePlayer(playerName).isOnline();
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
        return map.computeIfAbsent(playerName, key -> new GamePlayer(key));
    }

    public static GamePlayer getGamePlayer(OfflinePlayer player) {
        return getGamePlayer(player.getName());
    }

    public boolean isSpectator() {
        return game != null && isSpectator;
    }

    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
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
