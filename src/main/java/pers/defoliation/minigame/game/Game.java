package pers.defoliation.minigame.game;

import pers.defoliation.minigame.group.GamePlayerGroup;

import java.util.concurrent.atomic.AtomicInteger;

public interface Game {

    GameState preparing(AtomicInteger time);

    GameState waiting(AtomicInteger time);

    GameState running(AtomicInteger time);

    GameState ended(AtomicInteger time);

    GamePlayerGroup getGroup();

}
