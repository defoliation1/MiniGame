package pers.defoliation.minigame.util.setup;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import pers.defoliation.minigame.conversation.request.Request;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class SetUp {

    private static final AtomicLong id = new AtomicLong();
    private final String title;
    private final Object instance;
    private List<FieldRequest> list = new ArrayList<>();

    public SetUp(String title, Object instance) {
        this.title = title;
        this.instance = instance;
    }

    public <T> void setFieldRequest(String fieldName, Request<T> request) {
        setFieldRequest(fieldName, request, String::valueOf);
    }

    public <T> void setFieldRequest(String fieldName, Request<T> request, Function<T, String> toString) {
        Field field = foundField(fieldName);
        if (field != null) {
            FieldName annotation = field.getAnnotation(FieldName.class);
            FieldRequest fieldRequest = new FieldRequest(fieldName, field, annotation.desc(), request, toString);
            list.add(fieldRequest);
        }
    }

    private Field foundField(String fieldName) {
        return foundField(instance.getClass(), fieldName);
    }

    private Field foundField(Class instanceClass, String fieldName) {
        if (Object.class.equals(instanceClass))
            return null;
        for (Field declaredField : instanceClass.getDeclaredFields()) {
            FieldName annotation = declaredField.getAnnotation(FieldName.class);
            if (annotation != null && annotation.value().equals(fieldName)) {
                return declaredField;
            }
        }
        return foundField(instanceClass.getSuperclass(), fieldName);
    }


    public void sendSetUpMessage(Player player) {
        player.sendMessage(title);
        list.stream().map(this::getFieldComponent).forEach(baseComponent -> player.spigot().sendMessage(baseComponent));
    }

    private BaseComponent getFieldComponent(FieldRequest fieldRequest) {
        TextComponent textComponent = new TextComponent(fieldRequest.name);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, string2Component(fieldRequest.desc)));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "setupclickcommand " + fieldRequest.id));
        TextComponent textComponent1 = new TextComponent(": ");
        textComponent.addExtra(textComponent1);
        TextComponent textComponent2 = new TextComponent((String) fieldRequest.toStringFunction.apply(fieldRequest.getValue()));
        textComponent.addExtra(textComponent2);
        return textComponent;
    }

    private BaseComponent[] string2Component(String[] strings) {
        BaseComponent[] baseComponents = new BaseComponent[strings.length];
        for (int i = 0; i < strings.length; i++) {
            baseComponents[i] = new TextComponent(strings[i]);
        }
        return baseComponents;
    }

    private class FieldRequest<T> {

        public final long id = SetUp.id.getAndIncrement();
        public final String name;
        public final Field field;
        public final String[] desc;
        public final Request<T> request;
        public final Function<T, String> toStringFunction;

        public FieldRequest(String name, Field field, String[] desc, Request<T> request, Function<T, String> toStringFunction) {
            this.name = name;
            this.field = field;
            this.desc = desc;
            this.request = request;
            this.toStringFunction = toStringFunction;

            field.setAccessible(true);
        }

        public T getValue() {
            try {
                return (T) field.get(instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
