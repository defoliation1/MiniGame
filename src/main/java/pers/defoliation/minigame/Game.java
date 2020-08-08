package pers.defoliation.minigame;

public interface Game {

    GameState preparing();

    GameState waiting(int time);

    GameState running();

    GameState ended();

}
