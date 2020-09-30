package pers.defoliation.minigame.ui;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pers.defoliation.minigame.conversation.request.Request;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class RequestWithInfo {

    private final Request request;

    private final ItemStack itemStack;
    private final Supplier<String> title;
    private final Supplier<List<String>> desc;
    private final Supplier<Boolean> isComplete;

    public RequestWithInfo(Request request, ItemStack itemStack, Supplier<String> title, Supplier<List<String>> desc, Supplier<Boolean> isComplete) {
        this.request = request;
        this.title = title;
        this.itemStack = itemStack;
        this.desc = desc;
        this.isComplete = isComplete;
    }

    public Request getRequest() {
        return request;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getTitle() {
        return title.get();
    }

    public List<String> getDesc() {
        if (desc == null)
            return Arrays.asList("§r人懒无注释");
        return desc.get();
    }

    public boolean isComplete() {
        return isComplete.get();
    }

    public static RequestWithInfo wrap(Request request, Supplier<String> title, Supplier<Boolean> isComplete) {
        return wrap(request, new ItemStack(Material.STONE), title, null, isComplete);
    }

    public static RequestWithInfo wrap(Request request, Supplier<String> title, Supplier<List<String>> desc, Supplier<Boolean> isComplete) {
        return wrap(request, new ItemStack(Material.STONE), title, desc, isComplete);
    }

    public static RequestWithInfo wrap(Request request, ItemStack itemStack, Supplier<String> title, Supplier<Boolean> isComplete) {
        return wrap(request, itemStack, title, null, isComplete);
    }

    public static RequestWithInfo wrap(Request request, ItemStack itemStack, Supplier<String> title, Supplier<List<String>> desc, Supplier<Boolean> isComplete) {
        return new RequestWithInfo(request, itemStack, title, desc, isComplete);
    }

}
