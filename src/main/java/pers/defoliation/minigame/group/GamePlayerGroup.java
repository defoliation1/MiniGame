package pers.defoliation.minigame.group;

import org.bukkit.entity.Player;
import pers.defoliation.minigame.player.GamePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public abstract class GamePlayerGroup<T extends Team> {

    private List<T> teams = new ArrayList<>();
    private List<GamePlayer> spectators = new ArrayList<>();

    private List<Consumer<Player>> joinConsumers = new ArrayList<>();
    private List<Consumer<Player>> leaveConsumers = new ArrayList<>();

    public List<T> getTeams() {
        return teams;
    }

    public abstract boolean canJoin(Player player);

    public void join(Player player) {
        joinConsumers.forEach(consumer -> consumer.accept(player));
    }

    public void leave(Player player) {
        leaveConsumers.forEach(consumer -> consumer.accept(player));
    }

    public List<GamePlayer> getPlayers() {
        List<GamePlayer> teamPlayers = new ArrayList<>();
        for (Team team : teams) {
            teamPlayers.addAll(team.getPlayers());
        }
        return teamPlayers;
    }

    public GamePlayerGroup addJoinTask(Consumer<Player> consumer) {
        joinConsumers.add(consumer);
        return this;
    }

    public GamePlayerGroup addLeaveTask(Consumer<Player> consumer) {
        leaveConsumers.add(consumer);
        return this;
    }

    public int playerNum() {
        return getPlayers().size();
    }

    public T getTeamByName(String teamName) {
        for (T team : teams) {
            if (team.getTeamName().equals(teamName))
                return team;
        }
        return null;
    }

    public void addTeam(T team) {
        this.teams.add(team);
    }

    public List<Player> getAlivePlayers() {
        List<Player> teamPlayers = new ArrayList<>();
        for (Team team : teams) {
            teamPlayers.addAll(team.getAlivePlayers());
        }
        return teamPlayers;
    }

    public int getMaxPlayer() {
        return teams.stream().flatMapToInt(team -> IntStream.of(team.getMaxPlayer())).sum();
    }

    public int getAlivePlayerNum() {
        return teams.stream().flatMapToInt(team -> IntStream.of(team.getAlivePlayerNum())).sum();
    }

    public T getTeamByPlayer(Player player) {
        return getTeamByPlayer(GamePlayer.getGamePlayer(player));
    }

    public T getTeamByPlayer(GamePlayer player) {
        for (T team : teams) {
            if (team.getPlayers().contains(player))
                return team;
        }
        return null;
    }

    public List<T> getAliveTeams() {
        List<T> teams = new ArrayList<>();
        for (T team : this.teams) {
            if (team.getAlivePlayerNum() > 0)
                teams.add(team);
        }
        return teams;
    }

    public void addSpectator(Player gamePlayer) {
        spectators.add(GamePlayer.getGamePlayer(gamePlayer));
    }

    public List<GamePlayer> getSpectators() {
        return spectators;
    }

    public void removeSpectator(Player gamePlayer) {
        this.spectators.remove(GamePlayer.getGamePlayer(gamePlayer));
    }

}
