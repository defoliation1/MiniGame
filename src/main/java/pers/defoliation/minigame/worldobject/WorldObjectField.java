package pers.defoliation.minigame.worldobject;

import pers.defoliation.minigame.conversation.request.Request;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class WorldObjectField {

    public final String fieldName;
    public final String name;
    public final String[] desc;
    private final Field field;
    private final Object instance;

    private Supplier<String> field2String;
    private Request request;
    private Supplier<Boolean> setup;

    private WorldObjectField(String fieldName, String name, String[] desc, Field field, Object instance) {
        this.fieldName = fieldName;
        this.name = name;
        this.field = field;
        this.instance = instance;
        this.desc = desc;
        field.setAccessible(true);
        field2String = () -> {
            try {
                return Objects.toString(field.get(instance));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return "error: " + instance.getClass().getName() + ":" + field.getName();
        };
        setup = () -> {
            try {
                return field.get(instance) != null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
        };
    }

    public String getFieldToString() {
        return field2String.get();
    }

    public Request getSetupRequest() {
        return request;
    }

    public boolean isSetup() {
        return setup.get();
    }

    public void setField2String(Supplier<String> field2String) {
        this.field2String = field2String;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void setSetup(Supplier<Boolean> setup) {
        this.setup = setup;
    }

    public static FieldBuilder getBuilder(Object instance) {
        return new FieldBuilder(instance);
    }

    public static class FieldBuilder {

        private List<WorldObjectField> worldObjectFieldList = new ArrayList<>();

        private FieldBuilder(Object instance) {
            for (Field declaredField : instance.getClass().getDeclaredFields()) {
                ObjectField annotation = declaredField.getAnnotation(ObjectField.class);
                if (annotation != null) {
                    WorldObjectField worldObjectField = new WorldObjectField(declaredField.getName(), annotation.value(), annotation.desc(), declaredField, instance);
                    worldObjectFieldList.add(worldObjectField);
                }
            }
        }

        public FieldBuilder setFieldToString(String name, Supplier<String> supplier) {
            getField(name).ifPresent(worldObjectField -> worldObjectField.field2String = supplier);
            return this;
        }

        private Optional<WorldObjectField> getField(String name) {
            return worldObjectFieldList.stream().filter(worldObjectField -> worldObjectField.name.equals(name)).findAny();
        }

        public FieldBuilder setRequest(String name, Request request) {
            getField(name).ifPresent(worldObjectField -> worldObjectField.request = request);
            return this;
        }

        public FieldBuilder setIsSetupSupplier(String name, Supplier<Boolean> supplier) {
            getField(name).ifPresent(worldObjectField -> worldObjectField.setup = supplier);
            return this;
        }

        public List<WorldObjectField> build() {
            return worldObjectFieldList;
        }
    }


}
