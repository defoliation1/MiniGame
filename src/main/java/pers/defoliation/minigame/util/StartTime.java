package pers.defoliation.minigame.util;

import pers.defoliation.minigame.group.GamePlayerGroup;

import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * 时间以tick计
 */
public class StartTime {

    private int countdown = Integer.MAX_VALUE;
    private Supplier<Integer> countdownTime;
    private Supplier<Boolean> canCountdown;
    private Runnable whenStart;

    public StartTime(Supplier<Integer> countdownTime, Supplier<Boolean> canCountdown, Runnable whenStart) {
        this.countdownTime = countdownTime;
        this.canCountdown = canCountdown;
        this.whenStart = whenStart;
    }

    public void resetCountdown() {
        countdown = this.countdownTime.get();
    }

    public void tick() {
        int countdownTime = this.countdownTime.get();
        if (canCountdown.get()) {
            countdown--;
            if (countdown > countdownTime)
                countdown = countdownTime;
            if (countdown <= 0)
                whenStart.run();
        } else {
            countdown = countdownTime;
        }
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
    public static StartTime time1(int countdownTime, int fullCountdownTime, GamePlayerGroup group, int startCountdownPlayerNum, Runnable runnable) {
        return new StartTime(() -> {
            if (group.size() == group.getTeams().stream().flatMapToInt(team -> IntStream.of(team.getMaxPlayer())).sum()) {
                return fullCountdownTime;
            } else {
                return countdownTime;
            }
        }, () -> group.size() >= startCountdownPlayerNum, runnable);
    }

}
