package pers.defoliation.minigame.util;

import org.bukkit.entity.Player;

import java.util.Collection;

public class Title {

    public final String title;
    public final String subTitle;
    public final int fadeIn;
    public final int stay;
    public final int fadeOut;

    private Title(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public void send(Player player) {
        player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
    }

    public void send(Collection<Player> players) {
        players.forEach(this::send);
    }

    public static Title as(String title, String subTitle) {
        return new Title(title, subTitle, 3, 15, 2);
    }

    public static Title as(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        return new Title(title, subTitle, fadeIn, stay, fadeOut);
    }

}
