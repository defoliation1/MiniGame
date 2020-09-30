package pers.defoliation.minigame.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import pers.defoliation.minigame.MiniGame;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlayerState {


    private List<Consumer<Player>> taskList = new ArrayList<>();

    private PlayerState() {
    }

    public void apply(Player player) {
        taskList.forEach(consumer -> consumer.accept(player));
    }

    public static PlayerStateBuilder getBuilder() {
        return new PlayerStateBuilder();
    }

    public static class PlayerStateBuilder {

        private PlayerState playerState = new PlayerState();

        public PlayerStateBuilder addTask(Consumer<Player> consumer) {
            playerState.taskList.add(consumer);
            return this;
        }

        public PlayerState build() {
            return playerState;
        }

    }

    public static Consumer<Player> clearInventory() {
        return player -> {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
        };
    }

    public static Consumer<Player> fullState() {
        return player -> {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setFireTicks(0);
            player.resetPlayerTime();
            player.resetPlayerWeather();
            player.setGlowing(false);
            player.setCollidable(true);
        };
    }

    public static Consumer<Player> clearPotionEffect() {
        return player -> {
            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(activePotionEffect.getType());
            }
        };
    }

    public static Consumer<Player> zeroExp() {
        return player -> {
            player.setExp(0);
            player.setLevel(0);
        };
    }

    public static Consumer<Player> hidePlayer() {
        return player -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.hidePlayer(MiniGame.INSTANCE, player);
            }
        };
    }

    public static Consumer<Player> showPlayer() {
        return player -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(MiniGame.INSTANCE, player);
            }
        };
    }


}
