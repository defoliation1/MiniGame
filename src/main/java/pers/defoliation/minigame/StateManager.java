package pers.defoliation.minigame;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateManager {

    private static final Object[] nullObject = new Object[0];

    private static Map<Class, Map<String, List<Object>>> enumMap = new HashMap<>();

    static {
        Bukkit.getScheduler().runTaskTimer(MiniGame.INSTANCE, () -> {
            for (Map.Entry<Class, Map<String, List<Object>>> classMapEntry : enumMap.entrySet()) {
                Map<String, Object> moveMap = new HashMap<>();
                for (Map.Entry<String, List<Object>> stringListEntry : classMapEntry.getValue().entrySet()) {
                    if (stringListEntry.getValue().isEmpty())
                        continue;
                    String methodName = getMethodName(stringListEntry.getKey());
                    List<Object> removeValue = new ArrayList<>();
                    for (Object o : stringListEntry.getValue()) {
                        try {
                            Object invoke = o.getClass().getMethod(methodName).invoke(o, nullObject);
                            Enum anEnum = (Enum) invoke;
                            if (!anEnum.name().equals(stringListEntry.getKey())) {
                                removeValue.add(o);
                                moveMap.put(anEnum.getClass().getName() + "-" + anEnum.name(), o);
                            }
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
                for (Map.Entry<String, Object> stringObjectEntry : moveMap.entrySet()) {
                    classMapEntry.getValue().get(stringObjectEntry.getKey()).add(stringObjectEntry.getValue());
                }
            }
        }, 1, 1);
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

    public static void create(Class<Enum> enumClass) {
        if (enumMap.containsKey(enumClass)) {
            throw new RuntimeException(enumClass + " already exist");
        }
        HashMap<String, List<Object>> map = new HashMap<>();
        enumMap.put(enumClass, map);

        Enum[] enumConstants = enumClass.getEnumConstants();

        for (Enum enumConstant : enumConstants) {
            map.put(enumConstant.getClass().getName() + "-" + enumConstant.name(), new ArrayList<>());
        }
    }

    public static void addInstance(Class<Enum> enumClass, Enum defaultEnum, Object instance) {
        if (instanceCheck(enumClass, instance)) {
            enumMap.get(enumClass).get(enumClass.getName() + "-" + defaultEnum.name()).add(instance);
        }
    }

    private static boolean instanceCheck(Class<Enum> enumClass, Object instance) {
        Class<?> instanceClass = instance.getClass();
        for (Enum enumConstant : enumClass.getEnumConstants()) {
            String methodName = getMethodName(enumClass.getName() + "-" + enumConstant.name());
            try {
                Method method = instanceClass.getMethod(methodName);
                if (!method.getReturnType().equals(enumClass)) {
                    throw new IllegalArgumentException(enumClass.getName() + "." + method.getName() + " must return " + enumClass.getName());
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } finally {
                return false;
            }
        }
        return true;
    }


}
