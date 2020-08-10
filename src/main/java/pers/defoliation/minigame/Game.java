package pers.defoliation.minigame;

import java.util.concurrent.atomic.AtomicInteger;

public interface Game {

    GameState preparing(AtomicInteger time);

    GameState waiting(AtomicInteger time);

    GameState running(AtomicInteger time);

    GameState ended(AtomicInteger time);

}
