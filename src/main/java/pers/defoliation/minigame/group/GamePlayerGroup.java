package pers.defoliation.minigame.group;

import org.bukkit.entity.Player;
import pers.defoliation.minigame.player.GamePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public abstract class GamePlayerGroup {

    private List<Team> teams = new ArrayList<>();

    private List<Consumer<Player>> joinConsumers = new ArrayList<>();
    private List<Consumer<Player>> leaveConsumers = new ArrayList<>();

    public List<Team> getTeams() {
        return teams;
    }

    public abstract boolean canJoin(Player player);

    public void join(Player player){
        joinConsumers.forEach(consumer -> consumer.accept(player));
    }

    public void leave(Player player){
        leaveConsumers.forEach(consumer -> consumer.accept(player));
    }

    public List<GamePlayer> getPlayers() {
        List<GamePlayer> teamPlayers = new ArrayList<>();
        for (Team team : teams) {
            teamPlayers.addAll(team.getPlayers());
        }
        return teamPlayers;
    }

    public void addJoinTask(Consumer<Player> consumer){
        joinConsumers.add(consumer);
    }

    public void addLeaveTask(Consumer<Player> consumer){
        leaveConsumers.add(consumer);
    }

    public int playerNum() {
        return getPlayers().size();
    }

    public Team getTeamByName(String teamName) {
        for (Team team : teams) {
            if (team.getTeamName().equals(teamName))
                return team;
        }
        return null;
    }

    public void addTeam(Team team) {
        this.teams.add(team);
    }

    public List<Player> getAlivePlayers() {
        List<Player> teamPlayers = new ArrayList<>();
        for (Team team : teams) {
            teamPlayers.addAll(team.getAlivePlayers());
        }
        return teamPlayers;
    }

    public int getMaxPlayer(){
        return teams.stream().flatMapToInt(team -> IntStream.of(team.getMaxPlayer())).sum();
    }

    public int getAlivePlayerNum() {
        return teams.stream().flatMapToInt(team -> IntStream.of(team.getAlivePlayerNum())).sum();
    }

    public List<Team> getAliveTeams() {
        List<Team> teams = new ArrayList<>();
        for (Team team : this.teams) {
            if (team.getAlivePlayerNum() > 0)
                teams.add(team);
        }
        return teams;
    }

}
