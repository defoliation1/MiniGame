package pers.defoliation.minigame.worldobject;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import pers.defoliation.minigame.conversation.request.RequestString;
import pers.defoliation.minigame.conversation.request.setup.RequestWithInfoSupplier;
import pers.defoliation.minigame.ui.RequestWithInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class WorldObject implements RequestWithInfoSupplier {

    private String name;

    @Override
    public List<RequestWithInfo> getRequestWithInfos() {
        ArrayList<RequestWithInfo> list = new ArrayList<>();
        list.add(RequestWithInfo.wrap(RequestString.newRequestString().setOnComplete(r -> name = r.getResult().get()), Material.STONE, () -> "物体名称", () -> Arrays.asList(new TextComponent("主要用于区分其他物品，故要保证唯一")), () -> name != null));
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

    public abstract List<GlowingObject> getGlowingObjects();

    public abstract Location getMainLocation();

    public abstract void load();

    public abstract void unload();

    public List<BaseComponent> getInfo() {
        ArrayList<BaseComponent> list = new ArrayList<>();
        list.add(new TextComponent("组件名: " + getName()));
        list.add(new TextComponent("类名: " + getClass().getName()));
        list.add(new TextComponent("主要位置: " + getMainLocation().toVector().toString()));
        return list;
    }

}
