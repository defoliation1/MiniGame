package pers.defoliation.minigame.config.setup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pers.defoliation.minigame.MiniGame;
import pers.defoliation.minigame.conversation.Conversation;
import pers.defoliation.minigame.conversation.request.Request;
import pers.defoliation.minigame.ui.Slot;
import pers.defoliation.minigame.ui.UI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Setup {

    private static final List<String> defaultLore = Collections.singletonList("这个开发者很懒，什么也没写");
    private final AtomicInteger atomicInteger = new AtomicInteger(1);
    private List<SetupWrapper> list = new ArrayList<>();
    private String title = "Config Inventory";

    private UI ui;
    private Inventory inventory;

    public Setup() {
    }

    public Setup(String title) {
        this.title = title;
    }

    public void startSetup(Player player) {
        if (ui == null || list.size() > inventory.getSize()) {
            ui = new UI(inventory);
            inventory = Bukkit.createInventory(player, (list.size() + 8 / 9) * 9, title);
            ui.setAllowOperateInventory(false);

            for (int i = 0; i < inventory.getSize(); i++) {
                Slot slot = new Slot(i);
                slot.setOnClick(event -> {
                    event.getWhoClicked().closeInventory();
                    Conversation conversation = new Conversation(MiniGame.INSTANCE);
                    conversation.addRequest(list.get(slot.getSlot()).onClick);
                    conversation.start((Player) event.getWhoClicked());
                });
            }
            ui.setOnOpen((ui1, humanEntity) -> {
                for (SetupWrapper setupWrapper : list) {
                    ItemStack itemStack = new ItemStack(setupWrapper.itemId);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(getStateColor(setupWrapper.state) + setupWrapper.name);
                    List<String> lore = new ArrayList<>();
                    lore.addAll(setupWrapper.lore);
                    lore.add(" ");
                    lore.add("名字为§4红,意为还未设置，名字为§a绿，则为已设置");
                    lore.add("已设置的配置项可点击并重新设置");
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    inventory.setItem(setupWrapper.itemId - 1, itemStack);
                }
            });
        }
        ui.open(player);
    }

    private static String getStateColor(SetupState state) {
        switch (state) {
            case COMPLETE:
                return "§a";
            case NEED_SETUP:
                return "§4";
        }
        return "";
    }

    public Setup addConfig(String name, SetupState setupState, Request onClick) {
        return addConfig(name, defaultLore, setupState, onClick);
    }

    public Setup addConfig(String name, List<String> lore, SetupState state, Request onClick) {
        if (list.stream().filter(setupWrapper -> setupWrapper.name.equals(name)).findAny().isPresent()) {
            throw new IllegalArgumentException("配置项: " + name + " 已存在,不可重复添加");
        }
        list.add(new SetupWrapper(name, lore, state, onClick));
        return this;
    }

    public void setSetupState(String name, SetupState state) {
        list.stream()
                .filter(setupWrapper -> setupWrapper.name.equals(name))
                .findAny()
                .ifPresent(setupWrapper -> setupWrapper.state = state);
    }

    private class SetupWrapper {
        String name;
        int itemId = atomicInteger.getAndIncrement();
        List<String> lore;
        SetupState state;
        Request onClick;

        public SetupWrapper(String name, List<String> lore, SetupState state, Request onClick) {
            this.name = name;
            this.lore = lore;
            this.state = state;
            this.onClick = onClick;
        }
    }

    public enum SetupState {
        NEED_SETUP, COMPLETE
    }

}