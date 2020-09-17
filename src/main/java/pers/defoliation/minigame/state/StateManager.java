package pers.defoliation.minigame.state;

import org.bukkit.Bukkit;
import pers.defoliation.minigame.MiniGame;
import pers.defoliation.minigame.game.Game;
import pers.defoliation.minigame.game.GameState;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StateManager {

    private static List<InstanceWrapper> list = new ArrayList<>();

    private static List<Object> removeValue = new ArrayList<>();

    static {
        Bukkit.getScheduler().runTaskTimer(MiniGame.INSTANCE, () -> {
            list.removeIf(instanceWrapper -> removeValue.contains(instanceWrapper.instance));
            removeValue.clear();
            for (InstanceWrapper instanceWrapper : list) {
                Method method = instanceWrapper.methodMap.get(instanceWrapper.state);
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
        EnumMap<GameState, Method> methodMap = new EnumMap<>(GameState.class);
        EnumMap<GameState, Method> changeOutMap = new EnumMap<>(GameState.class);
        EnumMap<GameState, Method> changeInMap = new EnumMap<>(GameState.class);
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
                    methodMap.put(enumConstant, method);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            for (Method method : aClass.getMethods()) {
                ChangeIn changeIn = method.getAnnotation(ChangeIn.class);
                if (changeIn != null) {
                    changeInMap.put(changeIn.value(), method);
                }
                ChangeOut changeOut = method.getAnnotation(ChangeOut.class);
                if (changeOut != null) {
                    changeOutMap.put(changeOut.value(), method);
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

        private void changeState(GameState gameState) {
            if (this.state != gameState) {
                changeOut(this.state);
                changeIn(gameState);
            }
            this.state = gameState;
        }

        private void changeIn(GameState gameState) {
            if (changeInMap.containsKey(gameState)) {
                try {
                    changeInMap.remove(gameState).invoke(instance);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        private void changeOut(GameState gameState) {
            if (changeOutMap.containsKey(gameState)) {
                try {
                    changeOutMap.remove(gameState).invoke(instance);
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
