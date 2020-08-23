package pers.defoliation.minigame.map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import pers.defoliation.minigame.MiniGame;

public final class RegionFactory {

    private RegionFactory() {
    }

    public static Region getWEResetRegion(Location locationA, Location locationB) {
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            throw new IllegalStateException("WorldEdit not found");
        }
        return new WE12Region(locationA,locationB);
    }

    public static Region getAgentUnsaveRegion(Location locationA, Location locationB) {
        if(MiniGame.AGENT_ENABLE)
            throw new IllegalStateException("agent not enable");
        return new AgentRegion(locationA,locationB);
    }

}
