package pers.defoliation.minigame.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Countdown<T> {

    private List<CountdownData> countdownDataList = new ArrayList<>();

    private Consumer<T> whenEnd;

    private List<BiConsumer<T, AtomicInteger>> perSecondTask = new ArrayList<>();

    private HashMap<Integer, BiConsumer<T, AtomicInteger>> secondTask = new HashMap<>();

    private Countdown(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, this::second, 20, 20);
    }

    public void second() {
        List<CountdownData> removeList = new ArrayList<>();
        for (CountdownData countdownData : countdownDataList) {
            if (countdownData.atomicInteger.incrementAndGet() <= 0) {
                whenEnd.accept(countdownData.t);
            } else {
                perSecondTask.forEach(consumer -> consumer.accept(countdownData.t, countdownData.atomicInteger));
            }
            if (countdownData.atomicInteger.get() <= 0) {
                removeList.add(countdownData);
            }
        }
        countdownDataList.remove(removeList);
    }

    public void start(int countdownTime, T... t) {
        for (T t1 : t) {
            countdownDataList.add(new CountdownData(countdownTime, t1));
        }
    }

    public void cancel(T t) {
        for (CountdownData countdownData : countdownDataList) {
            if(countdownData.t.equals(t)){
                countdownDataList.remove(countdownData);
                return;
            }
        }
    }

    public void start(int countdownTime, Collection<T> collection) {
        for (T t : collection) {
            countdownDataList.add(new CountdownData(countdownTime, t));
        }
    }

    private class CountdownData {
        AtomicInteger atomicInteger = new AtomicInteger();
        T t;

        public CountdownData(int time, T t) {
            atomicInteger.set(time);
            this.t = t;
        }
    }

    public static <T> CountdownBuilder<T> getBuilder(JavaPlugin plugin) {
        return new CountdownBuilder<T>(plugin);
    }

    public static class CountdownBuilder<T> {

        private Countdown<T> countdown;

        public CountdownBuilder(JavaPlugin plugin) {
            this.countdown = new Countdown<>(plugin);
        }

        public CountdownBuilder<T> whenEnd(Consumer<T> consumer) {
            countdown.whenEnd = consumer;
            return this;
        }

        public CountdownBuilder<T> addPerSecondTask(BiConsumer<T, AtomicInteger>... consumers) {
            for (BiConsumer<T, AtomicInteger> consumer : consumers) {
                countdown.perSecondTask.add(consumer);
            }
            return this;
        }

        public CountdownBuilder<T> setSecondTask(BiConsumer<T, AtomicInteger> consumer, int... seconds) {
            for (int second : seconds) {
                countdown.secondTask.put(second, consumer);
            }
            return this;
        }

        public Countdown<T> build() {
            return countdown;
        }

    }
}
