package pers.defoliation.minigame.worldobject;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import pers.defoliation.minigame.conversation.request.RequestString;
import pers.defoliation.minigame.conversation.request.setup.RequestWithInfoSupplier;
import pers.defoliation.minigame.ui.PlayerSelect;
import pers.defoliation.minigame.ui.RequestWithInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class WorldObject implements RequestWithInfoSupplier {

    private String name;
    private boolean glowing;

    @Override
    public List<RequestWithInfo> getRequestWithInfos() {
        ArrayList<RequestWithInfo> list = new ArrayList<>();
        list.add(RequestWithInfo.wrap(RequestString.newRequestString().setOnComplete(r -> name = r.getResult().get()), Material.STONE, () -> "物体名称", () -> Arrays.asList("主要用于区分其他物品，故要保证唯一")));
        return list;
    }

    public void serialize(ConfigurationSection section) {
        section.set("name", name);
    }

    public void deserialize(ConfigurationSection section) {
        name = section.getString("name");
    }

    public String getName() {
        return name;
    }

    public void setGlowing(boolean b) {
        glowing = b;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public abstract Location getMainLocation();

    public abstract boolean isSelect(PlayerSelect select);

    public abstract void load();

    public abstract void unload();

    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        list.add("组件名: " + getName());
        list.add("类名: "+getClass().getName());
        list.add("主要位置: " + getMainLocation().toVector().toString());
        return list;
    }

}
