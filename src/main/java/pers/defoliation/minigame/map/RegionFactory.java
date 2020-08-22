package pers.defoliation.minigame.map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class RegionFactory {

    private RegionFactory() {
    }

    public static Region getWEResetRegion(Location locationA, Location locationB) {
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            throw new IllegalStateException("WorldEdit not found");
        }

        return null;
    }

    public static Region getUnsaveRegion(Location locationA, Location locationB) {
        return null;
    }


}
