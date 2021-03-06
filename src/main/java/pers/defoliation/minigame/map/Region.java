package pers.defoliation.minigame.map;

import org.bukkit.Location;

public interface Region {

    void reset();

    Location getMaxLocation();

    Location getMinLocation();

    void addBreakableBlock(Location location);

    boolean isBreakableBlock(Location location);

    void removeBreakableBlock(Location location);

    void addUnbreakableBlock(Location location);

    boolean isUnbreakableBlock(Location location);

    void removeUnbreakableBlock(Location location);

    void setCanBreakInRegion(boolean flag);

    boolean canBreakInRegion();

}
