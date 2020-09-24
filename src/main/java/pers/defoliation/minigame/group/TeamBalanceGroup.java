package pers.defoliation.minigame.group;

import org.bukkit.entity.Player;
import pers.defoliation.minigame.player.GamePlayer;

import java.util.Collections;
import java.util.Comparator;

public class TeamBalanceGroup<T extends Team> extends GamePlayerGroup<T> {

    @Override
    public boolean canJoin(GamePlayer player) {
        if (getTeams().isEmpty())
            return false;
        return getTeams().get(0).canJoin();
    }

    @Override
    public void join(GamePlayer player) {
        getTeams().get(0).join(player);
        sortTeams();
        super.join(player);
    }

    private void sortTeams() {
        Collections.sort(getTeams(), Comparator.comparing(team -> team.playersNum()));
    }

    @Override
    public void leave(GamePlayer player) {
        super.leave(player);
        for (Team team : getTeams()) {
            if (team.contains(player))
                team.leave(player);
        }
        sortTeams();
        removeSpectator(player);
    }
}