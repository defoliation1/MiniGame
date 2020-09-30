package pers.defoliation.minigame.ui;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import pers.defoliation.minigame.conversation.request.Request;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class RequestWithInfo {

    private final Request request;

    private final Material material;
    private final Supplier<String> title;
    private final Supplier<List<BaseComponent>> desc;
    private final Supplier<Boolean> isComplete;

    public RequestWithInfo(Request request, Material material, Supplier<String> title, Supplier<List<BaseComponent>> desc, Supplier<Boolean> isComplete) {
        this.request = request;
        this.title = title;
        this.material = material;
        this.desc = desc;
        this.isComplete = isComplete;
    }

    public Request getRequest() {
        return request;
    }

    public Material getMaterial() {
        return material;
    }

    public String getTitle() {
        return title.get();
    }

    public List<BaseComponent> getDesc() {
        if (desc == null)
            return Arrays.asList(new TextComponent("§r人懒无注释"));
        return desc.get();
    }

    public boolean isComplete() {
        return isComplete.get();
    }

    public static RequestWithInfo wrap(Request request, Material material, Supplier<String> title, Supplier<Boolean> isComplete) {
        return wrap(request, material, title, null, isComplete);
    }

    public static RequestWithInfo wrap(Request request, Material material, Supplier<String> title, Supplier<List<BaseComponent>> desc, Supplier<Boolean> isComplete) {
        return new RequestWithInfo(request, material, title, desc, isComplete);
    }

}
