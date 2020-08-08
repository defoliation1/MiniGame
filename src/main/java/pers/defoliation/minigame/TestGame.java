package pers.defoliation.minigame;

import org.bukkit.Bukkit;

public class TestGame implements Game {

    @Override
    public GameState preparing() {
        Bukkit.getLogger().info("preparing");
        return GameState.WAITING;
    }

    @Override
    public GameState waiting(int time) {
        Bukkit.getLogger().info("waiting " + time);
        if (time > 20)
            return GameState.RUNNING;
        return null;
    }

    @Override
    public GameState running() {
        Bukkit.getLogger().info("running");
        return GameState.ENDED;
    }

    @Override
    public GameState ended() {
        Bukkit.getLogger().info("ended");
        return null;
    }
}
