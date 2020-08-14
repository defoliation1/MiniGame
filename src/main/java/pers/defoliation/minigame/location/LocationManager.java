package pers.defoliation.minigame.location;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LocationManager {

    private HashMap<String, LocationProvider> map = new HashMap<>();

    public LocationManager request(String... locationNames) {
        for (String locationName : locationNames) {
            if (!map.containsKey(locationName))
                map.put(locationName, new LocationProvider(locationName));
        }
        return this;
    }

    public LocationManager requestDisposableLocation(String... locationNames) {
        for (String locationName : locationNames) {
            requestDisposableLocation(locationName, 1);
        }
        return this;
    }

    public LocationManager requestDisposableLocation(String locationName, int useTimes) {
        if (!map.containsKey(locationName)) {
            LocationProvider provider = new LocationProvider(locationName);
            provider.setConsumeTimes(useTimes);
            map.put(locationName, provider);
        }
        return this;
    }

    public void putLocation(String locationName, Location location) {
        if (map.containsKey(locationName)) {
            map.get(locationName).setLocation(location);
        }
    }

    public List<String> getUnsetLocation() {
        return map.values().stream()
                .filter(locationProvider -> locationProvider.getLocation() == null)
                .map(LocationProvider::getName)
                .collect(Collectors.toList());
    }

    public Location getLocation(String location) {
        if (map.containsKey(location))
            return map.get(location).getLocation();
        return null;
    }

    public Location consumeLocation(String location) {
        if (map.containsKey(location))
            return map.get(location).consumeLocation();
        return null;
    }

    public void resetDisposableLocation() {
        map.values().forEach(LocationProvider::resetConsumeTimes);
    }

    public boolean allLocationIsReady() {
        return !map.values().stream()
                .filter(locationProvider -> locationProvider.getLocation() == null)
                .findAny().isPresent();
    }

    public void save(ConfigurationSection section) {
        section.set("locationProvider",map);
    }

    public void load(ConfigurationSection section) {
        map.clear();
        ConfigurationSection locationProvider = section.getConfigurationSection("locationProvider");
        for (String key : locationProvider.getKeys(false)) {
            map.put(key, (LocationProvider) locationProvider.get(key));
        }
    }

}
