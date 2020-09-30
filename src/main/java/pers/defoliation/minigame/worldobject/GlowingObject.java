package pers.defoliation.minigame.worldobject;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class GlowingObject {

    private Location location;
    private Entity entity;

    public GlowingObject(Location location) {
        this.location = location;
    }

    public GlowingObject(Entity entity) {
        this.entity = entity;
    }

    public Location getLocation() {
        return location;
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isLocation() {
        return location != null;
    }

    public boolean isEntity() {
        return entity != null;
    }

}
