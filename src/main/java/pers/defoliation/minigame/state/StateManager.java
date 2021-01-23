package pers.defoliation.minigame.state;

import org.bukkit.Bukkit;
import pers.defoliation.minigame.MiniGame;
import pers.defoliation.minigame.game.Game;
import pers.defoliation.minigame.game.GameState;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StateManager {

    private static List<InstanceWrapper> list = new ArrayList<>();

    private static List<Object> removeValue = new ArrayList<>();

    private StateManager() {
    }

    static {
        Bukkit.getScheduler().runTaskTimer(MiniGame.INSTANCE, () -> {
            list.removeIf(instanceWrapper -> removeValue.contains(instanceWrapper.instance));
            removeValue.clear();
            for (InstanceWrapper instanceWrapper : list) {

                Method method = instanceWrapper.methodMap.get(instanceWrapper.state.name());
                GameState gameState = null;
                try {
                    gameState = (GameState) method.invoke(instanceWrapper.instance, instanceWrapper.runTime);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (gameState != null) {
                    instanceWrapper.changeState(gameState);
                }
                instanceWrapper.runTime.incrementAndGet();
            }
        }, 1, 1);
    }

    public static void addInstance(GameState defaultEnum, Game instance) {
        list.add(new InstanceWrapper(defaultEnum, instance));
    }

    public static void removeInstance(Game instance) {
        removeValue.add(instance);
    }

    public static void init() {
    }

    public static <T extends Enum> T getState(Game instance) {
        for (InstanceWrapper instanceWrapper : list) {
            if (instanceWrapper.instance.equals(instance))
                return (T) instanceWrapper.state;
        }
        return null;
    }

    public static AtomicInteger getClock(Game instance) {
        for (InstanceWrapper instanceWrapper : list) {
            if (instanceWrapper.instance.equals(instance))
                return instanceWrapper.runTime;
        }
        return null;
    }

    private static class InstanceWrapper {

        Object instance;
        HashMap<String, Method> methodMap = new HashMap<>();
        HashMap<String, Method> changeOutMap = new HashMap<>();
        HashMap<String, Method> changeInMap = new HashMap<>();
        Field stateField;
        AtomicInteger runTime = new AtomicInteger();
        GameState state;

        public InstanceWrapper(GameState anEnum, Object instance) {
            state = anEnum;
            this.instance = instance;
            Class<?> aClass = instance.getClass();
            for (GameState enumConstant : GameState.values()) {
                try {
                    Method method = aClass.getMethod(enumConstant.name().toLowerCase(), AtomicInteger.class);
                    methodMap.put(enumConstant.name(), method);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            for (Method method : aClass.getMethods()) {
                ChangeIn changeIn = method.getAnnotation(ChangeIn.class);
                if (changeIn != null) {
                    changeInMap.put(changeIn.value().name(), method);
                }
                ChangeOut changeOut = method.getAnnotation(ChangeOut.class);
                if (changeOut != null) {
                    changeOutMap.put(changeOut.value().name(), method);
                }
            }
            for (Field field : instance.getClass().getDeclaredFields()) {
                if (field.getType().equals(GameState.class)) {
                    State annotation = field.getAnnotation(State.class);
                    if (annotation != null) {
                        stateField = field;
                        stateField.setAccessible(true);
                        setStateField();
                        return;
                    }
                }
            }
        }

        private void changeState(GameState gameState) {
            if (this.state != gameState) {
                changeOut(this.state);
                changeIn(gameState);
            }
            this.state = gameState;
            setStateField();
        }

        private void changeIn(GameState gameState) {
            if (changeInMap.containsKey(gameState.name())) {
                try {
                    changeInMap.remove(gameState.name()).invoke(instance);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        private void changeOut(GameState gameState) {
            if (changeOutMap.containsKey(gameState.name())) {
                try {
                    changeOutMap.remove(gameState.name()).invoke(instance);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
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
