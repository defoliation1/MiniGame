package pers.defoliation.minigame.group;

import org.bukkit.entity.Player;
import pers.defoliation.minigame.player.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public abstract class GamePlayerGroup {

    private List<Team> teams = new ArrayList<>();

    public List<Team> getTeams() {
        return teams;
    }

    public abstract boolean canJoin(Player player);

    public abstract void join(Player player);

    public abstract void leave(Player player);

    public List<GamePlayer> getPlayers() {
        List<GamePlayer> teamPlayers = new ArrayList<>();
        for (Team team : teams) {
            teamPlayers.addAll(team.getPlayers());
        }
        return teamPlayers;
    }

    public int size() {
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

    public static GamePlayerGroup getGroup(boolean teamBalance) {
        return new TeamBalanceGroup();
    }

}
