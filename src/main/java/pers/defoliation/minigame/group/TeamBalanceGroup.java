package pers.defoliation.minigame.group;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Comparator;

public class TeamBalanceGroup<T extends Team> extends GamePlayerGroup<T> {

    @Override
    public boolean canJoin(Player player) {
        if (getTeams().isEmpty())
            return false;
        return getTeams().get(0).canJoin();
    }

    @Override
    public void join(Player player) {
        getTeams().get(0).join(player.getName());
        sortTeams();
        super.join(player);
    }

    private void sortTeams() {
        Collections.sort(getTeams(), Comparator.comparing(team -> team.playersNum()));
    }

    @Override
    public void leave(Player player) {
        super.leave(player);
        for (Team team : getTeams()) {
            if (team.contains(player.getName()))
                team.leave(player.getName());
        }
        sortTeams();
        removeSpectator(player);
    }
}