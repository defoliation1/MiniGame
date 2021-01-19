package pers.defoliation.minigame.util.setup;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pers.defoliation.minigame.MiniGame;
import pers.defoliation.minigame.conversation.Conversation;
import pers.defoliation.minigame.conversation.request.Request;
import pers.defoliation.minigame.listener.MiniGameEventHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class SetUp {

    private static final AtomicLong id = new AtomicLong();
    private static final Map<Long, FieldRequest> idMap = new HashMap<>();
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
            FieldRequest fieldRequest = new FieldRequest(this, fieldName, field, annotation.desc(), request, toString);
            list.add(fieldRequest);
            idMap.put(fieldRequest.id, fieldRequest);
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
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setupclickcommand " + fieldRequest.id));
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
        public final SetUp setUp;
        public final String name;
        public final Field field;
        public final String[] desc;
        public final Request<T> request;
        public final Function<T, String> toStringFunction;

        public FieldRequest(SetUp setUp, String name, Field field, String[] desc, Request<T> request, Function<T, String> toStringFunction) {
            this.setUp = setUp;
            this.name = name;
            this.field = field;
            this.desc = desc;
            this.request = request;
            this.toStringFunction = toStringFunction;
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

    static {

        MiniGameEventHandler miniGameEventHandler = new MiniGameEventHandler(MiniGame.INSTANCE);
        miniGameEventHandler.addHandle(PlayerCommandPreprocessEvent.class, playerCommandPreprocessEvent -> {
            String message = playerCommandPreprocessEvent.getMessage().substring(1);
            String[] s = message.split(" ");
            if (s.length == 2 && "setupclickcommand".equals(s[0])) {
                Long id = Long.valueOf(s[1]);
                if (idMap.containsKey(id)) {
                    Conversation conversation = new Conversation(MiniGame.INSTANCE);
                    conversation.addRequest(idMap.get(id).request);
                    conversation.start(playerCommandPreprocessEvent.getPlayer());
                    conversation.setOnComplete(conversation1 -> idMap.get(id).setUp.sendSetUpMessage(conversation1.getPlayer()));
                }
            }
        });

    }

}
