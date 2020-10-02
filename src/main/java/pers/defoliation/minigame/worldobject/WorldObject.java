package pers.defoliation.minigame.worldobject;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import pers.defoliation.minigame.conversation.request.setup.RequestWithInfoSupplier;
import pers.defoliation.minigame.ui.RequestWithInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class WorldObject implements RequestWithInfoSupplier {

    private static final AtomicInteger atomicInteger = new AtomicInteger();

    @ObjectField("组件名")
    private String name;

    protected int id = atomicInteger.getAndIncrement();

    private List<WorldObjectField> fieldList = new ArrayList<>();

    public WorldObject() {
        fieldList.addAll(setupField(WorldObjectField.getBuilder(this)).build());
    }

    public WorldObjectField.ObjectFieldBuilder setupField(WorldObjectField.ObjectFieldBuilder builder) {
        builder.getField("name")
                .setFieldToString(() -> name)
                .fieldDone();
        return builder;
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

    public List<WorldObjectField> getFieldList() {
        return fieldList;
    }

    public void setName(String name) {
        this.name = name;
    }

    //    public abstract List<GlowingObject> getGlowingObjects();

    public abstract Location getMainLocation();

    public abstract void load();

    public abstract void unload();

    public List<BaseComponent> getInfo() {
        ArrayList<BaseComponent> list = new ArrayList<>();
        list.add(new TextComponent("§4§l§m-------------------------------------"));
        list.add(new TextComponent());
        list.add(new TextComponent("类名: " + getClass().getName()));
        list.add(new TextComponent("主要位置: " + getMainLocation().toVector().toString()));
        for (WorldObjectField worldObjectField : fieldList) {
            list.add(field2BaseComponent(worldObjectField));
        }
        return list;
    }

    private BaseComponent field2BaseComponent(WorldObjectField field) {
        TextComponent textComponent = new TextComponent(field.name);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/worldobjectclickcommand" + id + ":" + field.fieldName));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Arrays.asList(field.desc).stream().map(m -> new TextComponent(m)).collect(Collectors.toList()).toArray(new BaseComponent[0])));
        TextComponent textComponent1 = new TextComponent(": ");
        textComponent.addExtra(textComponent1);
        TextComponent textComponent2 = new TextComponent(field.getFieldToString());
        textComponent2.setColor(field.isSetup() ? ChatColor.GREEN : ChatColor.RED);
        textComponent.addExtra(textComponent2);
        return textComponent;
    }

}
