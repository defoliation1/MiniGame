package pers.defoliation.minigame.ui;

import org.bukkit.Material;
import pers.defoliation.minigame.conversation.request.Request;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class RequestWithInfo {

    private final Request request;

    private final Material material;
    private final Supplier<String> title;
    private final Supplier<List<String>> desc;

    public RequestWithInfo(Request request, Material material, Supplier<String> title, Supplier<List<String>> desc) {
        this.request = request;
        this.title = title;
        this.material = material;
        this.desc = desc;
    }

    public Request getRequest() {
        return request;
    }

    public String getTitle() {
        return title.get();
    }

    public List<String> getDesc() {
        if (desc == null)
            return Arrays.asList("§r人懒无解释");
        return desc.get();
    }

    public static RequestWithInfo wrap(Request request, Material material, Supplier<String> title) {
        return wrap(request, material, title, null);
    }

    public static RequestWithInfo wrap(Request request, Material material, Supplier<String> title, Supplier<List<String>> desc) {
        return new RequestWithInfo(request, material, title, desc);
    }

}
