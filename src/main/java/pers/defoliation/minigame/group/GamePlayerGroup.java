package pers.defoliation.minigame.group;

import org.bukkit.entity.Player;
import pers.defoliation.minigame.player.GamePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class GamePlayerGroup<T extends Team> {

    private List<T> teams = new ArrayList<>();
    private List<GamePlayer> spectators = new ArrayList<>();

    private List<Consumer<GamePlayer>> joinConsumers = new ArrayList<>();
    private List<Consumer<GamePlayer>> leaveConsumers = new ArrayList<>();

    public List<T> getTeams() {
        return teams;
    }

    public abstract boolean canJoin(GamePlayer player);

    public void join(GamePlayer player) {
        joinConsumers.forEach(consumer -> consumer.accept(player));
    }

    public void leave(GamePlayer player) {
        leaveConsumers.forEach(consumer -> consumer.accept(player));
    }

    public List<GamePlayer> getPlayers() {
        List<GamePlayer> teamPlayers = new ArrayList<>();
        for (Team team : teams) {
            teamPlayers.addAll(team.getPlayers());
        }
        return teamPlayers;
    }

    public GamePlayerGroup addJoinTask(Consumer<GamePlayer> consumer) {
        joinConsumers.add(consumer);
        return this;
    }

    public GamePlayerGroup addLeaveTask(Consumer<GamePlayer> consumer) {
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

    public void removeTeam(T team) {
        this.teams.remove(team);
    }

    public List<Player> getAlivePlayers() {
        List<Player> teamPlayers = new ArrayList<>();
        for (Team team : teams) {
            teamPlayers.addAll(team.getAlivePlayers());
        }
        return teamPlayers;
    }

    public List<GamePlayer> getEffectPlayers() {
        return getPlayers().stream().filter(GamePlayer::isEffective).collect(Collectors.toList());
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

    public void addSpectator(GamePlayer gamePlayer) {
        for (T team : teams) {
            if (team.contains(gamePlayer.getName()))
                throw new IllegalStateException("玩家已在队伍内,加入观战失败 尝试加入的玩家: " + gamePlayer.getName());
        }
        spectators.add(gamePlayer);
    }

    public List<GamePlayer> getSpectators() {
        return spectators;
    }

    public void removeSpectator(GamePlayer gamePlayer) {
        this.spectators.remove(gamePlayer);
    }

}
