package pers.defoliation.minigame.group;

import pers.defoliation.minigame.player.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private int maxPlayer;
    private List<GamePlayer> players = new ArrayList<>();

    public Team(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public boolean canJoin() {
        return players.size() < maxPlayer;
    }

    public void join(String player) {
        players.add(GamePlayer.getGamePlayer(player));
    }

    public void leave(String player) {
        GamePlayer leavePlayer = null;
        for (GamePlayer gamePlayer : players) {
            if (gamePlayer.getPlayerName().equals(player))
                leavePlayer = gamePlayer;
        }
        if (leavePlayer != null)
            players.remove(leavePlayer);
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public int playersNum() {
        return players.size();
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public boolean contains(String name) {
        for (GamePlayer player : players) {
            if (player.getPlayerName().equals(name))
                return true;
        }
        return false;
    }

}
