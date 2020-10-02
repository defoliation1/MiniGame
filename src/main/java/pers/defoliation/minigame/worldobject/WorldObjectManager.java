package pers.defoliation.minigame.worldobject;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pers.defoliation.minigame.conversation.Conversation;
import pers.defoliation.minigame.conversation.request.Request;
import pers.defoliation.minigame.conversation.request.setup.ChatSetup;
import pers.defoliation.minigame.conversation.request.setup.RequestWithInfoSupplier;
import pers.defoliation.minigame.ui.RequestWithInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class WorldObjectManager implements RequestWithInfoSupplier, Listener {

    private static HashMap<String, WorldObjectManager> map = new HashMap<>();

    private static List<WorldObjectSupplier> suppliers = new ArrayList<>();

    private World world;
    private File configFile;
    private HashMap<String, WorldObject> worldObjectHashMap = new HashMap<>();
    protected final JavaPlugin plugin;
//    private GlowingWorldObject glowingWorldObject = new GlowingWorldObject();

    private WorldObjectManager(JavaPlugin plugin, World world) {
        this.world = world;
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), "worldObject");
        configFile = new File(configFile, world.getName());
        load();
    }

    public void load() {
        if (configFile.exists()) {
            worldObjectHashMap.values().forEach(WorldObject::unload);
            worldObjectHashMap.clear();
            for (File file : configFile.listFiles()) {
                YamlConfiguration yamlConfiguration = new YamlConfiguration();
                Bukkit.getLogger().info("deserialize: " + file.toString());
                try {
                    yamlConfiguration.load(file);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                    Bukkit.getLogger().info(file.toString());
                }
                String className = yamlConfiguration.getString("class");
                try {
                    WorldObject object = (WorldObject) Class.forName(className).newInstance();
                    try {
                        object.deserialize(yamlConfiguration.getConfigurationSection("object"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Bukkit.getLogger().info(file.toString() + " deserialize fail");
                        Bukkit.getLogger().info(file.toString() + " deserialize fail");
                        Bukkit.getLogger().info(file.toString() + " deserialize fail");
                        continue;
                    }
                    Bukkit.getLogger().info("put " + object.getName());
                    worldObjectHashMap.put(object.getName(), object);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else
            configFile.mkdirs();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String substring = event.getMessage().substring(1);
        if (substring.startsWith("worldobjectclickcommand")) {
            String substring1 = substring.substring("worldobjectclickcommand".length());
            String[] split = substring1.split(":");
            int id = Integer.valueOf(split[0]);
            for (WorldObject value : worldObjectHashMap.values()) {
                if (value.id == id) {
                    for (WorldObjectField worldObjectField : value.getFieldList()) {
                        if (worldObjectField.fieldName.equals(split[1])) {
                            Request setupRequest = worldObjectField.getSetupRequest();
                            if (setupRequest != null) {
                                Conversation conversation = new Conversation(plugin);
                                conversation.addRequest(setupRequest);
                                conversation.start(event.getPlayer());
                            }
                        }
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    public void save(WorldObject object) {
        File file = getObjectConfigFile(object.getName());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        ConfigurationSection object1;
        if (yamlConfiguration.contains("object")) {
            object1 = yamlConfiguration.getConfigurationSection("object");
        } else object1 = yamlConfiguration.createSection("object");
        object.serialize(object1);
        yamlConfiguration.set("class", object.getClass().getName());
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getObjectConfigFile(String objectName) {
        return new File(configFile, objectName + ".yml");
    }

    public void save() {
        worldObjectHashMap.values().forEach(this::save);
    }

    public List<WorldObject> getNearLocationWorldObject(Location location, double distance) {
        return worldObjectHashMap.values().stream().filter(object -> object.getMainLocation().distance(location) < distance).collect(Collectors.toList());
    }

    public List<WorldObject> getWorldObjects() {
        return worldObjectHashMap.values().stream().collect(Collectors.toList());
    }

    public WorldObject getWorldObjectByName(String name) {
        return worldObjectHashMap.get(name);
    }

    public void removeWorldObject(String name) {
        File objectConfigFile = getObjectConfigFile(name);
        if (objectConfigFile.exists())
            objectConfigFile.delete();
        worldObjectHashMap.remove(name);
    }

    public void removeWorldObject(WorldObject worldObject) {
        worldObjectHashMap.remove(worldObject.getName());
    }

    public void addWorldObject(String name, WorldObject worldObject) {
        worldObjectHashMap.put(name, worldObject);
    }

    @Override
    public List<RequestWithInfo> getRequestWithInfos() {
        return worldObjectHashMap.values().stream().map(WorldObjectManager::object2Request).collect(Collectors.toList());
    }

    private static RequestWithInfo object2Request(WorldObject worldObject) {
        ChatSetup chatSetup = new ChatSetup(worldObject.getName());
        chatSetup.addRequest(worldObject.getRequestWithInfos());
        List<String> list = new ArrayList<>();
        for (BaseComponent baseComponent : worldObject.getInfo()) {
            list.add(baseComponent.toPlainText());
        }
//        RequestWithInfo requestWithInfo = new RequestWithInfo(chatSetup,new ItemStack(), () -> worldObject.getName(), () -> list, () -> true);
        return null;
//        return requestWithInfo;
    }

    public static WorldObjectManager getWorldObjectManager(JavaPlugin javaPlugin, World world) {
        return map.computeIfAbsent(javaPlugin.getName() + world.getName(), s -> new WorldObjectManager(javaPlugin, world));
    }

    public static void addSupplier(WorldObjectSupplier supplier) {
        suppliers.add(supplier);
    }

    public static List<WorldObjectSupplier> getSuppliers() {
        return suppliers;
    }
/*
    public static void sendGlowingBlock(Player p, Location loc, long lifetime){
        Bukkit.getScheduler().scheduleSyncDelayedTask(MiniGame.INSTANCE, () -> {
            PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

            EntityShulker shulker = new EntityShulker(((CraftWorld) loc.getWorld()).getHandle());
            shulker.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
            shulker.setFlag(6, true); //Glow
            shulker.setFlag(5, true); //Invisibility

            PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(shulker);
            connection.sendPacket(spawnPacket);

            Bukkit.getScheduler().scheduleSyncDelayedTask(MiniGame.INSTANCE, () -> {
                PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(shulker.getId());
                connection.sendPacket(destroyPacket);
            }, lifetime + (long) ((Math.random() + 1) * 100));
        }, (long) ((Math.random() + 1) * 40));
    }*/

/*
    public GlowingWorldObject getGlowingWorldObject() {
        return glowingWorldObject;
    }

    public class GlowingWorldObject {

        public void glowingObjectNearPlayer(Player player, double distance) {

        }

        public Optional<WorldObject> getWorldObjectByGlowingEntity(Entity entity) {
            return Optional.empty();
        }

        public Optional<WorldObject> getWorldObjectByGlowingLocation(Location location) {
            return Optional.empty();
        }

    }
*/

}
