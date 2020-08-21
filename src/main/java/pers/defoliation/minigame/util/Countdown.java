package pers.defoliation.minigame.util;

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

    private int tick;

    private Countdown() {
    }

    public void tick() {
        tick++;
        if (tick % 20 == 0) {
            second();
        }
    }

    public void second() {
        for (CountdownData countdownData : countdownDataList) {
            int i = countdownData.atomicInteger.incrementAndGet();
            if (i <= 0) {
                whenEnd.accept(countdownData.t);
            } else {
                perSecondTask.forEach(consumer -> consumer.accept(countdownData.t, countdownData.atomicInteger));
            }
        }
    }

    public void start(int countdownTime, T... t) {
        for (T t1 : t) {
            countdownDataList.add(new CountdownData(countdownTime, t1));
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

    public static <T> CountdownBuilder<T> getBuilder() {
        return new CountdownBuilder<T>();
    }

    public static class CountdownBuilder<T> {

        private Countdown<T> countdown;

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
