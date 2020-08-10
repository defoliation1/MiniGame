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

    public String getTeamName() {
        return teamName;
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

    public List<Player> getAlivePlayers(){
        ArrayList<Player> players = new ArrayList<>();
        for (GamePlayer player : this.players) {
            if(player.isOnline()&&!player.isSpectator()){
                players.add(player.getPlayer());
            }
        }
        return players;
    }

    public int getAlivePlayerNum(){
        int i = 0;
        for (GamePlayer player : this.players) {
            if(player.isOnline()&&!player.isSpectator()){
                i++;
            }
        }
        return i;
    }

}
