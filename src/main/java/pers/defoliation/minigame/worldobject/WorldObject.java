package pers.defoliation.minigame.worldobject;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pers.defoliation.minigame.MiniGame;
import pers.defoliation.minigame.conversation.request.setup.RequestWithInfoSupplier;
import pers.defoliation.minigame.ui.RequestWithInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class WorldObject implements RequestWithInfoSupplier, Listener {

    private static final AtomicInteger atomicInteger = new AtomicInteger();

    @ObjectField("组件名")
    private String name;

    private int id = atomicInteger.getAndIncrement();

    private List<WorldObjectField> fieldList = new ArrayList<>();

    public WorldObject() {
        fieldList.addAll(WorldObjectField.getBuilder(this).build());
    }

    @Override
    public List<RequestWithInfo> getRequestWithInfos() {
        return fieldList.stream().map(WorldObject::field2Request).collect(Collectors.toList());
    }

    private static RequestWithInfo field2Request(WorldObjectField field) {
        return RequestWithInfo.wrap(field.getSetupRequest(), field.itemStack, () -> field.name, () -> Arrays.asList(field.desc), () -> field.isSetup());
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

    protected void addWorldObjectField(List<WorldObjectField> worldObjectFields) {
        fieldList.addAll(worldObjectFields);
    }

    public void setName(String name) {
        this.name = name;
    }

    //    public abstract List<GlowingObject> getGlowingObjects();

    public abstract Location getMainLocation();

    public void load() {
        Bukkit.getPluginManager().registerEvents(this, MiniGame.INSTANCE);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String substring = event.getMessage().substring(1);
        if (substring.startsWith("worldobjectclickcommand")) {
            event.setCancelled(true);

        }
    }

    public void unload() {
        PlayerCommandPreprocessEvent.getHandlerList().unregister(this);
    }

    public List<BaseComponent> getInfo() {
        ArrayList<BaseComponent> list = new ArrayList<>();
        list.add(new TextComponent("类名: " + getClass().getName()));
        list.add(new TextComponent("主要位置: " + getMainLocation().toVector().toString()));
        for (WorldObjectField worldObjectField : fieldList) {
            list.add(field2BaseComponent(worldObjectField));
        }
        return list;
    }

    private BaseComponent field2BaseComponent(WorldObjectField field) {
        TextComponent textComponent = new TextComponent(field.name);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/worldobjectclickcommand:" + id + ":" + field.fieldName));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Arrays.asList(field.desc).stream().map(m -> new TextComponent(m)).collect(Collectors.toList()).toArray(new BaseComponent[0])));
        textComponent.setColor(field.isSetup() ? ChatColor.GREEN : ChatColor.RED);
        TextComponent textComponent1 = new TextComponent(": ");
        textComponent1.setColor(ChatColor.WHITE);
        textComponent.addExtra(textComponent1);
        TextComponent textComponent2 = new TextComponent(field.getFieldToString());
        textComponent.addExtra(textComponent2);
        return textComponent;
    }

}
