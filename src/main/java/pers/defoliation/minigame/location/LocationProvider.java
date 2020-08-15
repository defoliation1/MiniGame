package pers.defoliation.minigame.location;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class LocationProvider implements ConfigurationSerializable {

    private String name;
    private Location location;
    private int consumeTimes=-1;
    private int consumeTemp;

    public LocationProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getConsumeTimes() {
        return consumeTimes;
    }

    public void setConsumeTimes(int consumeTimes) {
        this.consumeTimes = consumeTimes;
    }

    public void resetConsumeTimes() {
        this.consumeTemp = consumeTimes;
    }

    public Location consumeLocation() {
        if (consumeTemp > 0) {
            consumeTemp--;
            return getLocation();
        }else if(consumeTimes==-1)
            return getLocation();
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("location",location);
        map.put("consumeTimes",consumeTimes);
        return map;
    }

    public static LocationProvider deserialize(Map<String,Object> map){
        LocationProvider locationProvider = new LocationProvider((String) map.get("name"));
        locationProvider.setLocation((Location) map.get("location"));
        locationProvider.setConsumeTimes((Integer) map.get("consumeTimes"));
        return locationProvider;
    }

}
