package pers.defoliation.minigame;

public interface Game {

    GameState preparing();

    GameState waiting();

    GameState running();

    GameState ended();

}
