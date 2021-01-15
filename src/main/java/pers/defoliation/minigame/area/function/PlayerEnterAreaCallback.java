package pers.defoliation.minigame.area.function;

import org.bukkit.entity.Player;
import pers.defoliation.minigame.area.Area;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface PlayerEnterAreaCallback extends BiConsumer<Player, Area> {
}
