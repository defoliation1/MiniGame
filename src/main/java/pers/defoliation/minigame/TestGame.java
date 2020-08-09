package pers.defoliation.minigame;

import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicInteger;

public class TestGame implements Game {

    @Override
    public GameState preparing() {
        Bukkit.getLogger().info("preparing");
        return GameState.WAITING;
    }

    @Override
    public GameState waiting(AtomicInteger time) {
        Bukkit.getLogger().info("waiting " + time);
        if (time.get() > 20)
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
