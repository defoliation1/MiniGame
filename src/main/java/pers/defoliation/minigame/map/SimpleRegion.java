package pers.defoliation.minigame.map;

import org.bukkit.Location;

import java.util.HashSet;

public abstract class SimpleRegion implements Region {

    private Location maxLocation;
    private Location minLocation;

    protected SimpleRegion(Location locationA, Location locationB) {
        maxLocation = new Location(locationA.getWorld(),
                getMax(locationA.getBlockX(), locationB.getBlockX()),
                getMax(locationA.getBlockY(), locationB.getBlockY()),
                getMax(locationA.getBlockZ(), locationB.getBlockZ()));
        minLocation = new Location(locationA.getWorld(),
                getMin(locationA.getBlockX(), locationB.getBlockX()),
                getMin(locationA.getBlockY(), locationB.getBlockY()),
                getMin(locationA.getBlockZ(), locationB.getBlockZ()));
    }

    private int getMax(int i1, int i2) {
        return i1 > i2 ? i1 : i2;
    }

    private int getMin(int i1, int i2) {
        return i1 < i2 ? i1 : i2;
    }

    @Override
    public Location getMaxLocation() {
        return maxLocation;
    }

    @Override
    public Location getMinLocation() {
        return minLocation;
    }

    private HashSet<Location> breakableBlock = new HashSet<>();
    private HashSet<Location> unbreakableBlock = new HashSet<>();
    private boolean canBreakInRegion;

    @Override
    public void addBreakableBlock(Location location) {
        breakableBlock.add(location);
    }

    @Override
    public boolean isBreakableBlock(Location location) {
        return breakableBlock.contains(location);
    }

    @Override
    public void removeBreakableBlock(Location location) {
        breakableBlock.remove(location);
    }

    @Override
    public void addUnbreakableBlock(Location location) {
        unbreakableBlock.add(location);
    }

    @Override
    public boolean isUnbreakableBlock(Location location) {
        return unbreakableBlock.contains(location);
    }

    @Override
    public void removeUnbreakableBlock(Location location) {
        unbreakableBlock.remove(location);
    }

    @Override
    public void setCanBreakInRegion(boolean flag) {
        canBreakInRegion = flag;
    }

    @Override
    public boolean canBreakInRegion() {
        return canBreakInRegion;
    }
}