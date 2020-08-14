package pers.defoliation.minigame.state;

import org.bukkit.Bukkit;
import pers.defoliation.minigame.MiniGame;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StateManager {

    private static Map<Class, Map<String, List<InstanceWrapper>>> enumMap = new HashMap<>();

    private static List<Object> removeValue = new ArrayList<>();

    static {
        Bukkit.getScheduler().runTaskTimer(MiniGame.INSTANCE, () -> {
            for (Map.Entry<Class, Map<String, List<InstanceWrapper>>> classMapEntry : enumMap.entrySet()) {
                Map<String, InstanceWrapper> moveMap = new HashMap<>();
                for (Map.Entry<String, List<InstanceWrapper>> stringListEntry : classMapEntry.getValue().entrySet()) {
                    if (stringListEntry.getValue().isEmpty())
                        continue;
                    String methodName = getMethodName(stringListEntry.getKey());
                    for (InstanceWrapper instanceWrapper : stringListEntry.getValue()) {
                        if (removeValue.contains(instanceWrapper.instance))
                            continue;
                        Object result = null;
                        if (instanceWrapper.needTime.get(methodName)) {
                            try {
                                result = instanceWrapper.methodMap.get(methodName).invoke(instanceWrapper.instance, instanceWrapper.runTime);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                result = instanceWrapper.methodMap.get(methodName).invoke(instanceWrapper.instance);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }

                        instanceWrapper.runTime.incrementAndGet();

                        if (result == null)
                            continue;
                        Enum anEnum = (Enum) result;
                        if (!getKey(anEnum).equals(stringListEntry.getKey())) {
                            removeValue.add(instanceWrapper);
                            moveMap.put(getKey(anEnum), instanceWrapper);
                            instanceWrapper.state = anEnum;
                            instanceWrapper.setStateField();
                        }
                    }
                    stringListEntry.getValue().removeAll(removeValue);
                    removeValue.clear();
                }
                for (Map.Entry<String, InstanceWrapper> stringObjectEntry : moveMap.entrySet()) {
                    classMapEntry.getValue().get(stringObjectEntry.getKey()).add(stringObjectEntry.getValue());
                }
            }
        }, 1, 1);
    }

    private static String getKey(Enum anEnum) {
        return anEnum.getClass().getName() + "-" + anEnum.name();
    }

    private static HashMap<String, String> methodCacheName = new HashMap<>();

    private static String getMethodName(String name) {
        if (methodCacheName.containsKey(name)) {
            return methodCacheName.get(name);
        }
        String enumName = name.substring(name.indexOf("-") + 1);
        if (!enumName.contains("_")) {
            methodCacheName.put(name, enumName.toLowerCase());
            return enumName.toLowerCase();
        }

        StringBuilder stringBuilder = new StringBuilder();
        boolean upFlag = false;
        for (char c : enumName.toCharArray()) {
            if (c >= 65 && c <= 90) {
                if (upFlag) {
                    stringBuilder.append(c);
                    upFlag = false;
                } else {
                    stringBuilder.append((char) (c + 32));
                }
            } else if (c == '_') {
                upFlag = true;
            }
        }
        methodCacheName.put(name, stringBuilder.toString());
        return stringBuilder.toString();
    }

    public static void create(Enum anEnum) {
        Class<? extends Enum> enumClass = anEnum.getClass();
        if (enumMap.containsKey(enumClass)) {
            throw new RuntimeException(enumClass + " already exist");
        }
        HashMap<String, List<InstanceWrapper>> map = new HashMap<>();
        enumMap.put(enumClass, map);

        Enum[] enumConstants = enumClass.getEnumConstants();

        for (Enum enumConstant : enumConstants) {
            map.put(getKey(enumConstant), new ArrayList<>());
        }
    }

    public static void addInstance(Enum defaultEnum, Object instance) {
        Class<? extends Enum> enumClass = defaultEnum.getClass();
        if (instanceCheck(enumClass, instance)) {
            enumMap.get(enumClass).get(getKey(defaultEnum)).add(new InstanceWrapper(defaultEnum, instance));
        }
    }

    public static void removeInstance(Object instance) {
        removeValue.add(instance);
    }

    private static boolean instanceCheck(Class<? extends Enum> enumClass, Object instance) {
        Class<?> instanceClass = instance.getClass();
        for (Enum enumConstant : enumClass.getEnumConstants()) {
            String methodName = getMethodName(enumClass.getName() + "-" + enumConstant.name());
            try {
                Method method = instanceClass.getMethod(methodName);
                if (!method.getReturnType().equals(enumClass)) {
                    throw new IllegalArgumentException(enumClass.getName() + "." + method.getName() + " must return " + enumClass.getName());
                }
            } catch (NoSuchMethodException e) {
                try {
                    Method method = instanceClass.getMethod(methodName, AtomicInteger.class);
                    if (!method.getReturnType().equals(enumClass)) {
                        throw new IllegalArgumentException(enumClass.getName() + "." + method.getName() + " must return " + enumClass.getName());
                    }
                } catch (NoSuchMethodException noSuchMethodException) {
                    noSuchMethodException.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    public static void init() {
    }

    public static <T extends Enum> T getState(Class<T> tClass,Object instance){
        for (List<InstanceWrapper> value : enumMap.get(tClass).values()) {
            for (InstanceWrapper instanceWrapper : value) {
                if(instanceWrapper.instance.equals(instance))
                    return (T) instanceWrapper.state;
            }
        }
        return null;
    }

    private static class InstanceWrapper {

        Object instance;
        Map<String, Method> methodMap = new HashMap<>();
        Map<String, Boolean> needTime = new HashMap<>();
        Field stateField;
        AtomicInteger runTime = new AtomicInteger();
        Enum state;

        public InstanceWrapper(Enum anEnum, Object instance) {
            state = anEnum;
            this.instance = instance;
            Class<?> aClass = instance.getClass();
            for (Enum enumConstant : anEnum.getClass().getEnumConstants()) {
                String methodName = getMethodName(getKey(enumConstant));
                try {
                    Method method = aClass.getMethod(methodName);
                    methodMap.put(methodName, method);
                    needTime.put(methodName, false);
                } catch (NoSuchMethodException e) {
                    try {
                        Method method = aClass.getMethod(methodName, AtomicInteger.class);
                        methodMap.put(methodName, method);
                        needTime.put(methodName, true);
                    } catch (NoSuchMethodException noSuchMethodException) {
                        noSuchMethodException.printStackTrace();
                    }
                }
            }
            for (Field field : instance.getClass().getFields()) {
                if (field.getDeclaringClass().equals(anEnum.getClass())) {
                    State annotation = field.getAnnotation(State.class);
                    if (annotation != null) {
                        stateField = field;
                        setStateField();
                        return;
                    }
                }
            }
        }

        private void setStateField() {
            if (stateField != null)
                try {
                    stateField.set(instance, state);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        }

    }


}
