package pers.defoliation.minigame.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import pers.defoliation.minigame.group.GamePlayerGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class Countdown {

    private int countdown = Integer.MAX_VALUE;
    private Supplier<Integer> countdownTime;
    private Supplier<Boolean> canCountdown;
    private Runnable whenZero;
    private List<Consumer<Integer>> perSecondTask = new ArrayList<>();
    private Multimap<Integer,Runnable> secondTask = HashMultimap.create();
    private int tick;

    public Countdown(Supplier<Integer> countdownTime, Supplier<Boolean> canCountdown, Runnable whenZero) {
        this.countdownTime = countdownTime;
        this.canCountdown = canCountdown;
        this.whenZero = whenZero;
    }

    public void resetCountdown() {
        countdown = this.countdownTime.get();
    }

    public void tick() {
        tick++;
        if (tick % 20 == 0) {
            second();
        }
    }

    public void addPerSecondTask(Consumer<Integer> secondConsumer) {
        perSecondTask.add(secondConsumer);
    }

    public void setSecondTask(int second,Runnable task){
        secondTask.put(second,task);
    }

    public void second() {
        int countdownTime = this.countdownTime.get();
        if (canCountdown.get()) {
            countdown--;
            if (countdown > countdownTime)
                countdown = countdownTime;
            if (countdown == 0)
                whenZero.run();
        } else {
            countdown = countdownTime;
        }
        perSecondTask.forEach(consumer-> consumer.accept(countdownTime));
        secondTask.get(countdownTime).forEach(Runnable::run);
    }

    /**
     * 策略1
     *
     * @param countdownTime           当玩家数量达到最低要求时的倒计时时间
     * @param fullCountdownTime       当玩家数量满时的倒计时时间
     * @param group                   用于判断的玩家群体
     * @param startCountdownPlayerNum 玩家数量的最低要求
     * @param runnable                倒计时完成后执行的任务
     * @return
     */
    public static Countdown speedUpWhenFull(int countdownTime, int fullCountdownTime, GamePlayerGroup group, int startCountdownPlayerNum, Runnable runnable) {
        return new Countdown(() -> {
            if (group.playerNum() == group.getTeams().stream().flatMapToInt(team -> IntStream.of(team.getMaxPlayer())).sum()) {
                return fullCountdownTime;
            } else {
                return countdownTime;
            }
        }, () -> group.playerNum() >= startCountdownPlayerNum, runnable);
    }

}
