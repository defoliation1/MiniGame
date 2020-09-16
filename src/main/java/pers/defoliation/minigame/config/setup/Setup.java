package pers.defoliation.minigame.config.setup;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import java.util.function.Supplier;

public class Setup {

    private static final List<String> defaultLore = Collections.singletonList("这个开发者很懒，什么也没写");
    private final AtomicInteger atomicInteger = new AtomicInteger(1);
    private List<SetupWrapper> list = new ArrayList<>();
    private String title = "Config Inventory";

    private UI ui;
    private Inventory inventory;
    private int initSize;

    public Setup() {
    }

    public Setup(String title) {
        this.title = title;
    }

    public void startSetup(Player player) {
        if (ui == null || list.size() != initSize) {
            initSize = list.size();
            int inventorySize = list.isEmpty() ? 9 : ((list.size() + 8) / 9) * 9;
            inventory = Bukkit.createInventory(null, inventorySize, title);
            ui = new UI(inventory);
            ui.setAllowOperateInventory(false);

            for (int i = 0; i < list.size(); i++) {
                Slot slot = new SetupSlot(i);
                ui.addSlot(slot);
                slot.setOnClick(event -> {
                    event.getWhoClicked().closeInventory();
                    Conversation conversation = new Conversation(MiniGame.INSTANCE);
                    conversation.addRequest(list.get(slot.getSlot()).onClick);
                    conversation.start((Player) event.getWhoClicked());
                });
            }
        }
        if (player == null)
            return;
        ui.open(player);
    }

    private class SetupSlot extends Slot {

        public SetupSlot(int slot) {
            super(slot);
        }

        @Override
        protected void updateItem() {
            itemStack = list.get(getSlot()).getItem();
            super.updateItem();
        }
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

    public Setup addConfig(String name, Supplier<SetupState> setupState, Request onClick) {
        return addConfig(name, () -> defaultLore, setupState, onClick);
    }

    public Setup addConfig(String name, Supplier<List<String>> lore, Supplier<SetupState> state, Request onClick) {
        if (list.stream().filter(setupWrapper -> setupWrapper.name.equals(name)).findAny().isPresent()) {
            throw new IllegalArgumentException("配置项: " + name + " 已存在,不可重复添加");
        }
        list.add(new SetupWrapper(name, lore, state, onClick));
        return this;
    }

    public void removeConfig(String name) {
        for (SetupWrapper setupWrapper : list) {
            if (setupWrapper.name.equals(name)) {
                list.remove(setupWrapper);
                return;
            }
        }
    }

    private class SetupWrapper {
        String name;
        int itemId;
        Supplier<List<String>> lore;
        Supplier<SetupState> stateSupplier;
        Request onClick;

        public SetupWrapper(String name, Supplier<List<String>> lore, Supplier<SetupState> stateSupplier, Request onClick) {
            this.name = name;
            this.lore = lore;
            this.stateSupplier = stateSupplier;
            this.onClick = onClick;
            setId();
        }

        public void setId() {
            int id = atomicInteger.getAndIncrement();
            while (!Material.getMaterial(id).isItem()) {
                id = atomicInteger.getAndIncrement();
            }
            itemId = id;
        }

        public ItemStack getItem() {
            ItemStack itemStack = new ItemStack(itemId);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(getStateColor(stateSupplier.get()) + name);
            List<String> lore = new ArrayList<>();
            lore.addAll(this.lore.get());
            lore.add(" ");
            lore.add("名字为§4红,意为还未设置，名字为§a绿，则为已设置");
            lore.add("已设置的配置项可点击并重新设置");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
    }

    public enum SetupState {
        NEED_SETUP, COMPLETE
    }

    public static Supplier<SetupState> notZero(Supplier<Integer> supplier) {
        return () -> supplier.get() == 0 ? SetupState.NEED_SETUP : SetupState.COMPLETE;
    }

    public static Supplier<SetupState> notNull(Supplier<Object> supplier) {
        return () -> supplier.get() == null ? SetupState.NEED_SETUP : SetupState.COMPLETE;
    }

    public static Supplier<SetupState> booleanState(Supplier<Boolean> supplier) {
        return () -> supplier.get() ? SetupState.COMPLETE : SetupState.NEED_SETUP;
    }

}
