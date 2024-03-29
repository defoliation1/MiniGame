package pers.defoliation.minigame.group;

import org.bukkit.entity.Player;
import pers.defoliation.minigame.player.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public abstract class Team {

    private String teamName;
    private int maxPlayer;
    private List<GamePlayer> players = new ArrayList<>();

    public Team(String teamName, int maxPlayer) {
        this.teamName = teamName;
        this.maxPlayer = maxPlayer;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public String getTeamName() {
        return teamName;
    }

    public boolean canJoin() {
        return players.size() < getMaxPlayer();
    }

    public void join(GamePlayer player) {
        players.add(player);
    }

    public void leave(GamePlayer player) {
        players.remove(player);
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

    public boolean contains(GamePlayer player) {
        return players.contains(player);
    }

    public List<Player> getAlivePlayers() {
        ArrayList<Player> players = new ArrayList<>();
        for (GamePlayer player : this.players) {
            if (player.isOnline() && !player.isSpectator()) {
                players.add(player.getPlayer());
            }
        }
        return players;
    }

    public List<Player> getOnlinePlayers(){
        ArrayList<Player> players = new ArrayList<>();
        for (GamePlayer player : this.players) {
            if (player.isOnline()) {
                players.add(player.getPlayer());
            }
        }
        return players;
    }

    public int getAlivePlayerNum() {
        int i = 0;
        for (GamePlayer player : this.players) {
            if (player.isOnline() && !player.isSpectator()) {
                i++;
            }
        }
        return i;
    }

}
