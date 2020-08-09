package pers.defoliation.minigame;

import java.util.concurrent.atomic.AtomicInteger;

public interface Game {

    GameState preparing();

    GameState waiting(AtomicInteger time);

    GameState running();

    GameState ended();

}
