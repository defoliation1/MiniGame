package pers.defoliation.minigame.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.entity.Player;
import pers.defoliation.minigame.group.GamePlayerGroup;
import pers.defoliation.minigame.group.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class Countdown {

    private int countdown = Integer.MAX_VALUE;
    private Supplier<Integer> countdownTime;
    private Supplier<Boolean> canCountdown;
    private List<Consumer<Integer>> perSecondTask = new ArrayList<>();
    private Multimap<Integer, Runnable> secondTask = HashMultimap.create();
    private int tick;

    public Countdown(Supplier<Integer> countdownTime, Supplier<Boolean> canCountdown) {
        this.countdownTime = countdownTime;
        this.canCountdown = canCountdown;
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

    public Countdown addPerSecondTask(Consumer<Integer> secondConsumer) {
        perSecondTask.add(secondConsumer);
        return this;
    }

    public Countdown setSecondTask(int second, Runnable task) {
        secondTask.put(second, task);
        return this;
    }

    public Countdown setSecondTask(Consumer<Integer> consumer, int... seconds) {
        if (seconds == null || seconds.length == 0)
            return this;
        Arrays.sort(seconds);
        addPerSecondTask(integer -> {
            if (Arrays.binarySearch(seconds, integer) > 0) {
                consumer.accept(integer);
            }
        });
        return this;
    }

    public void second() {
        int countdownTime = this.countdownTime.get();
        if (canCountdown.get()) {
            countdown--;
            if (countdown > countdownTime)
                countdown = countdownTime;
        } else {
            countdown = countdownTime;
        }
        perSecondTask.forEach(consumer -> consumer.accept(countdownTime));
        secondTask.get(countdownTime).forEach(Runnable::run);
    }

    /**
     * 策略1
     *
     * @param countdownTime           当玩家数量达到最低要求时的倒计时时间
     * @param fullCountdownTime       当玩家数量满时的倒计时时间
     * @param group                   用于判断的玩家群体
     * @param startCountdownPlayerNum 玩家数量的最低要求
     * @return
     */
    public static Countdown speedUpWhenFull(int countdownTime, int fullCountdownTime, GamePlayerGroup<? extends Team> group, int startCountdownPlayerNum) {
        return new Countdown(() -> {
            if (group.playerNum() == group.getTeams().stream().flatMapToInt(team -> IntStream.of(team.getMaxPlayer())).sum()) {
                return fullCountdownTime;
            } else {
                return countdownTime;
            }
        }, () -> group.playerNum() >= startCountdownPlayerNum);
    }

    public static Consumer<Integer> setLevel(Supplier<List<Player>> players) {
        return integer -> players.get().forEach(player -> player.setLevel(integer));
    }

    public static Consumer<Integer> sendTitle(Supplier<List<Player>> players, Function<Integer, Title> function) {
        return integer -> function.apply(integer).send(players.get());
    }

}
